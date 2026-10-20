package com.uth.exa1par0212.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class AsignaturaDAO {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public AsignaturaDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public boolean insertAsignatura(String nombre) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ASIGNATURA_NOMBRE, nombre);
        long result = database.insertWithOnConflict(DatabaseHelper.TABLE_ASIGNATURAS, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        return result != -1;
    }

    public List<String> getAllAsignaturas() {
        List<String> asignaturas = new ArrayList<>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_ASIGNATURAS,
                new String[]{DatabaseHelper.COLUMN_ASIGNATURA_NOMBRE},
                null, null, null, null,
                DatabaseHelper.COLUMN_ASIGNATURA_NOMBRE + " ASC");

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                asignaturas.add(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ASIGNATURA_NOMBRE)));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return asignaturas;
    }
}
