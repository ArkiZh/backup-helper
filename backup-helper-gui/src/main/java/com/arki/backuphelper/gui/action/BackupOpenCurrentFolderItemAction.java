package com.arki.backuphelper.gui.action;

import com.arki.backuphelper.base.entity.Difference;
import com.arki.backuphelper.gui.layiout.LayoutAbsoluteFrame;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BackupOpenCurrentFolderItemAction implements ActionListener {
    private final LayoutAbsoluteFrame frame;

    public BackupOpenCurrentFolderItemAction(LayoutAbsoluteFrame frame) {
        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Desktop desktop = Desktop.getDesktop();
        List<Difference> differenceList = this.frame.getBackupResultList().getSelectedValuesList();
        List<String> dirList = new ArrayList<>();
        for (Difference d : differenceList) {
            String dir = d.getFileInfo().getType().equals("dir")
                    ? d.getFileInfo().getCanonicalPath()
                    : new File(d.getFileInfo().getCanonicalPath()).getParent();
            if (!dirList.contains(dir)) {
                dirList.add(dir);
                try {
                    desktop.open(new File(dir));
                } catch (IOException e1) {
                    e1.printStackTrace();
                    System.out.println("Can't open directory: " + dir);
                }
            }
        }

    }
}
