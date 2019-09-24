package com.arki.backuphelper.base.utils;


import com.arki.backuphelper.base.exception.BaseException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class FileUtil {


    public static String calculateMD5(File file) {
        String md5 = null;
        MessageDigest md5Instance;
        try {
             md5Instance = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new BaseException(e);
        }

        if (file.exists() && file.isFile()) {
            try {
                long a = System.currentTimeMillis();
                FileInputStream fis =  new FileInputStream(file);
                byte[] buffer = new byte[1024*1024];
                int length;
                while ((length = fis.read(buffer)) != -1) {
                    md5Instance.update(buffer, 0, length);
                }
                byte[] digest = md5Instance.digest();
                md5 = CommonUtil.encodeHex(digest, false);
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

    public static void deleteFileOrDir(File file) {
        if (file.exists()) {
            boolean isDirectory = file.isDirectory();
            if (isDirectory) {
                File[] children = file.listFiles();
                if (children != null) {
                    for (File child : children) {
                        deleteFileOrDir(child);
                    }
                }
            }
            if (file.delete()) {
                System.out.println((isDirectory ? "Directory" : "File") + " deleted: " + file.getAbsolutePath());
            } else {
                System.out.println((isDirectory ? "Directory" : "File") + " can't be deleted: " + file.getAbsolutePath());
            }
        } else {
            System.out.println("File not exist: " + file.getAbsolutePath());
        }
    }

    public static boolean[] copyFileByPath(String originDir, String backupDir, String[] filePaths, boolean[] overwriteFlag) {
        boolean[] results = new boolean[filePaths.length];
        for (int i = 0; i < filePaths.length; i++) {
            File originFile = new File(originDir, filePaths[i]);
            File targetFile = new File(backupDir, filePaths[i]);
            results[i] = copyFileOrDir(originFile, targetFile, new CopyOption(true, overwriteFlag[i]));
        }
        return results;
    }

    public static boolean copyFileOrDir(File origin, File target, CopyOption copyOption) {
        System.out.println("Copying: " + origin.getAbsolutePath() + " -> " + target.getAbsolutePath());
        if (origin.isDirectory()) {
            copyDirectory(origin, target, copyOption.copyDate, copyOption.overwrite);
        } else {
            copyFile(origin, target, copyOption.copyDate, copyOption.overwrite);
        }
        return true;
    }

    static class CopyOption {
        private boolean copyDate;
        private boolean overwrite;
        CopyOption(boolean copyDate, boolean overwrite) {
            this.copyDate = copyDate;
            this.overwrite = overwrite;
        }
    }

    public static void copyDirectory(File origin, File target, boolean copyDate, boolean overwrite){
        // Check origin directory.
        if (origin == null) {
            throw new BaseException("Origin directory should not be null.");
        }
        if (!origin.exists()) {
            throw new BaseException("Origin directory doesn't exist!");
        }
        if (!origin.isDirectory()) {
            throw new BaseException("Origin directory is not really a DIRECTORY!");
        }
        // Check target directory.
        if (target == null) {
            throw new BaseException("Target directory should not be null!");
        }

        String originCanonicalPath = getCanonicalPath(origin);
        String targetCanonicalPath = getCanonicalPath(target);
        if (targetCanonicalPath.startsWith(originCanonicalPath)) {
            throw new BaseException("Can't copy: The target dir is sub-dir of origin dir!");
        }
        boolean newDir=false;
        if (!target.exists() || !target.isDirectory()) {
            if (!target.mkdirs()) {
                throw new BaseException("Can't create directory: " + targetCanonicalPath);
            }
            System.out.println("Directory created: " + targetCanonicalPath);
            newDir = true;
        }
        // Copy content.
        File[] listFiles = origin.listFiles();
        if (listFiles != null) {
            for (File file : listFiles) {
                if (file.isDirectory()) {
                    copyDirectory(file, new File(target, file.getName()), copyDate, overwrite);
                } else {
                    copyFile(file, new File(target, file.getName()), copyDate, overwrite);
                }
            }
        } else {
            throw new BaseException("Failed to list directory: " + origin.getAbsolutePath());
        }

        if (newDir && copyDate) {
            target.setLastModified(origin.lastModified());
        }
    }

    public static void copyFile(File origin, File target, boolean copyDate, boolean overwrite){
        // Check origin file.
        if (!origin.exists()) {
            throw new BaseException("Origin file doesn't exist!");
        }
        if (!origin.isFile()) {
            throw new BaseException("Origin file is not really a FILE!");
        }
        // Check target file.
        if (target.exists() && target.isFile()) {
            if (overwrite) {
                if (target.canWrite()) {
                    doCopyFile(origin, target, copyDate);
                } else {
                    throw new BaseException("Have no privilege to write the target file: " + target.getAbsolutePath());
                }
            } else {
                System.out.println("Skip existing file: " + origin.getAbsolutePath() + " -> " + target.getAbsolutePath());
                return;
            }
        } else {
            if (!target.getParentFile().exists()) {
                boolean mkdirs = target.getParentFile().mkdirs();
                if (!mkdirs) {
                    throw new BaseException("Can't create dir: " + target.getParentFile().getAbsolutePath());
                }
                System.out.println("Directory created when copying file: " + target.getAbsolutePath());
            }
            doCopyFile(origin, target, copyDate);

        }

    }

    private static String getCanonicalPath(File file) {
        try {
            String canonicalPath = file.getCanonicalPath();
            return canonicalPath;
        } catch (IOException e) {
            e.printStackTrace();
            return file.getAbsolutePath();
        }
    }

    private static void doCopyFile(File origin, File target, boolean copyDate) {
        copyByBufferedStream(origin, target);
        System.out.println("File copied: " + origin.getAbsolutePath() + " -> " + target.getAbsolutePath());
        if (copyDate) {
            copyTimeAttributes(origin, target);
        }
    }

    private static void copyTimeAttributes(File origin, File target) {
        try {
            BasicFileAttributes originAttributes = Files.readAttributes(Paths.get(origin.getAbsolutePath()), BasicFileAttributes.class);
            FileTime creationTime = originAttributes.creationTime();
            FileTime lastModifiedTime = originAttributes.lastModifiedTime();
            FileTime lastAccessTime = originAttributes.lastAccessTime();
            Path targetPath = Paths.get(target.getAbsolutePath());
            /* Same as targetAttributeView.setTimes
            Files.setAttribute(targetPath, "creationTime", creationTime);
            Files.setAttribute(targetPath, "lastModifiedTime", lastModifiedTime);
            Files.setAttribute(targetPath, "lastAccessTime", lastAccessTime);*/
            BasicFileAttributeView targetAttributeView = Files.getFileAttributeView(targetPath, BasicFileAttributeView.class);
            targetAttributeView.setTimes(lastModifiedTime,lastAccessTime,creationTime);
        } catch (IOException e) {
            throw new BaseException(e);
        }
    }

    /**
     * Use BufferedStream to copy.
     * @param origin
     * @param target
     */
    private static void copyByBufferedStream(File origin, File target) {
        FileInputStream fis = null;
        BufferedInputStream in = null;
        FileOutputStream fos = null;
        BufferedOutputStream out = null;

        try {
            fis = new FileInputStream(origin);
            in = new BufferedInputStream(fis);
            fos = new FileOutputStream(target);
            out = new BufferedOutputStream(fos);
            int s = 8*1024*1024; // 1MB
            byte[] buf = new byte[s];
            int i;
            while ((i = in.read(buf)) != -1) {
                out.write(buf, 0, i);
            }
            in.close();
            out.close();
        } catch (IOException e) {
            try {
                if (in != null) {
                    in.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            try {
                if (out != null) {
                    out.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}
