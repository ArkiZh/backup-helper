package com.arki.backuphelper.gui.layiout;

import com.arki.backuphelper.base.entity.Difference;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class LayoutAbsoluteFrame extends JFrame{


    private final GridBagConstraints constraints = new GridBagConstraints();
    private final JLabel originDirLabel = new JLabel("Origin directory:");
    private final JTextField originDirText = new JTextField(25);
    private final JFileChooser originDirChooser = new JFileChooser();
    private final JButton originDirChooserButton = new JButton("Choose");
    private final JLabel backupDirLabel = new JLabel("Backup directory:");
    private final JTextField backupDirText = new JTextField(25);
    private final JFileChooser backupDirChooser = new JFileChooser();
    private final JButton backupDirChooserButton = new JButton("Choose");
    private final JLabel optionLabel = new JLabel("Options:");
    private final JCheckBox fileNameCheckbox = new JCheckBox("Use file name check.");
    private final JCheckBox fileSizeCheckbox = new JCheckBox("Use file size check.");
    private final JCheckBox fileMd5Checkbox = new JCheckBox("Use MD5 check.");
    private final JLabel tipLabel = new JLabel("Tip: todo");
    private final JButton scanButton = new JButton("Scan");
    private final JButton cancelButton = new JButton("Cancel");
    private final JLabel warnInfoLabel = new JLabel();
    private final JLabel resultAreaLabel = new JLabel("Scan result:");
    private final JSeparator resultAreaSeparator = new JSeparator();
    // Origin directory contains these files, while backup directory doesn't contain:
    private final JLabel originResultInfoLabel = new JLabel("Backup lacking:");
    // Backup directory contains these files, while origin directory doesn't contain:
    private final JLabel backupResultInfoLabel = new JLabel("Backup redundant:");
    private final DifferenceJList originResultList = new DifferenceJList();
    private final JScrollPane originResultScrollPane = new JScrollPane();
    private final JPopupMenu originResultPopupMenu = new JPopupMenu();
    private final JMenuItem originSynchronizeFilesItem = new JMenuItem("Synchronize selected files to the other side");
    private final JMenuItem originOpenCurrentFolderItem = new JMenuItem("Open folders of selected files");
    private final JMenuItem originShowFileDetailsItem = new JMenuItem("Show details of selected files");
    private final JMenuItem originDeleteFilesItem = new JMenuItem("Delete selected files");
    private final DifferenceJList backupResultList = new DifferenceJList();
    private final JScrollPane backupResultScrollPane = new JScrollPane();
    private final JPopupMenu backupResultPopupMenu = new JPopupMenu();
    private final JMenuItem backupSynchronizeFilesItem = new JMenuItem("Synchronize selected files to the other side");
    private final JMenuItem backupOpenCurrentFolderItem = new JMenuItem("Open folders of selected files");
    private final JMenuItem backupShowFileDetailsItem = new JMenuItem("Show details of selected files");
    private final JMenuItem backupDeleteFilesItem = new JMenuItem("Delete selected files");

    public LayoutAbsoluteFrame() {

        JFrame frame = this;
        frame.setTitle("Folder Compare");
        setBoundsByCell(frame,15,5,45,27);
        frame.setBackground(new Color(200, 200, 200));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLayout(null);

        Map<Component, int[]> boundsMap = new HashMap<>();
        Map<int[],Component> boundsMapReverse = new HashMap<>();
        boundsMapReverse.put(new int[]{2, 2, 4, 1}, originDirLabel);
        boundsMapReverse.put(new int[]{6, 2, 11, 1}, originDirText);
        boundsMapReverse.put(new int[]{17, 2, 4, 1}, originDirChooserButton);
        boundsMapReverse.put(new int[]{24, 2, 4, 1},backupDirLabel);
        boundsMapReverse.put(new int[]{28, 2, 11, 1},backupDirText);
        boundsMapReverse.put(new int[]{39, 2, 4, 1}, backupDirChooserButton);

        boundsMapReverse.put(new int[]{2, 4, 4, 1}, optionLabel);
        boundsMapReverse.put(new int[]{6, 4, 6, 1}, fileNameCheckbox);
        boundsMapReverse.put(new int[]{13, 4, 6, 1}, fileSizeCheckbox);
        boundsMapReverse.put(new int[]{20, 4, 6, 1}, fileMd5Checkbox);

        boundsMapReverse.put(new int[]{2, 7, 19, 1}, tipLabel);
        boundsMapReverse.put(new int[]{28, 7, 4, 1}, scanButton);
        boundsMapReverse.put(new int[]{35, 7, 4, 1}, cancelButton);

        boundsMapReverse.put(new int[]{2, 9, 19, 1}, warnInfoLabel);

        boundsMapReverse.put(new int[]{2, 11, 4, 1}, resultAreaLabel);
        boundsMapReverse.put(new int[]{6, 12, 37, 1}, resultAreaSeparator);

        boundsMapReverse.put(new int[]{2, 12, 19, 1}, originResultInfoLabel);
        boundsMapReverse.put(new int[]{24, 12, 19, 1}, backupResultInfoLabel);

        boundsMapReverse.put(new int[]{2, 13, 19, 12}, originResultScrollPane);
        boundsMapReverse.put(new int[]{24, 13, 19, 12}, backupResultScrollPane);

        for (Map.Entry<int[], Component> e : boundsMapReverse.entrySet()) {
            int[] key = e.getKey();
            Component c = e.getValue();
            boundsMap.put(c, key);
            setBoundsByCell(c, key[0], key[1], key[2], key[3]);
            frame.add(c);
        }

        //------------------------ Row start------------------------------
        // Origin directory chooser.
        originDirChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        // Backup directory chooser.
        backupDirChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        //------------------------ Row end  ------------------------------


        //------------------------ Row start------------------------------
        fileNameCheckbox.setSelected(true);
        fileNameCheckbox.setEnabled(false);
        //------------------------ Row end  ------------------------------

        //------------------------ Row start------------------------------
        warnInfoLabel.setForeground(Color.RED);
        //------------------------ Row end  ------------------------------

        //------------------------ Row start------------------------------
        // Result area. Origin
        originResultList.setVisibleRowCount(20);
        originResultPopupMenu.add(originSynchronizeFilesItem);
        originResultPopupMenu.add(originOpenCurrentFolderItem);
        originResultPopupMenu.add(originShowFileDetailsItem);
        originResultPopupMenu.add(originDeleteFilesItem);
        originResultList.setComponentPopupMenu(originResultPopupMenu);

        originResultScrollPane.setViewportView(originResultList);
        originResultScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        originResultScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // Result area. Backup
        backupResultList.setVisibleRowCount(20);
        backupResultPopupMenu.add(backupSynchronizeFilesItem);
        backupResultPopupMenu.add(backupOpenCurrentFolderItem);
        backupResultPopupMenu.add(backupShowFileDetailsItem);
        backupResultPopupMenu.add(backupDeleteFilesItem);
        backupResultList.setComponentPopupMenu(backupResultPopupMenu);

        backupResultScrollPane.setViewportView(backupResultList);
        backupResultScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        backupResultScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        //------------------------ Row end  ------------------------------

    }

    private static <T extends Component> void setBoundsByCell(T c,int x, int y, int width, int height) {
        int cellWidth = 25;
        int cellHeight = 25;
        c.setBounds(x * cellWidth, y * cellHeight, width * cellWidth, height * cellHeight);
    }

    public GridBagConstraints getConstraints() {
        return constraints;
    }

    public JLabel getOriginDirLabel() {
        return originDirLabel;
    }

    public JTextField getOriginDirText() {
        return originDirText;
    }

    public JFileChooser getOriginDirChooser() {
        return originDirChooser;
    }

    public JButton getOriginDirChooserButton() {
        return originDirChooserButton;
    }

    public JLabel getBackupDirLabel() {
        return backupDirLabel;
    }

    public JTextField getBackupDirText() {
        return backupDirText;
    }

    public JFileChooser getBackupDirChooser() {
        return backupDirChooser;
    }

    public JButton getBackupDirChooserButton() {
        return backupDirChooserButton;
    }

    public JLabel getOptionLabel() {
        return optionLabel;
    }

    public JCheckBox getFileNameCheckbox() {
        return fileNameCheckbox;
    }

    public JCheckBox getFileSizeCheckbox() {
        return fileSizeCheckbox;
    }

    public JCheckBox getFileMd5Checkbox() {
        return fileMd5Checkbox;
    }

    public JLabel getTipLabel() {
        return tipLabel;
    }

    public JButton getScanButton() {
        return scanButton;
    }

    public JButton getCancelButton() {
        return cancelButton;
    }

    public JLabel getWarnInfoLabel() {
        return warnInfoLabel;
    }

    public JLabel getResultAreaLabel() {
        return resultAreaLabel;
    }

    public JSeparator getResultAreaSeparator() {
        return resultAreaSeparator;
    }

    public JLabel getOriginResultInfoLabel() {
        return originResultInfoLabel;
    }

    public JLabel getBackupResultInfoLabel() {
        return backupResultInfoLabel;
    }

    public DifferenceJList getOriginResultList() {
        return originResultList;
    }

    public JScrollPane getOriginResultScrollPane() {
        return originResultScrollPane;
    }

    public JPopupMenu getOriginResultPopupMenu() {
        return originResultPopupMenu;
    }

    public JMenuItem getOriginSynchronizeFilesItem() {
        return originSynchronizeFilesItem;
    }

    public JMenuItem getOriginShowFileDetailsItem() {
        return originShowFileDetailsItem;
    }

    public JMenuItem getOriginDeleteFilesItem() {
        return originDeleteFilesItem;
    }

    public DifferenceJList getBackupResultList() {
        return backupResultList;
    }

    public JScrollPane getBackupResultScrollPane() {
        return backupResultScrollPane;
    }

    public JPopupMenu getBackupResultPopupMenu() {
        return backupResultPopupMenu;
    }

    public JMenuItem getBackupSynchronizeFilesItem() {
        return backupSynchronizeFilesItem;
    }

    public JMenuItem getBackupShowFileDetailsItem() {
        return backupShowFileDetailsItem;
    }

    public JMenuItem getBackupDeleteFilesItem() {
        return backupDeleteFilesItem;
    }

    public JMenuItem getOriginOpenCurrentFolderItem() {
        return originOpenCurrentFolderItem;
    }

    public JMenuItem getBackupOpenCurrentFolderItem() {
        return backupOpenCurrentFolderItem;
    }

    public static class DifferenceJList extends JList<Difference> {
        public DifferenceJList() {
            super();
            initCellRender();
            initListener();
        }

        private void initListener() {

        }

        private void initCellRender() {
            //this.setCellRenderer(new MyCellRenderer());

            this.setCellRenderer(new ListCellRenderer<Difference>() {
                @Override
                public Component getListCellRendererComponent(JList<? extends Difference> list, Difference difference, int index, boolean isSelected, boolean cellHasFocus) {

                    JLabel jLabel = new JLabel(difference.getFileInfo().getCanonicalPath());
                    // Set item color according to selected, diff-type. Focus has no effect here.
                    if (isSelected){
                        jLabel.setBackground(Color.LIGHT_GRAY);
                    } else {
                        if (difference.getCode() == Difference.DIFF_SIZE) {
                            jLabel.setBackground(Color.PINK);
                        } else if (difference.getCode() == Difference.DIFF_MD5) {
                            jLabel.setBackground(Color.RED);
                        } else if ("dir".equals(difference.getFileInfo().getType())) {
                            jLabel.setBackground(Color.orange);
                        } else {
                            jLabel.setBackground(Color.WHITE);
                        }
                    }

                    // Opaque must be true, the default is false. Otherwise, you can't see any effects on it.
                    jLabel.setOpaque(true);
                    return jLabel;
                }
            });
        }
    }
}
