package com.arki.backuphelper.gui.action;

import com.arki.backuphelper.base.entity.Difference;
import com.arki.backuphelper.base.utils.FileUtil;
import com.arki.backuphelper.gui.layiout.LayoutAbsoluteFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

public class BackupDeleteFilesItemAction implements ActionListener {
    private LayoutAbsoluteFrame frame;

    public BackupDeleteFilesItemAction(LayoutAbsoluteFrame frame) {
        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        List<Difference> differenceList = this.frame.getBackupResultList().getSelectedValuesList();
        for (Difference d : differenceList) {
            System.out.println("Deleting: " + d.getFileInfo().getCanonicalPath());
            FileUtil.deleteFileOrDir(new File(d.getFileInfo().getCanonicalPath()));
        }
    }
}
