package com.ghedeon.rebro;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

/**
 * public modifiers because of stupid error from json-smart in test fixture.
 */
public class ListResponse {

    @Nullable
    private String connectionId;
    @Nullable
    private List<RTable> tables;
    private String dbName;

    @Nullable
    public String getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(@NonNull final String connectionId) {
        this.connectionId = connectionId;
    }

    @Nullable
    public List<RTable> getTables() {
        return tables;
    }

    public void setTables(@NonNull final List<RTable> tables) {
        this.tables = tables;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(final String dbName) {
        this.dbName = dbName;
    }
}
