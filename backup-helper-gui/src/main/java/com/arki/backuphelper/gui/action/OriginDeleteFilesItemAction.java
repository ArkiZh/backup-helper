package com.arki.backuphelper.gui.action;

import com.arki.backuphelper.base.entity.Difference;
import com.arki.backuphelper.base.utils.FileUtil;
import com.arki.backuphelper.gui.layiout.LayoutAbsoluteFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class OriginDeleteFilesItemAction implements ActionListener {
    private LayoutAbsoluteFrame frame;
    public OriginDeleteFilesItemAction(LayoutAbsoluteFrame frame) {
        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        List<Difference> differenceList = this.frame.getOriginResultList().getSelectedValuesList();
        for (Difference d : differenceList) {
            System.out.println("Deleting: " + d.getFileInfo().getCanonicalPath());
            FileUtil.deleteFileOrDir(new File(d.getFileInfo().getCanonicalPath()));
        }
    }
}
