package com.arki.backuphelper.gui.callback;

import com.arki.backuphelper.base.callback.GuiCallback;
import com.arki.backuphelper.gui.layiout.LayoutAbsoluteFrame;

import java.util.List;

public class TipInfoCallback implements GuiCallback<String> {

    private final LayoutAbsoluteFrame frame;

    public TipInfoCallback(LayoutAbsoluteFrame frame) {
        this.frame = frame;
    }

    @Override
    public void record(String tipInfo) {
        frame.getTipLabel().setText(tipInfo);
    }

    @Override
    public void record(List<String> contents) {
        // Do nothing.
    }
}
