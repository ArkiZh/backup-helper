package com.arki.backuphelper.gui.action;

import com.arki.backuphelper.gui.util.ThreadUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Future;

public class CancelButtonAction implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        Thread scanThread = ThreadUtil.getThread(ThreadUtil.ThreadType.SCAN);
        if (scanThread.isAlive()) {
            //Future futureTask = ThreadUtil.getFutureTask(ThreadUtil.ThreadType.SCAN);
            //futureTask.cancel(true);
            scanThread.stop();
        }
    }
}
