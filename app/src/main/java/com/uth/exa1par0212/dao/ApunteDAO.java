package com.uth.exa1par0212.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.uth.exa1par0212.model.Apunte;

import java.util.ArrayList;
import java.util.List;

public class ApunteDAO {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public ApunteDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insertApunte(Apunte apunte) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ASIGNATURA, apunte.getAsignatura());
        values.put(DatabaseHelper.COLUMN_TEMA, apunte.getTema());
        values.put(DatabaseHelper.COLUMN_FECHA, apunte.getFecha());
        values.put(DatabaseHelper.COLUMN_DESCRIPCION, apunte.getDescripcion());
        values.put(DatabaseHelper.COLUMN_AUDIO, apunte.getAudio());
        return database.insert(DatabaseHelper.TABLE_APUNTES, null, values);
    }

    public int updateApunte(Apunte apunte) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ASIGNATURA, apunte.getAsignatura());
        values.put(DatabaseHelper.COLUMN_TEMA, apunte.getTema());
        values.put(DatabaseHelper.COLUMN_FECHA, apunte.getFecha());
        values.put(DatabaseHelper.COLUMN_DESCRIPCION, apunte.getDescripcion());
        values.put(DatabaseHelper.COLUMN_AUDIO, apunte.getAudio());
        return database.update(DatabaseHelper.TABLE_APUNTES, values,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(apunte.getId())});
    }

    public void deleteApunte(long id) {
        database.delete(DatabaseHelper.TABLE_APUNTES,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public List<Apunte> getAllApuntes() {
        List<Apunte> apuntes = new ArrayList<>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_APUNTES,
                null, null, null, null, null,
                DatabaseHelper.COLUMN_ID + " DESC");

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Apunte apunte = cursorToApunte(cursor);
                apuntes.add(apunte);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return apuntes;
    }

    // Consulta parametrizada exigida por la rúbrica
    public List<Apunte> getApuntesByAsignatura(String asignatura) {
        List<Apunte> apuntes = new ArrayList<>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_APUNTES,
                null,
                DatabaseHelper.COLUMN_ASIGNATURA + " = ?",
                new String[]{asignatura},
                null, null,
                DatabaseHelper.COLUMN_ID + " DESC");

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Apunte apunte = cursorToApunte(cursor);
                apuntes.add(apunte);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return apuntes;
    }

    private Apunte cursorToApunte(Cursor cursor) {
        Apunte apunte = new Apunte();
        apunte.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)));
        apunte.setAsignatura(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ASIGNATURA)));
        apunte.setTema(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TEMA)));
        apunte.setFecha(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FECHA)));
        apunte.setDescripcion(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPCION)));
        apunte.setAudio(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_AUDIO)));
        return apunte;
    }
}
