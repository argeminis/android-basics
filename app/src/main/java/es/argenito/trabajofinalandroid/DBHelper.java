package es.argenito.trabajofinalandroid;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	
	// DBHelper: PARA CREAR LA BASE DE DATOS - si es necesario	
	// Es comun crear una subclase de SQLiteOpenHelper por cada tabla SQL que exista en la base de datos
	
	public DBHelper(Context context) {
		super(context, DatosDB.DATABASE_NAME, null, DatosDB.DATABASE_VERSION);		

	}
	
	@Override
	public void onCreate(SQLiteDatabase db) { // el db es un objeto SQLIiteDatabase
		// EJECUTA las instrucciones SQL
		db.execSQL(DatosDB.DATABASE_CREATE_CMD);

	}
	
	//Al actualizar TODA LA BASE DE DATOS
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
	  if(!db.isOpen()) {		  
		  SQLiteDatabase.openDatabase(db.getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.CREATE_IF_NECESSARY);
	   
	  	}
	 }
	
	//Pare recrear la tabla en la BBDD (Boton)
	public void resetTable() {
		
		this.getWritableDatabase().execSQL(DatosDB.DATABASE_RESET_TABLE_CMD);
		this.getWritableDatabase().execSQL(DatosDB.DATABASE_CREATE_CMD);
		
	}
	
	
}