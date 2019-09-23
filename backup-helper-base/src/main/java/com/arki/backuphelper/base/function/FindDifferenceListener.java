package com.arki.backuphelper.base.function;

import com.arki.backuphelper.base.entity.Difference;

public interface FindDifferenceListener {
    void recordDiffenence(Difference difference);
}
