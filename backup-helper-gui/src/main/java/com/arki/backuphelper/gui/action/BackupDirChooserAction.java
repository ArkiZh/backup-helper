package com.arki.backuphelper.gui.action;

import com.arki.backuphelper.gui.layiout.LayoutAbsoluteFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BackupDirChooserAction implements ActionListener {

    private LayoutAbsoluteFrame frame;

    public BackupDirChooserAction(LayoutAbsoluteFrame frame) {
        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int state = this.frame.getBackupDirChooser().showOpenDialog(null);
        if (state == JFileChooser.APPROVE_OPTION) {
            this.frame.getBackupDirText().setText(this.frame.getBackupDirChooser().getSelectedFile().toString());
        }
    }
}
