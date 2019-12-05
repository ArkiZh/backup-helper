package com.arki.backuphelper.base.function;

import com.arki.backuphelper.base.entity.Difference;
import com.arki.backuphelper.base.entity.FileInfo;
import com.arki.backuphelper.base.callback.GuiCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FolderCompare {

    public enum CompareStatus{
        SUCCESS(1, "Comparision is done successfully."),
        INTERRUPTED_BY_INTENTION(-1, "The comparision is interrupted, according to the preconditions."),
        INTERRUPTED_UNEXPECTED(-2, "The comparision is interrupted unexpected!");

        private int code;
        private String info;

        CompareStatus(int code, String info) {
            this.code = code;
            this.info = info;
        }

        public int getCode() {
            return code;
        }

        public String getInfo() {
            return info;
        }
    }

    private GuiCallback<String> showWarnInfoCallback;
    private GuiCallback<String> showProcessInfoCallback;
    private GuiCallback<Difference> showDifferenceScannedCallback;
    public FolderCompare(Map<GuiCallback.RecordType, GuiCallback> guiCallBacks) {
        this.showWarnInfoCallback = guiCallBacks.get(GuiCallback.RecordType.WARN_INFO);
        this.showProcessInfoCallback = guiCallBacks.get(GuiCallback.RecordType.PROCESS_INFO);
        this.showDifferenceScannedCallback = guiCallBacks.get(GuiCallback.RecordType.DIFFERENCE_SCANNED);
    }

    /**
     * 第一次调用时候origin backup的文件名可不一样，之后递归调用时两者名字需确保一样。
     * 原因：第一次调用时可能只是为了比较两个不同名的文件md5是否一致，或者比较两个不同名字的文件夹中内容是否一样。
     * @param origin
     * @param backup
     * @param useSize
     * @param useMD5
     *
     * @return status of comparison
     *
     */
    public CompareStatus compareFileInfo(FileInfo origin, FileInfo backup, boolean useSize, boolean useMD5) {

        this.showProcessInfoCallback.record("Comparing: " + origin.getCanonicalPath() + " <=====> " + backup.getCanonicalPath());

        if (!origin.sameType(backup)) {
            // origin and backup are not the same type.
            String warnInfo = "dir".equals(origin.getType())
                    ? "Warning: The origin is a directory while the backup is a file"
                    : "Warning: The origin is a file while the backup is a directory";
            this.showWarnInfoCallback.record((warnInfo));
            return CompareStatus.INTERRUPTED_BY_INTENTION;
        }else{
            if ("file".equals(origin.getType())) {
                // Compare files
                if (useSize) {
                    if (origin.getSize() != backup.getSize()) {
                        // Find different size.
                        this.showDifferenceScannedCallback.record(new Difference(origin, Difference.CAMP_ORIGIN, Difference.DIFF_SIZE));
                        this.showDifferenceScannedCallback.record(new Difference(backup, Difference.CAMP_BACKUP, Difference.DIFF_SIZE));
                        return CompareStatus.SUCCESS;
                    }
                }
                if (useMD5) {
                    if (!origin.getMd5().equals(backup.getMd5())) {
                        // Find different MD5.
                        this.showDifferenceScannedCallback.record(new Difference(origin, Difference.CAMP_ORIGIN, Difference.DIFF_MD5));
                        this.showDifferenceScannedCallback.record(new Difference(backup, Difference.CAMP_BACKUP, Difference.DIFF_MD5));
                        return CompareStatus.SUCCESS;
                    }
                }
            } else if ("dir".equals(origin.getType())) {
                // Compare directories
                // Step 1: Separate each dir's children into dirs and files.
                ArrayList<String> originChildrenDir = new ArrayList<>();
                ArrayList<String> originChildrenFile = new ArrayList<>();
                separateFileAndDir(new File(origin.getCanonicalPath()), originChildrenDir, originChildrenFile);

                ArrayList<String> backupChildrenDir = new ArrayList<>();
                ArrayList<String> backupChildrenFile = new ArrayList<>();
                separateFileAndDir(new File(backup.getCanonicalPath()), backupChildrenDir, backupChildrenFile);

                // Step 2: Compare the same type of children.
                ArrayList<String>[][] compareArray = new ArrayList[2][2];
                compareArray[0][0] = originChildrenDir;
                compareArray[0][1] = backupChildrenDir;
                compareArray[1][0] = originChildrenFile;
                compareArray[1][1] = backupChildrenFile;
                for (ArrayList<String>[] pair : compareArray) {
                    ArrayList<String> originChildren = pair[0];
                    ArrayList<String> backupChildren = pair[1];
                    // Iterate children of origin.
                    for (int i = 0; i < originChildren.size(); i++) {
                        String originChildName = originChildren.get(i);
                        // Since this origin child may don't have the corresponding backup child, don't calculate md5 now. It takes much time to calculate.
                        FileInfo originChild = new FileInfo(new File(origin.getCanonicalPath(), originChildName), origin, useSize, false, false);
                        // Judge whether children of backup contains this origin child.
                        int hitIndex = backupChildren.indexOf(originChildName);
                        if (hitIndex >= 0) {
                            // Calculate md5 and set it into md5 field.
                            if (useMD5) {
                                originChild.calculateMd5();
                            }
                            // Compare the same named files.
                            FileInfo backupChild = new FileInfo(new File(backup.getCanonicalPath(), originChildName), backup, useSize, useMD5, false);
                            CompareStatus status = compareFileInfo(originChild, backupChild, useSize, useMD5);
                            if (status != CompareStatus.SUCCESS) {
                                return status;
                            }
                            // Remove the file of backup since it has been compared.
                            backupChildren.remove(hitIndex);
                        } else {
                            // Only the origin has this file. Record it.
                            this.showDifferenceScannedCallback.record(new Difference(originChild, Difference.CAMP_ORIGIN, Difference.DIFF_REDUNDANT));
                        }
                    }
                    // After the compare according to name and remove, only the backup has these files.
                    for (int i = 0; i < backupChildren.size(); i++) {
                        FileInfo backupChild = new FileInfo(new File(backup.getCanonicalPath(), backupChildren.get(i)), backup, useSize, false, false);
                        this.showDifferenceScannedCallback.record(new Difference(backupChild, Difference.CAMP_BACKUP, Difference.DIFF_REDUNDANT));
                    }
                }
            } else {
                throw new RuntimeException("Unexpected file type.");
            }
        }
        return CompareStatus.SUCCESS;
    }

    private void separateFileAndDir(File dirAsParent, List<String> dirList, List<String> fileList) {
        String[] nameArray = dirAsParent.list();
        if(nameArray==null) return;
        for (String s : nameArray) {
            if (new File(dirAsParent, s).isDirectory()) {
                dirList.add(s);
            } else {
                fileList.add(s);
            }
        }
    }
}
