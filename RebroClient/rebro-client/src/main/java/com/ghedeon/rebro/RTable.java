package com.ghedeon.rebro;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Abstract representation of generic Realm table
 */
class RTable {

    @NonNull
    private String name;
    @NonNull
    private Object[][] values;
    @NonNull
    private String[] columnNames;
    @NonNull
    private RType[] columnTypes;
    @NonNull
    private String[] columnClasses;

    RTable(@NonNull final String name, final int columns, final int rows) {
        this.name = name;
        values = new Object[columns][rows];
        columnNames = new String[columns];
        columnTypes = new RType[columns];
        columnClasses = new String[columns];
    }

    @Nullable
    Object getValueAt(final int row, final int column) {
        return values[row][column];
    }

    void setValueAt(@Nullable final Object value, final int column, final int row) {
        values[column][row] = value;
    }

    @NonNull
    String getName() {
        return name;
    }

    void setName(@NonNull final String name) {
        this.name = name;
    }

    @NonNull
    Object[][] getValues() {
        return values;
    }

    void setValues(@NonNull final Object[][] values) {
        this.values = values;
    }

    @NonNull
    String[] getColumnNames() {
        return columnNames;
    }

    void setColumnNames(@NonNull final String[] strings) {
        columnNames = strings;
    }

    void setColumnName(final int i, @NonNull final String columnName) {
        columnNames[i] = columnName;
    }

    @NonNull
    RType[] getColumnTypes() {
        return columnTypes;
    }

    void setColumnTypes(@NonNull final RType[] columnTypes) {
        this.columnTypes = columnTypes;
    }

    void setColumnType(final int columnIdx, @NonNull final RType type) {
        columnTypes[columnIdx] = type;
    }

    @NonNull
    String[] getColumnClasses() {
        return columnClasses;
    }

    void setColumnClasses(@NonNull final String[] columnClasses) {
        this.columnClasses = columnClasses;
    }

    void setColumnClass(final int columnIdx, @NonNull final String name) {
        columnClasses[columnIdx] = name;
    }
}
