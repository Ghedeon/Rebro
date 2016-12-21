package com.ghedeon.rebro;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

class ListResponse {

    @Nullable
    private String connectionId;
    @Nullable
    private List<RTable> tables;
    private String dbName;

    @Nullable
    String getConnectionId() {
        return connectionId;
    }

    void setConnectionId(@NonNull final String connectionId) {
        this.connectionId = connectionId;
    }

    @Nullable
    List<RTable> getTables() {
        return tables;
    }

    void setTables(@NonNull final List<RTable> tables) {
        this.tables = tables;
    }

    String getDbName() {
        return dbName;
    }

    void setDbName(final String dbName) {
        this.dbName = dbName;
    }
}
