package com.arki.backuphelper.gui.callback;

import com.arki.backuphelper.base.callback.GuiCallback;
import com.arki.backuphelper.gui.layiout.LayoutAbsoluteFrame;

public class WarnInfoCallback implements GuiCallback<String> {

    private final LayoutAbsoluteFrame frame;

    public WarnInfoCallback(LayoutAbsoluteFrame frame) {
        this.frame=frame;
    }

    @Override
    public void record(String warnInfo) {
        frame.getWarnInfoLabel().setText(warnInfo);
        //Rectangle bounds = frame.getWarnInfoLabel().getBounds();
        //frame.getWarnInfoLabel().paintImmediately(0, 0, bounds.width, bounds.height);
    }
}
