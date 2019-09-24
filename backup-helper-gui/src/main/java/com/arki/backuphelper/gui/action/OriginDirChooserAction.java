package com.arki.backuphelper.gui.action;

import com.arki.backuphelper.gui.layiout.LayoutAbsoluteFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OriginDirChooserAction implements ActionListener {

    private LayoutAbsoluteFrame frame;

    public OriginDirChooserAction(LayoutAbsoluteFrame frame) {
        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int state = this.frame.getOriginDirChooser().showOpenDialog(null);
        if (state == JFileChooser.APPROVE_OPTION) {
            this.frame.getOriginDirText().setText(this.frame.getOriginDirChooser().getSelectedFile().toString());
        }
    }
}
