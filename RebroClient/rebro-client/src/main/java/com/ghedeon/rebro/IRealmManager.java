package com.ghedeon.rebro;

import android.support.annotation.NonNull;

import java.util.List;

interface IRealmManager {
    interface DataChangedListener {

        void onDataChanged();

    }

    @NonNull
    String getRealmFileName();

    @NonNull
    List<RTable> list();

    void setOnDataChangeListener(final DataChangedListener dataChangedListener);

    void close();
}
