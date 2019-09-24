package com.arki.backuphelper.gui.action;

import com.arki.backuphelper.gui.layiout.LayoutAbsoluteFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OriginDeleteFilesItemAction implements ActionListener {
    private LayoutAbsoluteFrame frame;
    public OriginDeleteFilesItemAction(LayoutAbsoluteFrame frame) {
        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO
        System.out.println("Delete files. TODO.");
    }
}
