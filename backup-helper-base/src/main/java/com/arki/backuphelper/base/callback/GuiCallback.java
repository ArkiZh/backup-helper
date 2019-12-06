package com.arki.backuphelper.base.callback;

import java.util.List;

public interface GuiCallback<T> {

    enum RecordType {
        TIP_INFO, WARN_INFO, PROCESS_INFO, DIFFERENCE_SCANNED
    }

    void record(T content);

    void record(List<T> contents);

}
