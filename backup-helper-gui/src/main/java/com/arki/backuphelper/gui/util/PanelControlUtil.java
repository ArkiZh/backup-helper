package com.arki.backuphelper.gui.util;

import com.arki.backuphelper.base.entity.Difference;
import com.arki.backuphelper.base.utils.FileUtil;
import com.arki.backuphelper.gui.layiout.LayoutAbsoluteFrame;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PanelControlUtil {

    /**
     * Update result area according to synchronization results.
     * @param synchronizeResults The results indicate whether each item has been synchronized successfully.
     * @param currentJList Current JList on which the action is invoked.
     * @param theOtherJList The other JList.
     * @param currentDir Current directory in which the action is played.
     * @param theOtherDir The other directory.
     */
    public static void refreshResultAreaAfterSynchronize(boolean[] synchronizeResults,
                                                         LayoutAbsoluteFrame.DifferenceJList currentJList,
                                                         LayoutAbsoluteFrame.DifferenceJList theOtherJList,
                                                         String currentDir,
                                                         String theOtherDir) {

        List<Difference> oldDifferenceList = currentJList.getSelectedValuesList();

        List<Difference> successCopyed = new ArrayList<>();
        List<Difference> successCopyedConflicts = new ArrayList<>();
        for (int i = 0; i < synchronizeResults.length; i++) {
            if (synchronizeResults[i]) {
                Difference diff = oldDifferenceList.get(i);
                successCopyed.add(diff);
                if (diff.getCode() == Difference.DIFF_SIZE || diff.getCode() == Difference.DIFF_MD5) {
                    successCopyedConflicts.add(diff);
                }
            }
        }

        // Update current result area.
        if (successCopyed.size() > 0) {
            ListModel<Difference> model = currentJList.getModel();
            int oldSize = model.getSize();

            Difference[] remainedDifferences = new Difference[oldSize - successCopyed.size()];
            for (int i = 0, j = 0; i < oldSize; i++) {
                Difference oldElement = model.getElementAt(i);
                if (!successCopyed.contains(oldElement)) {
                    remainedDifferences[j++] = oldElement;
                }
            }

            currentJList.setListData(remainedDifferences);
            currentJList.repaint();
        }

        // Update the other result area.
        if (successCopyedConflicts.size() > 0) {
            String currentDirCanonicalPath = FileUtil.getCanonicalPath(new File(currentDir));

            List<String> correspondingTheOtherPath = new ArrayList<>();
            for (int i = 0; i < successCopyedConflicts.size(); i++) {
                String canonicalPath = successCopyedConflicts.get(i).getFileInfo().getCanonicalPath();
                correspondingTheOtherPath.add(FileUtil.getCanonicalPath(new File(theOtherDir, canonicalPath.substring(currentDirCanonicalPath.length()))));
            }

            ListModel<Difference> theOtherDifferences = theOtherJList.getModel();
            Difference[] originContents = new Difference[theOtherDifferences.getSize() - successCopyedConflicts.size()];
            for (int i = 0, j = 0; i < theOtherDifferences.getSize(); i++) {
                Difference theOtherElement = theOtherDifferences.getElementAt(i);
                if (!correspondingTheOtherPath.contains(theOtherElement.getFileInfo().getCanonicalPath())) {
                    originContents[j++] = theOtherElement;
                }
            }

            theOtherJList.setListData(originContents);
            theOtherJList.repaint();

        }
    }

}
