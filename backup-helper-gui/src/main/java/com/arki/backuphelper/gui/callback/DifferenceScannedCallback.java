package com.arki.backuphelper.gui.callback;

import com.arki.backuphelper.base.entity.Difference;
import com.arki.backuphelper.base.callback.GuiCallback;
import com.arki.backuphelper.gui.layiout.LayoutAbsoluteFrame;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class DifferenceScannedCallback implements GuiCallback<Difference> {

    private final LayoutAbsoluteFrame frame;

    public DifferenceScannedCallback(LayoutAbsoluteFrame frame) {
        this.frame = frame;
    }

    @Override
    public void record(Difference difference) {
        ArrayList<Difference> differences = new ArrayList<>();
        differences.add(difference);
        record(differences);
    }

    @Override
    public void record(List<Difference> differences) {
        List<Difference> originCampDifferences = new ArrayList<>();
        List<Difference> backupCampDifferences = new ArrayList<>();
        for (int i = 0; i < differences.size(); i++) {
            Difference difference = differences.get(i);
            if (difference.getCamp() == Difference.CAMP_ORIGIN) {
                originCampDifferences.add(difference);
            } else if (difference.getCamp() == Difference.CAMP_BACKUP) {
                backupCampDifferences.add(difference);
            } else {
                throw new RuntimeException("Unexpected camp!");
            }
        }
        if (originCampDifferences.size() > 0) {
            updateResultList(originCampDifferences, frame.getOriginResultList());
        }
        if (backupCampDifferences.size() > 0) {
            updateResultList(backupCampDifferences, frame.getBackupResultList());
        }
    }

    private void updateResultList(List<Difference> differences, LayoutAbsoluteFrame.DifferenceJList jList) {

        ListModel<Difference> model = jList.getModel();
        int oldSize = model.getSize();
        Difference[] content = new Difference[oldSize + differences.size()];
        for (int i = 0; i < oldSize; i++) {
            content[i] = model.getElementAt(i);
        }
        for (int i = 0; i < differences.size(); i++) {
            content[i + oldSize] = differences.get(i);
        }
        jList.setListData(content);
        jList.repaint();
        //jList.paintImmediately(jList.getX(),jList.getY(), jList.getWidth(),jList.getHeight());
    }
}
