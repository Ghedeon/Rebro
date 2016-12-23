package com.ghedeon.rebro;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmConfiguration;
import io.realm.RealmFieldType;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;
import io.realm.internal.Table;

final class RealmManager implements IRealmManager {

    @NonNull
    private final String realmFileName;
    private DynamicRealm dynamicRealm;
    private DataChangedListener dataChangedListener;
    private final Handler handler;

    RealmManager() {
        final RealmConfiguration configuration = Realm.getDefaultInstance().getConfiguration();
        realmFileName = configuration.getRealmFileName();
        dynamicRealm = DynamicRealm.getInstance(configuration);
        dynamicRealm.addChangeListener(new RealmChangeListener<DynamicRealm>() {
            @Override
            public void onChange(final DynamicRealm element) {
                dataChangedListener.onDataChanged();
            }
        });
        handler = new Handler();
    }

    @NonNull
    public String getRealmFileName() {
        return realmFileName;
    }

    public void setOnDataChangeListener(final DataChangedListener dataChangedListener) {
        this.dataChangedListener = dataChangedListener;
    }

    @NonNull
    public List<RTable> list() {
        final RealmSchema schema = dynamicRealm.getSchema();
        final List<RTable> RTables = new ArrayList<>(schema.getAll().size());

        for (final RealmObjectSchema objectSchema : schema.getAll()) {
            RTable RTable = listObjectSchema(objectSchema.getClassName(), schema);
            RTables.add(RTable);
        }

        return RTables;
    }

    //TODO validate the approach
    @Nullable
    private RTable listObjectSchema(@NonNull final String objectSchemaName, @NonNull final RealmSchema schema) {
        RTable rTable = null;
        try {
            final Method getTable = schema.getClass().getDeclaredMethod("getTable", String.class);
            getTable.setAccessible(true);
            final Table table = (Table) getTable.invoke(schema, objectSchemaName);

            //TODO: make table size long?
            rTable = new RTable(stripClassPrefix(table.getName()), (int) table.getColumnCount(), (int) table.size());

            for (int columnIdx = 0; columnIdx < table.getColumnCount(); columnIdx++) {
                rTable.setColumnName(columnIdx, table.getColumnName(columnIdx));

                final RType columnType = toRebroType(table.getColumnType(columnIdx));
                rTable.setColumnType(columnIdx, columnType);

                if (columnType == RType.OBJECT) {
                    final Table linkTarget = table.getLinkTarget(columnIdx);
                    rTable.setColumnClass(columnIdx, stripClassPrefix(linkTarget.getName()));
                }

                for (int rowIdx = 0; rowIdx < table.size(); rowIdx++) {
                    final Object value = getValue(table, columnIdx, rowIdx);
                    rTable.setValueAt(value, columnIdx, rowIdx);
                }
            }
        } catch (NoSuchMethodException e) { //todo: error handling
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return rTable;
    }

    @NonNull
    private static String stripClassPrefix(@NonNull final String name) {
        return name.replaceFirst("class_", "");
    }

    @Nullable
    private static Object getValue(@NonNull final Table table, final int columnIdx, final int rowIdx) {
        final RealmFieldType columnType = table.getColumnType(columnIdx);
        Object value = null;
        switch (columnType) {
            case BINARY:
                value = table.getBinaryByteArray(columnIdx, rowIdx);
                break;
            case BOOLEAN:
                value = table.getBoolean(columnIdx, rowIdx);
                break;
            case DATE:
                value = table.getDate(columnIdx, rowIdx);
                break;
            case DOUBLE:
                value = table.getDouble(columnIdx, rowIdx);
                break;
            case FLOAT:
                value = table.getFloat(columnIdx, rowIdx);
                break;
            case INTEGER:
                value = table.getLong(columnIdx, rowIdx);
                break;
            case STRING:
                value = table.getString(columnIdx, rowIdx);
                break;
            case OBJECT:
                value = table.getLink(columnIdx, rowIdx);
                break;
            default:
                //TODO throw an exception?
                break;
        }

        return value;
    }

    @NonNull
    private static RType toRebroType(@NonNull final RealmFieldType columnType) {
        RType type;

        switch (columnType) {
            case BINARY:
                type = RType.BINARY;
                break;
            case BOOLEAN:
                type = RType.BOOLEAN;
                break;
            case DATE:
                type = RType.DATE;
                break;
            case DOUBLE:
                type = RType.DOUBLE;
                break;
            case FLOAT:
                type = RType.FLOAT;
                break;
            case INTEGER:
                type = RType.INTEGER;
                break;
            case STRING:
                type = RType.STRING;
                break;
            case OBJECT:
                type = RType.OBJECT;
                break;
            default:
                type = RType.UNSUPPORTED;
                break;
        }

        return type;
    }

    public void close() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                dynamicRealm.removeAllChangeListeners();
                dynamicRealm.close();
            }
        });
    }
}
