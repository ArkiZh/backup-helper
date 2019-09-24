package com.arki.backuphelper.gui.action;

import com.arki.backuphelper.base.entity.Difference;
import com.arki.backuphelper.base.entity.FileInfo;
import com.arki.backuphelper.base.function.FolderCompare;
import com.arki.backuphelper.gui.callback.RecordDifferenceCallback;
import com.arki.backuphelper.gui.callback.WarnInfoCallback;
import com.arki.backuphelper.gui.layiout.LayoutAbsoluteFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class ScanButtionAction implements ActionListener {

    private LayoutAbsoluteFrame frame;

    public ScanButtionAction(LayoutAbsoluteFrame frame) {
        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // init start options.
        initScanOptions();

        // start compare.
        JTextField originDirText = frame.getOriginDirText();
        String originPath = originDirText.getText();
        JTextField backupDirText = frame.getBackupDirText();
        String backupPath = backupDirText.getText();
        boolean useFileSizeFlag = frame.getFileSizeCheckbox().isSelected();
        boolean useFileMD5Flag = frame.getFileMd5Checkbox().isSelected();

        startCompare(originPath, backupPath,useFileSizeFlag,useFileMD5Flag);

        // reset start options.
        resetScanOptions();

    }

    private void initScanOptions() {
        frame.getOriginDirText().setEnabled(false);
        frame.getOriginDirChooserButton().setEnabled(false);
        frame.getBackupDirText().setEnabled(false);
        frame.getBackupDirChooserButton().setEnabled(false);
        frame.getFileSizeCheckbox().setEnabled(false);
        frame.getFileMd5Checkbox().setEnabled(false);
        frame.getScanButton().setEnabled(false);
        frame.getWarnInfoLabel().setText("");
        frame.getOriginResultList().setListData(new Difference[]{});
        frame.getBackupResultList().setListData(new Difference[]{});
    }
    private void resetScanOptions() {
        frame.getOriginDirText().setEnabled(true);
        frame.getOriginDirChooserButton().setEnabled(true);
        frame.getBackupDirText().setEnabled(true);
        frame.getBackupDirChooserButton().setEnabled(true);
        frame.getFileSizeCheckbox().setEnabled(true);
        frame.getFileMd5Checkbox().setEnabled(true);
        frame.getScanButton().setEnabled(true);
    }

    private void startCompare(String originPath, String backupPath, boolean useFileSizeFlag, boolean useFileMD5Flag) {
        JLabel warnInfoLabel = frame.getWarnInfoLabel();
        warnInfoLabel.setText("");
        String warnInfo = "";
        // Ensure dir not empty.
        if ("".equals(originPath.trim())) {
            warnInfo = "Please choose directory for: Origin directory";
        }
        if ("".equals(backupPath.trim())) {
            warnInfo = "".equals(warnInfo) ? "Please choose directory for: Backup directory" : warnInfo + " | Backup directory";
        }
        if (!"".equals(warnInfo)) {
            warnInfoLabel.setText(warnInfo);
            return;
        }


        File originFile = new File(originPath);
        File backupFile = new File(backupPath);

        // Ensure directory for exist.
        if (!originFile.exists()) {
            warnInfo = "Warning: File doesn't exist: " + originPath;
        }
        if (!backupFile.exists()) {
            warnInfo = "".equals(warnInfo) ? "Warning: File doesn't exist: " + backupPath : warnInfo + " | " + backupPath;
        }
        if (!"".equals(warnInfo)) {
            warnInfoLabel.setText(warnInfo);
            return;
        }

        // Start to compare:
        FileInfo originFileInfo = new FileInfo(originFile, useFileSizeFlag, useFileMD5Flag);
        FileInfo backupFileInfo = new FileInfo(backupFile, useFileSizeFlag, useFileMD5Flag);

        FolderCompare folderCompare = new FolderCompare(new WarnInfoCallback(this.frame), new RecordDifferenceCallback(this.frame));
        folderCompare.compareFileInfo(originFileInfo,backupFileInfo,useFileSizeFlag,useFileMD5Flag);
    }

}
