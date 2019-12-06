package com.arki.backuphelper.gui.callback;

import com.arki.backuphelper.base.callback.GuiCallback;
import com.arki.backuphelper.gui.layiout.LayoutAbsoluteFrame;

import java.util.List;

public class WarnInfoCallback implements GuiCallback<String> {

    private final LayoutAbsoluteFrame frame;

    public WarnInfoCallback(LayoutAbsoluteFrame frame) {
        this.frame=frame;
    }

    @Override
    public void record(String warnInfo) {
        frame.getWarnInfoLabel().setText(warnInfo);
    }

    @Override
    public void record(List<String> contents) {
        // Do nothing.
    }
}
