package com.arki.backuphelper.gui.callback;

import com.arki.backuphelper.base.callback.GuiCallback;
import com.arki.backuphelper.gui.layiout.LayoutAbsoluteFrame;

import javax.swing.*;
import java.util.List;

public class ProcessInfoCallback implements GuiCallback<String> {

    private final LayoutAbsoluteFrame frame;

    public ProcessInfoCallback(LayoutAbsoluteFrame frame) {
        this.frame = frame;
    }

    @Override
    public void record(String processInfo) {
        JLabel processInfoLabel = frame.getProcessInfoLabel();
        processInfoLabel.setText(processInfo);
        // processInfoLabel.paintImmediately(0, 0, processInfoLabel.getWidth(), processInfoLabel.getHeight());
    }

    @Override
    public void record(List<String> contents) {
        // Do nothing.
    }
}
