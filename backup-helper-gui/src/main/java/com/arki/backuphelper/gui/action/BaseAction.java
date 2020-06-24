package com.arki.backuphelper.gui.action;

import com.arki.backuphelper.gui.layiout.LayoutAbsoluteFrame;
import com.arki.backuphelper.gui.util.ThreadUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Callable;

public abstract class BaseAction  implements ActionListener {

    private LayoutAbsoluteFrame frame;
    private ThreadUtil.ThreadType threadType;

    public BaseAction(LayoutAbsoluteFrame frame, ThreadUtil.ThreadType threadType) {
        this.frame = frame;
        this.threadType = threadType;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        lockAssociatedResource();
        ThreadUtil.submitTask(new Callable<String>() {
            @Override
            public String call() {
                String retval = "DONE";
                try {
                    process();
                } catch (Exception e1) {
                    frame.getWarnInfoLabel().setText("ERROR: " + e.getClass().getName() + ": " + e1.getLocalizedMessage());
                    retval = "FAILED";
                    e1.printStackTrace();
                }
                releaseAssociatedResource();
                return retval;
            }
        }, this.threadType);
    }

    public abstract void lockAssociatedResource();
    public abstract void process();
    public abstract void releaseAssociatedResource();


    public LayoutAbsoluteFrame getFrame() {
        return frame;
    }
}
