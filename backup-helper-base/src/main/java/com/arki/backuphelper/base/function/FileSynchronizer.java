package com.arki.backuphelper.base.function;

import com.arki.backuphelper.base.entity.Difference;
import com.arki.backuphelper.base.utils.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileSynchronizer {

    /**
     *
     * @param originDir The original dir.
     * @param targetDir The target dir.
     * @param differenceList Difference list.
     * @return An array of copy results. Same length as differenceList. true indicates success, false indicates failure.
     */
    public static boolean[] synchronizeFiles(String originDir, String targetDir, List<Difference> differenceList) {
        System.out.println("Synchronizing files: " + originDir + " -> " + targetDir);
        String originDirCanonical = FileUtil.getCanonicalPath(new File(originDir));
        String[] relativePaths = new String[differenceList.size()];
        boolean[] overwriteFlag = new boolean[differenceList.size()];
        for (int i = 0; i < differenceList.size(); i++) {
            Difference d = differenceList.get(i);
            overwriteFlag[i] = d.getCode() == Difference.DIFF_SIZE || d.getCode() == Difference.DIFF_MD5;
            String canonicalPath = d.getFileInfo().getCanonicalPath();
            String relativePath = canonicalPath.substring(originDirCanonical.length());
            System.out.println("File: " + relativePath);
            relativePaths[i] = relativePath;
        }
        return FileUtil.copyFileByPath(originDir, targetDir, relativePaths, overwriteFlag);
    }
}
