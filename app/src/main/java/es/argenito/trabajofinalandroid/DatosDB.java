package es.argenito.trabajofinalandroid;

//DatosDB: CLASE DE ABSTRACCION DE NOMBRES DE TABLAS Y CAMPOS	

public class DatosDB {
	
	//Variables de valores para instrucciones SQL 
	public static final String DATABASE_NAME = "mislugares.db";
	public static final int DATABASE_VERSION = 1;
	
	public static final String TABLE_LUGARES = "lugares";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NOMBRE = "nombre";
	public static final String COLUMN_DESCRIPCION = "descripcion";
	public static final String COLUMN_LATITUD = "latitud"; // double
	public static final String COLUMN_LONGITUD = "longitud";// double
	public static final String COLUMN_FOTO = "foto";
	
	//Las columnas se convierten en "projection" en el ContentProvider
	public static final String[] columnas = new String[] {
			DatosDB.COLUMN_ID,			//0
			DatosDB.COLUMN_NOMBRE,		//1
			DatosDB.COLUMN_DESCRIPCION,	//2
			DatosDB.COLUMN_LATITUD,		//3
			DatosDB.COLUMN_LONGITUD,	//4
			DatosDB.COLUMN_FOTO,		//5				
	};
	
	//El campo “foto” es una URI de Android que referencia a un fichero de imagen guardado en alguna carpeta.
	
	// Declaracion de creacion de Base de Datos y Tabla (SQL)
	public static final String DATABASE_CREATE_CMD = "create table "+ TABLE_LUGARES + "(" 
			+ COLUMN_ID + " integer primary key autoincrement, " 
			+ COLUMN_NOMBRE + " text not null, "
			+ COLUMN_DESCRIPCION + " text not null,"
			+ COLUMN_LATITUD + " real not null, "
			+ COLUMN_LONGITUD + " real not null,"
			+ COLUMN_FOTO + " text);";
	
	public static final String DATABASE_RESET_TABLE_CMD = "DROP TABLE lugares; ";
	
}
