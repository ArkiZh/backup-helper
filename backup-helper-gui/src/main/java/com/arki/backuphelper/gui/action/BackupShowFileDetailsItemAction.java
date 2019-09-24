package com.arki.backuphelper.gui.action;

import com.arki.backuphelper.gui.layiout.LayoutAbsoluteFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BackupShowFileDetailsItemAction implements ActionListener {
    private final LayoutAbsoluteFrame frame;

    public BackupShowFileDetailsItemAction(LayoutAbsoluteFrame frame) {
        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO
        System.out.println("Backup show details. TODO.");

    }
}
