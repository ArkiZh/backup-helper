package com.arki.backuphelper.base.entity;


import com.arki.backuphelper.base.utils.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileInfo {

    public static void main(String[] args) {
        File file = new File("C:/360Downloads");
        FileInfo fileInfo = new FileInfo(file, null,true, true, false);
        System.out.println(fileInfo);
    }

    public FileInfo(File file) {
        this(file,null,false, false, false);
    }

    public FileInfo(File file,boolean sizeFlag) {
        this(file,null,sizeFlag, false, false);
    }

    public FileInfo(File file,boolean sizeFlag,boolean md5Flag) {
        this(file, null, sizeFlag, md5Flag, false);
    }

    public FileInfo(File file,boolean sizeFlag,boolean md5Flag,boolean childrenInfoFlag) {
        this(file, null, sizeFlag, md5Flag, childrenInfoFlag);
    }

    public FileInfo(File file,FileInfo parent,boolean sizeFlag,boolean md5Flag, boolean childrenInfoFlag){
        this.name = file.getName();
        int dotIndex = this.name.lastIndexOf('.');
        this.extension = (dotIndex==-1 || dotIndex+1==this.name.length()) ? "" :this.name.substring(dotIndex);
        this.parent = parent;
        try {
            this.canonicalPath = file.getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
            this.canonicalPath = file.getAbsolutePath();
        }
        this.exists = file.exists();
        if (!this.exists) {
            return;
        }
        this.type = file.isDirectory() ? "dir" : file.isFile() ? "entity" : "NotDirectoryNotFile";
        if ("entity".equals(this.type)) {
            if (sizeFlag) {
                this.size = file.length();
            }
            if (md5Flag) {
                this.md5 = FileUtil.calculateMD5(file);
            }
        }
        if ("dir".equals(this.type) && childrenInfoFlag) {
            String[] list = file.list();
            for (int i = 0; i < list.length; i++) {
                File child = new File(file, list[i]);
                children.add(new FileInfo(child, this, sizeFlag, md5Flag, childrenInfoFlag));
            }
        }
    }


    // entity name
    private String name;
    // extension name
    private String extension;

    private String canonicalPath;

    private boolean exists;
    // entity or dir
    private String type;
    // size in bit
    private long size;
    // md5 code
    private String md5;
    // children
    private List<FileInfo> children = new ArrayList<>();
    // parent
    private FileInfo parent;


    public boolean sameType(FileInfo fileInfo) {
        return this.getType().equals(fileInfo.getType());
    }



    public String getName() {
        return name;
    }

    public String getExtension() {
        return extension;
    }

    public String getCanonicalPath() {
        return canonicalPath;
    }

    public boolean isExists() {
        return exists;
    }

    public String getType() {
        if (!"entity".equals(this.type) && !"dir".equals(this.type)) {
            throw new RuntimeException("Unexpected entity type:" + this.type + " Path:" + this.getCanonicalPath());
        }
        return type;
    }

    public long getSize() {
        if ("dir".equals(this.getType()) && this.size == 0) {
            for (int i = 0; i < this.children.size(); i++) {
                this.size += this.children.get(i).getSize();
            }
        }
        return this.size;
    }

    public double getSizeInMB() {
        return this.getSize() / (1024 * 1024.0);
    }

    public String getMd5() {
        return md5;
    }

    public List<FileInfo> getChildren() {
        return children;
    }

    public FileInfo getParent() {
        return parent;
    }

    /**
     * It takes much time to calculate md5, use it only necessary.
     */
    public void calculateMd5() {
        this.md5 = FileUtil.calculateMD5(new File(this.getCanonicalPath()));
    }
}
