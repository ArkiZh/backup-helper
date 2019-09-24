package com.arki.backuphelper.base.function;

import com.arki.backuphelper.base.entity.Difference;
import com.arki.backuphelper.base.entity.FileInfo;
import com.arki.backuphelper.base.listener.RecordDifferenceListener;
import com.arki.backuphelper.base.listener.WarnInfoListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FolderCompare {

    private WarnInfoListener warnInfoListener;
    private RecordDifferenceListener recordDifferenceListener;
    public FolderCompare(WarnInfoListener warnInfoListener, RecordDifferenceListener recordDifferenceListener) {
        this.warnInfoListener = warnInfoListener;
        this.recordDifferenceListener = recordDifferenceListener;
    }

    /**
     * 第一次调用时候origin backup的文件名可不一样，之后递归调用时两者名字需确保一样。
     * 原因：第一次调用时可能只是为了比较两个不同名的文件md5是否一致，或者比较两个不同名字的文件夹中内容是否一样。
     * @param origin
     * @param backup
     * @param useSize
     * @param useMD5
     */
    public void compareFileInfo(FileInfo origin, FileInfo backup, boolean useSize, boolean useMD5) {

        if (!origin.sameType(backup)) {
            // origin and backup are not the same type.
            String warnInfo = "dir".equals(origin.getType())
                    ? "Warning: The origin is a directory while the backup is a file"
                    : "Warning: The origin is a file while the backup is a directory";
            this.warnInfoListener.showWarnInfo(warnInfo);
            return;
        }else{
            if ("file".equals(origin.getType())) {
                // Compare files
                if (useSize) {
                    if (origin.getSize() != backup.getSize()) {
                        // Find different size.
                        this.recordDifferenceListener.recordDiffenence(new Difference(origin, Difference.CAMP_ORIGIN, Difference.DIFF_SIZE));
                        this.recordDifferenceListener.recordDiffenence(new Difference(backup, Difference.CAMP_BACKUP, Difference.DIFF_SIZE));
                        return;
                    }
                }
                if (useMD5) {
                    if (!origin.getMd5().equals(backup.getMd5())) {
                        // Find different MD5.
                        this.recordDifferenceListener.recordDiffenence(new Difference(origin, Difference.CAMP_ORIGIN, Difference.DIFF_MD5));
                        this.recordDifferenceListener.recordDiffenence(new Difference(backup, Difference.CAMP_BACKUP, Difference.DIFF_MD5));
                        return;
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
                Map<ArrayList<String>, ArrayList<String>> compareMap = new HashMap<>();
                compareMap.put(originChildrenDir, backupChildrenDir);
                compareMap.put(originChildrenFile, backupChildrenFile);
                for (Map.Entry<ArrayList<String>, ArrayList<String>> entry : compareMap.entrySet()) {
                    ArrayList<String> originChildren = entry.getKey();
                    ArrayList<String> backupChildren = entry.getValue();
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
                            compareFileInfo(originChild, backupChild, useSize, useMD5);
                            // Remove the file of backup since it has been compared.
                            backupChildren.remove(hitIndex);
                        } else {
                            // Only the origin has this file. Record it.
                            this.recordDifferenceListener.recordDiffenence(new Difference(originChild, Difference.CAMP_ORIGIN, Difference.DIFF_REDUNDANT));
                        }
                    }
                    // After the compare according to name and remove, only the backup has these files.
                    for (int i = 0; i < backupChildren.size(); i++) {
                        FileInfo backupChild = new FileInfo(new File(backup.getCanonicalPath(), backupChildren.get(i)), backup, useSize, false, false);
                        this.recordDifferenceListener.recordDiffenence(new Difference(backupChild, Difference.CAMP_BACKUP, Difference.DIFF_REDUNDANT));
                    }
                }
            } else {
                throw new RuntimeException("Unexpected file type.");
            }
        }
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
