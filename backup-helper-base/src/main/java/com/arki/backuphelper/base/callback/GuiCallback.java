package com.arki.backuphelper.base.callback;

public interface GuiCallback<T> {

    enum RecordType {
        TIP_INFO, WARN_INFO, PROCESS_INFO, DIFFERENCE_SCANNED
    }

    void record(T content);

}
