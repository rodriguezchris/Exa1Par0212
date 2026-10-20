package com.uth.exa1par0212.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "apuntes.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_APUNTES = "apuntes";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_ASIGNATURA = "asignatura";
    public static final String COLUMN_TEMA = "tema";
    public static final String COLUMN_FECHA = "fecha";
    public static final String COLUMN_DESCRIPCION = "descripcion";
    public static final String COLUMN_AUDIO = "audio";

    public static final String TABLE_ASIGNATURAS = "asignaturas";
    public static final String COLUMN_ASIGNATURA_ID = "id";
    public static final String COLUMN_ASIGNATURA_NOMBRE = "nombre";

    private static final String TABLE_APUNTES_CREATE =
            "CREATE TABLE " + TABLE_APUNTES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_ASIGNATURA + " TEXT NOT NULL, " +
                    COLUMN_TEMA + " TEXT NOT NULL, " +
                    COLUMN_FECHA + " TEXT NOT NULL, " +
                    COLUMN_DESCRIPCION + " TEXT, " +
                    COLUMN_AUDIO + " TEXT);";

    private static final String TABLE_ASIGNATURAS_CREATE =
            "CREATE TABLE " + TABLE_ASIGNATURAS + " (" +
                    COLUMN_ASIGNATURA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_ASIGNATURA_NOMBRE + " TEXT UNIQUE NOT NULL);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_APUNTES_CREATE);
        db.execSQL(TABLE_ASIGNATURAS_CREATE);

        // Insertar asignaturas por defecto
        db.execSQL("INSERT OR IGNORE INTO " + TABLE_ASIGNATURAS + " (" + COLUMN_ASIGNATURA_NOMBRE + ") VALUES ('Programación Móvil I');");
        db.execSQL("INSERT OR IGNORE INTO " + TABLE_ASIGNATURAS + " (" + COLUMN_ASIGNATURA_NOMBRE + ") VALUES ('Base de Datos');");
        db.execSQL("INSERT OR IGNORE INTO " + TABLE_ASIGNATURAS + " (" + COLUMN_ASIGNATURA_NOMBRE + ") VALUES ('Redes I');");
        db.execSQL("INSERT OR IGNORE INTO " + TABLE_ASIGNATURAS + " (" + COLUMN_ASIGNATURA_NOMBRE + ") VALUES ('Inglés Técnico');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APUNTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASIGNATURAS);
        onCreate(db);
    }
}
