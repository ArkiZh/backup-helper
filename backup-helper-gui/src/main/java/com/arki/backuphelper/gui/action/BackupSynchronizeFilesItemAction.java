package com.arki.backuphelper.gui.action;

import com.arki.backuphelper.base.entity.Difference;
import com.arki.backuphelper.base.function.FileSynchronizer;
import com.arki.backuphelper.base.utils.FileUtil;
import com.arki.backuphelper.gui.layiout.LayoutAbsoluteFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BackupSynchronizeFilesItemAction implements ActionListener {
    private LayoutAbsoluteFrame frame;
    public BackupSynchronizeFilesItemAction(LayoutAbsoluteFrame frame) {
        this.frame = frame;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        LayoutAbsoluteFrame.DifferenceJList backupResultList = this.frame.getBackupResultList();
        List<Difference> differenceList = backupResultList.getSelectedValuesList();
        String originDir = this.frame.getOriginDirText().getText();
        String backupDir = this.frame.getBackupDirText().getText();

        boolean[] results = FileSynchronizer.synchronizeFiles(backupDir, originDir, differenceList);

        // Update result area according to synchronization results.
        List<Difference> successCopyed = new ArrayList<>();
        List<Difference> successCopyedConflicts = new ArrayList<>();
        for (int i = 0; i < results.length; i++) {
            if (results[i]) {
                Difference diff = differenceList.get(i);
                successCopyed.add(diff);
                if (diff.getCode() == Difference.DIFF_SIZE || diff.getCode() == Difference.DIFF_MD5) {
                    successCopyedConflicts.add(diff);
                }
            }
        }

        // Update backup result area.
        if (successCopyed.size() > 0) {
            ListModel<Difference> model = backupResultList.getModel();
            int oldSize = model.getSize();

            Difference[] remainedDifferences = new Difference[oldSize - successCopyed.size()];
            for (int i = 0, j = 0; i < oldSize; i++) {
                Difference oldElement = model.getElementAt(i);
                if (!successCopyed.contains(oldElement)) {
                    remainedDifferences[j++] = oldElement;
                }
            }

            backupResultList.setListData(remainedDifferences);
            backupResultList.repaint();
        }

        // Update origin result area.
        if (successCopyedConflicts.size() > 0) {
            String backupDirCanonicalPath = FileUtil.getCanonicalPath(new File(backupDir));

            List<String> correspondingOriginPath = new ArrayList<>();
            for (int i = 0; i < successCopyedConflicts.size(); i++) {
                String canonicalPath = successCopyedConflicts.get(i).getFileInfo().getCanonicalPath();
                correspondingOriginPath.add(FileUtil.getCanonicalPath(new File(originDir, canonicalPath.substring(backupDirCanonicalPath.length()))));
            }

            LayoutAbsoluteFrame.DifferenceJList originResultList = frame.getOriginResultList();
            ListModel<Difference> originDifferences = originResultList.getModel();
            Difference[] originContents = new Difference[originDifferences.getSize() - successCopyedConflicts.size()];
            for (int i = 0, j = 0; i < originDifferences.getSize(); i++) {
                Difference originElement = originDifferences.getElementAt(i);
                if (!correspondingOriginPath.contains(originElement.getFileInfo().getCanonicalPath())) {
                    originContents[j++] = originElement;
                }
            }

            originResultList.setListData(originContents);
            originResultList.repaint();

        }
    }
}
