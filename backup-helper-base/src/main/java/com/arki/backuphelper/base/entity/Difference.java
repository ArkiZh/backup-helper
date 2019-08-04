package com.arki.backuphelper.base.entity;

public class Difference {
    public static final int CAMP_ORIGIN = 1;
    public static final int CAMP_BACKUP = 2;
    public static final int DIFF_REDUNDANT = 11;
    public static final int DIFF_SIZE = 12;
    public static final int DIFF_MD5 = 13;
    private FileInfo fileInfo;
    private int camp;
    private int code;

    public Difference(FileInfo fileInfo, int camp, int code) {
        this.fileInfo = fileInfo;
        this.camp = camp;
        this.code = code;
    }

    public FileInfo getFileInfo() {
        return fileInfo;
    }

    public void setFileInfo(FileInfo fileInfo) {
        this.fileInfo = fileInfo;
    }

    public int getCamp() {
        return camp;
    }

    public void setCamp(int camp) {
        this.camp = camp;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
