package com.arki.backuphelper.gui.callback;

import com.arki.backuphelper.base.listener.WarnInfoListener;
import com.arki.backuphelper.gui.layiout.LayoutAbsoluteFrame;

public class WarnInfoCallback implements WarnInfoListener {

    private final LayoutAbsoluteFrame frame;

    public WarnInfoCallback(LayoutAbsoluteFrame frame) {
        this.frame=frame;
    }

    @Override
    public void showWarnInfo(String warnInfo) {
        frame.getWarnInfoLabel().setText(warnInfo);
    }
}
