package com.arki.backuphelper.base.function;

import com.arki.backuphelper.base.entity.Difference;
import com.arki.backuphelper.base.entity.FileInfo;
import com.arki.backuphelper.base.callback.GuiCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FolderCompare {

    public enum CompareStatus {
        SUCCESS(1, "Comparision is done successfully."),
        INTERRUPTED_BY_CANCEL(0, "The comparision has been cancelled."),
        INTERRUPTED_BY_INTENTION(-1, "The comparision has been interrupted, according to the preconditions."),
        INTERRUPTED_UNEXPECTED(-2, "The comparision has been interrupted unexpected!"),
        ;

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
    private String originPath;
    private String backupPath;

    public FolderCompare(String originPath, String backupPath, Map<GuiCallback.RecordType, GuiCallback> guiCallBacks) {
        this.originPath = originPath;
        this.backupPath = backupPath;
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

        if (Thread.currentThread().isInterrupted()) {
            return CompareStatus.INTERRUPTED_BY_CANCEL;
        }

        this.showProcessInfoCallback.record("Comparing: " + origin.getCanonicalPath() + " <=====> " + backup.getCanonicalPath());
        try {

            if (!origin.sameType(backup)) {
                // origin and backup are not the same type.
                String warnInfo = "dir".equals(origin.getType())
                        ? "Warning: The origin is a directory while the backup is a file"
                        : "Warning: The origin is a file while the backup is a directory";
                this.showWarnInfoCallback.record((warnInfo));
                return CompareStatus.INTERRUPTED_BY_INTENTION;
            } else {
                if ("file".equals(origin.getType())) {
                    // Compare files
                    if (useSize) {
                        if (origin.getSize() != backup.getSize()) {
                            // Find different size.
                            List<Difference> sizeDifferences = new ArrayList<>();
                            sizeDifferences.add(new Difference(origin, Difference.CAMP_ORIGIN, Difference.DIFF_SIZE, this.originPath));
                            sizeDifferences.add(new Difference(backup, Difference.CAMP_BACKUP, Difference.DIFF_SIZE, this.backupPath));
                            this.showDifferenceScannedCallback.record(sizeDifferences);
                            return CompareStatus.SUCCESS;
                        }
                    }
                    if (useMD5) {
                        origin.calculateMd5();
                        backup.calculateMd5();
                        if (!origin.getMd5().equals(backup.getMd5())) {
                            // Find different MD5.
                            List<Difference> md5Differences = new ArrayList<>();
                            md5Differences.add(new Difference(origin, Difference.CAMP_ORIGIN, Difference.DIFF_MD5, this.originPath));
                            md5Differences.add(new Difference(backup, Difference.CAMP_BACKUP, Difference.DIFF_MD5, this.backupPath));
                            this.showDifferenceScannedCallback.record(md5Differences);
                            return CompareStatus.SUCCESS;
                        }
                    }
                } else if ("dir".equals(origin.getType())) {
                    // Compare directories
                    // Step 1: Separate each dir's children into dirs and files.
                    List<String> originChildrenDir = new ArrayList<>();
                    List<String> originChildrenFile = new ArrayList<>();
                    separateFileAndDir(new File(origin.getCanonicalPath()), originChildrenDir, originChildrenFile);

                    List<String> backupChildrenDir = new ArrayList<>();
                    List<String> backupChildrenFile = new ArrayList<>();
                    separateFileAndDir(new File(backup.getCanonicalPath()), backupChildrenDir, backupChildrenFile);

                    // Step 2: Compare the same type of children.
                    List<String>[][] compareArray = new ArrayList[2][2];
                    compareArray[0][0] = originChildrenDir;
                    compareArray[0][1] = backupChildrenDir;
                    compareArray[1][0] = originChildrenFile;
                    compareArray[1][1] = backupChildrenFile;

                    for (List<String>[] pair : compareArray) {
                        List<String> originChildren = pair[0];
                        List<String> backupChildren = pair[1];

                        List<String> originChildrenRedundant = new ArrayList<>();
                        List<String> childrenToCompare = new ArrayList<>();

                        // Iterate children of origin.
                        for (int i = 0; i < originChildren.size(); i++) {
                            String originChildName = originChildren.get(i);
                            // Judge whether children of backup contains this origin child.
                            int hitIndex = backupChildren.indexOf(originChildName);
                            if (hitIndex >= 0) {
                                // If contains, add to candidates for comparing.
                                // And remove from backupChildren, thus the backupChildren will only contain the redundant files.
                                childrenToCompare.add(backupChildren.remove(hitIndex));
                            } else {
                                originChildrenRedundant.add(originChildName);
                            }
                        }

                        // Record redundant differences.
                        List<Difference> redundantDifferences = new ArrayList<>();
                        for (int i = 0; i < originChildrenRedundant.size(); i++) {
                            FileInfo fileInfo = new FileInfo(new File(origin.getCanonicalPath(), originChildrenRedundant.get(i)), origin, useSize, false, false);
                            redundantDifferences.add(new Difference(fileInfo, Difference.CAMP_ORIGIN, Difference.DIFF_REDUNDANT, this.originPath));
                        }
                        for (int i = 0; i < backupChildren.size(); i++) {
                            FileInfo fileInfo = new FileInfo(new File(backup.getCanonicalPath(), backupChildren.get(i)), origin, useSize, false, false);
                            redundantDifferences.add(new Difference(fileInfo, Difference.CAMP_BACKUP, Difference.DIFF_REDUNDANT, this.backupPath));
                        }
                        this.showDifferenceScannedCallback.record(redundantDifferences);

                        // Compare children with the same name
                        for (int i = 0; i < childrenToCompare.size(); i++) {
                            String filename = childrenToCompare.get(i);
                            // Since this origin child may not have the corresponding backup child, don't calculate md5 now. It takes much time to calculate.
                            FileInfo originChild = new FileInfo(new File(origin.getCanonicalPath(), filename), origin, useSize, false, false);
                            FileInfo backupChild = new FileInfo(new File(backup.getCanonicalPath(), filename), backup, useSize, false, false);
                            CompareStatus status = compareFileInfo(originChild, backupChild, useSize, useMD5);
                            if (status != CompareStatus.SUCCESS) {
                                return status;
                            }
                        }
                    }
                } else {
                    throw new RuntimeException("Unexpected file type.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
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
