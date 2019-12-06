package com.arki.backuphelper.gui.action;

import com.arki.backuphelper.gui.util.ThreadUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CancelButtonAction implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        Thread scanThread = ThreadUtil.getThread(ThreadUtil.ThreadType.SCAN);
        if (scanThread!=null && scanThread.isAlive()) {
            scanThread.interrupt();
        }
    }
}
