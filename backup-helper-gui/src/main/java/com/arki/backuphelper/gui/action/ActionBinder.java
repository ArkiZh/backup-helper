package com.arki.backuphelper.gui.action;

import com.arki.backuphelper.gui.layiout.LayoutAbsoluteFrame;

public class ActionBinder {

    private LayoutAbsoluteFrame frame;

    public ActionBinder(LayoutAbsoluteFrame frame) {
        this.frame = frame;
    }

    public void init() {
        this.frame.getOriginDirChooserButton().addActionListener(new OriginDirChooserAction(this.frame));
        this.frame.getBackupDirChooserButton().addActionListener(new BackupDirChooserAction(this.frame));
        this.frame.getScanButton().addActionListener(new ScanButtionAction(this.frame));
        this.frame.getOriginSynchronizeFilesItem().addActionListener(new OriginSynchronizeFilesItemAction(this.frame));
        this.frame.getOriginShowFileDetailsItem().addActionListener(new OriginShowFileDetailsItemAction(this.frame));
        this.frame.getOriginDeleteFilesItem().addActionListener(new OriginDeleteFilesItemAction(this.frame));
        this.frame.getBackupSynchronizeFilesItem().addActionListener(new BackupSynchronizeFilesItemAction(this.frame));
        this.frame.getBackupShowFileDetailsItem().addActionListener(new BackupShowFileDetailsItemAction(this.frame));
        this.frame.getBackupDeleteFilesItem().addActionListener(new BackupDeleteFilesItemAction(this.frame));
        this.frame.setVisible(true);
    }
}
