package com.arki.backuphelper.gui.action.impl;

import com.arki.backuphelper.base.callback.GuiCallback;
import com.arki.backuphelper.base.entity.Difference;
import com.arki.backuphelper.base.entity.FileInfo;
import com.arki.backuphelper.base.function.FolderCompare;
import com.arki.backuphelper.base.utils.FileUtil;
import com.arki.backuphelper.gui.action.BaseAction;
import com.arki.backuphelper.gui.callback.DifferenceScannedCallback;
import com.arki.backuphelper.gui.callback.ProcessInfoCallback;
import com.arki.backuphelper.gui.callback.TipInfoCallback;
import com.arki.backuphelper.gui.callback.WarnInfoCallback;
import com.arki.backuphelper.gui.layiout.LayoutAbsoluteFrame;
import com.arki.backuphelper.gui.util.ThreadUtil;

import javax.swing.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ScanButtonAction extends BaseAction {

    public ScanButtonAction(LayoutAbsoluteFrame frame) {
        super(frame, ThreadUtil.ThreadType.SCAN);
    }

    @Override
    public void lockAssociatedResource() {
        // init start options.
        this.getFrame().getOriginDirText().setEnabled(false);
        this.getFrame().getOriginDirChooserButton().setEnabled(false);
        this.getFrame().getBackupDirText().setEnabled(false);
        this.getFrame().getBackupDirChooserButton().setEnabled(false);
        this.getFrame().getFileSizeCheckbox().setEnabled(false);
        this.getFrame().getFileMd5Checkbox().setEnabled(false);
        this.getFrame().getScanButton().setEnabled(false);
        this.getFrame().getWarnInfoLabel().setText("");
        this.getFrame().getOriginResultList().setListData(new Difference[]{});
        this.getFrame().getBackupResultList().setListData(new Difference[]{});
    }

    @Override
    public void process() {

        // start compare.
        JTextField originDirText = this.getFrame().getOriginDirText();
        String originPath = originDirText.getText();
        JTextField backupDirText = this.getFrame().getBackupDirText();
        String backupPath = backupDirText.getText();
        boolean useFileSizeFlag = this.getFrame().getFileSizeCheckbox().isSelected();
        boolean useFileMD5Flag = this.getFrame().getFileMd5Checkbox().isSelected();
        startCompare(originPath, backupPath, useFileSizeFlag, useFileMD5Flag);
    }

    @Override
    public void releaseAssociatedResource() {
        this.getFrame().getOriginDirText().setEnabled(true);
        this.getFrame().getOriginDirChooserButton().setEnabled(true);
        this.getFrame().getBackupDirText().setEnabled(true);
        this.getFrame().getBackupDirChooserButton().setEnabled(true);
        this.getFrame().getFileSizeCheckbox().setEnabled(true);
        this.getFrame().getFileMd5Checkbox().setEnabled(true);
        this.getFrame().getScanButton().setEnabled(true);
    }

    private void startCompare(String originPath, String backupPath, boolean useFileSizeFlag, boolean useFileMD5Flag) {
        JLabel warnInfoLabel = this.getFrame().getWarnInfoLabel();
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
            JLabel noticeLabel = this.getFrame().getNoticeLabel();
            noticeLabel.setText(noticeInfo);
        }
        originPath = originPathCanonical;
        backupPath = backupPathCanonical;

        // Ensure the origin's path and the backup's path are different.
        if (originPath.equals(backupPath)) {
            warnInfoLabel.setText("Warning: Origin and backup are the same path. No need to compare!");
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

        Map<GuiCallback.RecordType, GuiCallback> guiCallbacks = new HashMap<>();
        guiCallbacks.put(GuiCallback.RecordType.TIP_INFO, new TipInfoCallback(this.getFrame()));
        guiCallbacks.put(GuiCallback.RecordType.WARN_INFO, new WarnInfoCallback(this.getFrame()));
        guiCallbacks.put(GuiCallback.RecordType.PROCESS_INFO, new ProcessInfoCallback(this.getFrame()));
        guiCallbacks.put(GuiCallback.RecordType.DIFFERENCE_SCANNED, new DifferenceScannedCallback(this.getFrame()));

        FolderCompare folderCompare = new FolderCompare(originPath, backupPath, guiCallbacks);
        FolderCompare.CompareStatus compareStatus = folderCompare.compareFileInfo(originFileInfo, backupFileInfo, useFileSizeFlag, useFileMD5Flag);
        if ("dir".equals(originFileInfo.getType())) {
            this.getFrame().getTipLabel().setText("Tip: Result area shows the relative paths. PINK if different in size, and RED if different in MD5.");
        } else {
            this.getFrame().getTipLabel().setText("Tip: In result area, background color is PINK if different in size, and RED if different in MD5.");
        }

        this.getFrame().getProcessInfoLabel().setText(compareStatus.getInfo());
    }
}
