package com.arki.backuphelper.gui.callback;

import com.arki.backuphelper.base.entity.Difference;
import com.arki.backuphelper.base.callback.GuiCallback;
import com.arki.backuphelper.gui.layiout.LayoutAbsoluteFrame;

import javax.swing.*;

public class DifferenceScannedCallback implements GuiCallback<Difference> {

    private final LayoutAbsoluteFrame frame;

    public DifferenceScannedCallback(LayoutAbsoluteFrame frame) {
        this.frame = frame;
    }

    @Override
    public void record(Difference difference) {
        LayoutAbsoluteFrame.DifferenceJList jList;
        int camp = difference.getCamp();
        if (camp == Difference.CAMP_ORIGIN) {
            jList = frame.getOriginResultList();
        } else if (camp == Difference.CAMP_BACKUP) {
            jList = frame.getBackupResultList();
        } else {
            throw new RuntimeException("Unexpected camp!");
        }
        ListModel<Difference> model = jList.getModel();
        Difference[] content = new Difference[model.getSize() + 1];
        for (int i = 0; i < model.getSize(); i++) {
            content[i] = model.getElementAt(i);
        }
        content[model.getSize()] = difference;
        jList.setListData(content);

    }
}
