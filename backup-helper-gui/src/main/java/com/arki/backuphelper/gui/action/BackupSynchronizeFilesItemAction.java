package com.arki.backuphelper.gui.action;

import com.arki.backuphelper.base.entity.Difference;
import com.arki.backuphelper.base.function.FileSynchronizer;
import com.arki.backuphelper.gui.layiout.LayoutAbsoluteFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class BackupSynchronizeFilesItemAction implements ActionListener {
    private LayoutAbsoluteFrame frame;
    public BackupSynchronizeFilesItemAction(LayoutAbsoluteFrame frame) {
        this.frame = frame;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        List<Difference> differenceList = this.frame.getBackupResultList().getSelectedValuesList();
        String originDir = this.frame.getOriginDirText().getText();
        String backupDir = this.frame.getBackupDirText().getText();

        FileSynchronizer.synchronizeFiles(backupDir, originDir, differenceList);
    }
}
