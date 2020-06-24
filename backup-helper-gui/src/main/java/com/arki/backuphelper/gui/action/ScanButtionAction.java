package com.arki.backuphelper.gui.action;

import com.arki.backuphelper.base.callback.GuiCallback;
import com.arki.backuphelper.base.entity.Difference;
import com.arki.backuphelper.base.entity.FileInfo;
import com.arki.backuphelper.base.function.FolderCompare;
import com.arki.backuphelper.base.utils.FileUtil;
import com.arki.backuphelper.gui.callback.DifferenceScannedCallback;
import com.arki.backuphelper.gui.callback.ProcessInfoCallback;
import com.arki.backuphelper.gui.callback.TipInfoCallback;
import com.arki.backuphelper.gui.callback.WarnInfoCallback;
import com.arki.backuphelper.gui.layiout.LayoutAbsoluteFrame;
import com.arki.backuphelper.gui.util.ThreadUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

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
        final String originPath = originDirText.getText();
        JTextField backupDirText = frame.getBackupDirText();
        final String backupPath = backupDirText.getText();
        final boolean useFileSizeFlag = frame.getFileSizeCheckbox().isSelected();
        final boolean useFileMD5Flag = frame.getFileMd5Checkbox().isSelected();

        ThreadUtil.submitTask(new Callable<String>() {
            @Override
            public String call() {
                startCompare(originPath, backupPath, useFileSizeFlag, useFileMD5Flag);
                resetScanOptions();
                return "DONE";
            }
        }, ThreadUtil.ThreadType.SCAN);

        // reset start options.

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
            warnInfo = "Warning: Please choose directory for: Origin directory";
        }
        if ("".equals(backupPath.trim())) {
            warnInfo = "".equals(warnInfo) ? "Warning: Please choose directory for: Backup directory" : warnInfo + " | Backup directory";
        }
        if (!"".equals(warnInfo)) {
            warnInfoLabel.setText(warnInfo);
            return;
        }

        // Ensure path to be standard. Show the change in noticeLabel if different.
        String originPathCanonical = FileUtil.getCanonicalPath(new File(originPath));
        String backupPathCanonical = FileUtil.getCanonicalPath(new File(backupPath));
        String noticeInfo = "";
        if (!originPath.equals(originPathCanonical)) {
            noticeInfo = "Notice: Convert original path to " + originPathCanonical;
        }
        if (!backupPath.equals(backupPathCanonical)) {
            noticeInfo = "".equals(noticeInfo) ? "Notice: Convert backup path to " + backupPathCanonical : noticeInfo + " | backup path to " + backupPathCanonical;
        }
        if (!"".equals(noticeInfo)) {
            JLabel noticeLabel = frame.getNoticeLabel();
            noticeLabel.setText(noticeInfo);
        }
        originPath = originPathCanonical;
        backupPath = backupPathCanonical;

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

        Map<GuiCallback.RecordType, GuiCallback> guiCallbacks = new HashMap<>();
        guiCallbacks.put(GuiCallback.RecordType.TIP_INFO, new TipInfoCallback(frame));
        guiCallbacks.put(GuiCallback.RecordType.WARN_INFO, new WarnInfoCallback(frame));
        guiCallbacks.put(GuiCallback.RecordType.PROCESS_INFO, new ProcessInfoCallback(frame));
        guiCallbacks.put(GuiCallback.RecordType.DIFFERENCE_SCANNED, new DifferenceScannedCallback(frame));

        FolderCompare folderCompare = new FolderCompare(originPath, backupPath, guiCallbacks);
        FolderCompare.CompareStatus compareStatus = folderCompare.compareFileInfo(originFileInfo, backupFileInfo, useFileSizeFlag, useFileMD5Flag);
        if ("dir".equals(originFileInfo.getType())) {
            frame.getTipLabel().setText("Tip: Result area shows the relative paths. PINK if different in size, and RED if different in MD5.");
        } else {
            frame.getTipLabel().setText("Tip: In result area, background color is PINK if different in size, and RED if different in MD5.");
        }

        frame.getProcessInfoLabel().setText(compareStatus.getInfo());
    }

}
