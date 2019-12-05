package com.arki.backuphelper.gui.callback;

import com.arki.backuphelper.base.callback.GuiCallback;
import com.arki.backuphelper.gui.layiout.LayoutAbsoluteFrame;

public class ProcessInfoCallback implements GuiCallback<String> {

    private final LayoutAbsoluteFrame frame;

    public ProcessInfoCallback(LayoutAbsoluteFrame frame) {
        this.frame = frame;
    }

    @Override
    public void record(String processInfo) {
        frame.getProcessInfoLabel().setText(processInfo);
    }
}
