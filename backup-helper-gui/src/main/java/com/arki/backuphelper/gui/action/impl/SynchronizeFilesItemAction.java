package com.arki.backuphelper.gui.action.impl;

import com.arki.backuphelper.base.entity.Difference;
import com.arki.backuphelper.base.function.FileSynchronizer;
import com.arki.backuphelper.gui.action.BaseAction;
import com.arki.backuphelper.gui.layiout.LayoutAbsoluteFrame;
import com.arki.backuphelper.gui.util.PanelControlUtil;
import com.arki.backuphelper.gui.util.ThreadUtil;

import java.util.List;

public abstract class SynchronizeFilesItemAction extends BaseAction {

    public SynchronizeFilesItemAction(LayoutAbsoluteFrame frame) {
        super(frame, ThreadUtil.ThreadType.SYNCHRONIZE);
    }

    @Override
    public void lockAssociatedResource() {
        this.getFrame().getOriginDirText().setEnabled(false);
        this.getFrame().getOriginDirChooserButton().setEnabled(false);
        this.getFrame().getBackupDirText().setEnabled(false);
        this.getFrame().getBackupDirChooserButton().setEnabled(false);
        this.getFrame().getFileSizeCheckbox().setEnabled(false);
        this.getFrame().getFileMd5Checkbox().setEnabled(false);
        this.getFrame().getScanButton().setEnabled(false);

    }

    @Override
    public void releaseAssociatedResource() {
        this.getFrame().getOriginDirText().setEnabled(true);
        this.getFrame().getOriginDirChooserButton().setEnabled(true);
        this.getFrame().getBackupDirText().setEnabled(true);
        this.getFrame().getBackupDirChooserButton().setEnabled(true);
        this.getFrame().getFileSizeCheckbox().setEnabled(true);
        this.getFrame().getFileMd5Checkbox().setEnabled(true);
        this.getFrame().getScanButton().setEnabled(true);
    }

    public static class BackupSynchronizeFilesItemAction extends SynchronizeFilesItemAction {

        public BackupSynchronizeFilesItemAction(LayoutAbsoluteFrame frame) {
            super(frame);
        }

        @Override
        public void process() {

            LayoutAbsoluteFrame.DifferenceJList backupResultList = this.getFrame().getBackupResultList();
            LayoutAbsoluteFrame.DifferenceJList originResultList = this.getFrame().getOriginResultList();
            String originDir = this.getFrame().getOriginDirText().getText();
            String backupDir = this.getFrame().getBackupDirText().getText();

            List<Difference> differenceList = backupResultList.getSelectedValuesList();
            boolean[] results = FileSynchronizer.synchronizeFiles(backupDir, originDir, differenceList);

            PanelControlUtil.refreshResultAreaAfterSynchronize(results, backupResultList, originResultList, backupDir, originDir);
        }
    }


    public static class OriginSynchronizeFilesItemAction extends SynchronizeFilesItemAction {

        public OriginSynchronizeFilesItemAction(LayoutAbsoluteFrame frame) {
            super(frame);
        }

        @Override
        public void process() {

            LayoutAbsoluteFrame.DifferenceJList backupResultList = this.getFrame().getBackupResultList();
            LayoutAbsoluteFrame.DifferenceJList originResultList = this.getFrame().getOriginResultList();
            String originDir = this.getFrame().getOriginDirText().getText();
            String backupDir = this.getFrame().getBackupDirText().getText();

            List<Difference> differenceList = originResultList.getSelectedValuesList();
            boolean[] results = FileSynchronizer.synchronizeFiles(originDir, backupDir, differenceList);

            PanelControlUtil.refreshResultAreaAfterSynchronize(results, originResultList, backupResultList, originDir, backupDir);
        }
    }

}
