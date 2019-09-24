package com.arki.backuphelper.gui;

import com.arki.backuphelper.gui.action.ActionBinder;
import com.arki.backuphelper.gui.layiout.LayoutAbsoluteFrame;

public class App {
    public static void main(String[] args) {
        new ActionBinder(new LayoutAbsoluteFrame()).init();
    }
}
