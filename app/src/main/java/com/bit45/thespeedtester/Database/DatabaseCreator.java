package com.bit45.thespeedtester.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Gabriel on 8/6/2015.
 * This class serves as a manager for the database creation and upgrade, not only one table.
 */
public class DatabaseCreator extends SQLiteOpenHelper{

    public static int version = 2;

    //SQL sentence that will be used to create the table
    String creationSentence = "CREATE TABLE TestResults (_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                                                        + " connection TEXT,"
                                                        + " network TEXT,"
                                                        + " speed REAL,"
                                                        + " data REAL,"
                                                        + " duration INTEGER,"
                                                        + " date TEXT,"
                                                        + " time TEXT,"
                                                        + " unit INTEGER)";

    public DatabaseCreator(Context context, String dbName, CursorFactory factory){
        super(context, dbName, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(creationSentence);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //NOTA: Por simplicidad del ejemplo aquí utilizamos directamente la opción de
        //      eliminar la tabla anterior y crearla de nuevo vacía con el nuevo formato.
        //      Sin embargo lo normal será que haya que migrar datos de la tabla antigua
        //      a la nueva, por lo que este método debería ser más elaborado.

        //Se elimina la versión anterior de la tabla
        db.execSQL("DROP TABLE IF EXISTS TestResults");

        //Se crea la nueva versión de la tabla
        db.execSQL(creationSentence);
    }
}
