package com.arki.backuphelper.base.utils;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


public class FileUtil {


    public static String calculateMD5(File file) {
        String md5 = null;
        if (file.exists() && file.isFile()) {
            try {
                long a = System.currentTimeMillis();
                FileInputStream fis =  new FileInputStream(file);
                md5 = ""; // TODO DigestUtils.md2Hex(fis)
                long b = System.currentTimeMillis() - a;
                if (b > 1000) {
                    System.out.println("MD5 cost ms: " + b + " Size(MB):" + file.length() / (1024 * 1024.0) + " Path: " + file.getCanonicalPath());
                }
                fis.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return md5;
    }

    public static boolean[] copyFileByPath(String originDir, String backupDir, String[] filePaths) {
        boolean[] results = new boolean[filePaths.length];
        for (int i = 0; i < filePaths.length; i++) {
            File originFile = new File(originDir, filePaths[i]);
            File targetFile = new File(backupDir, filePaths[i]);
            results[i] = copyFileOrDir(originFile, targetFile, new CopyOption(true));
        }
        return results;
    }

    public static boolean copyFileOrDir(File origin, File target, CopyOption copyOption) {
        System.out.println("Copy from " + origin.getAbsolutePath() + " to " + target.getAbsolutePath());
        try {
            if (origin.isDirectory()) {
                copyDirectory(origin, target, copyOption.copyDate);
            } else {
                copyFile(origin, target, copyOption.copyDate);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    static class CopyOption {
        private boolean copyDate;
        CopyOption(boolean copyDate) {
            this.copyDate = copyDate;
        }
    }

    public static void copyDirectory(File origin, File target, boolean copyDate) throws IOException {
        // TODO
        throw new IOException();

    }

    public static void copyFile(File oririn, File target, boolean copyDate) throws IOException {
        // TODO
        throw new IOException();
    }

}
