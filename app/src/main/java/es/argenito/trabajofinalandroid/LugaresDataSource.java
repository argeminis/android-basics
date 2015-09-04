package es.argenito.trabajofinalandroid;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class LugaresDataSource extends ContentProvider{

	// Un ContentProvider no almacena datos, sino que proporciona una interfaz para acceder a ellos 
	// Un ContentResolver ACCEDE A UN CONTENTPROVIDER. Insert/Delete via ApllyBatch (ejecutar varias ordenes a la vez) [es MAS COMPLEJO de programar]
	
	// El ContentProvider se usa para CONSULTAR LA BASE DE DATOS DESDE LAS ACTIVIDADES
	
	/*Informacion del Provider*/
	public final static String PROVIDER_NAME= "es.argenito.spotme"; // nombre del proveedor 
	public final static Uri CONTENT_URI = Uri.parse("content://"+ PROVIDER_NAME +"/lugares"); // recurso unificado (tabla BBDD)
	
	/*Informacion de la tabla*/
	private final static String NOMBRE_TABLA = DatosDB.TABLE_LUGARES;
	public final static String _ID = DatosDB.COLUMN_ID;
	public final static String _NOMBRE= DatosDB.COLUMN_NOMBRE;
	public final static String _DESCRIPCION= DatosDB.COLUMN_DESCRIPCION;
	public final static String _LATITUDE= DatosDB.COLUMN_LATITUD;
	public final static String _LONGITUDE= DatosDB.COLUMN_LONGITUD;
	public final static String _FOTO= DatosDB.COLUMN_FOTO;
	
	/*Bandera para el Uri matcher*/
	//Si el CONTENTPROVIDER DETECTA QUE LE PEDIMOS UN LISTADO O UN UNICO REGISTRO
	private static final int LUGARES= 1;
	private static final int LUGARES_ID= 2;
	
	private static final UriMatcher uriMatcher; // se ha de inicializar
		static {
			
			uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);// necesita un codigo; por defecto ponemos que no coincida
			uriMatcher.addURI(PROVIDER_NAME, "lugares", LUGARES);
			uriMatcher.addURI(PROVIDER_NAME, "lugares/#", LUGARES_ID);
			
		}
	
	private SQLiteDatabase dbR; // referencia para trabajar con la BBDD 
	
	/**/
	
	@Override
	public boolean onCreate() {

		Context context= getContext();
		DBHelper dbhelper= new DBHelper(context);
		dbR = dbhelper.getWritableDatabase();
		
		return (dbR == null) ? false : true;
	}

	@Override
	public String getType(Uri uri) { // nuestro propio TIPO DE CONTENIDO
		
		// Utiliza el URIMATCHER para detectar el tipo de datos entrando
		switch (uriMatcher.match(uri)) {
		
		case LUGARES:
			
			//------------------------dir: hace referencia a un grupo; lo siguiente es la tabla : lugares
			return "vnd.android.cursor.dir/vnd.argenito.lugares";
			
		case LUGARES_ID:
			
			//------------------------item: hace referencia a un elemento; lo siguiente es la tabla : lugares			
			return "vnd.android.cursor.item/vnd.argenito.lugares";
			
		default:
			throw new IllegalArgumentException("Unsupported URI: " +uri);
		}

	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		
		//añadir un nuevo registro, entidad, elemento		
		long rowID= dbR.insert(NOMBRE_TABLA, "", values);
		
		//si todo va bien devolvemos su URI
		if (rowID>0) {
			
			Uri _uri= ContentUris.withAppendedId(CONTENT_URI, rowID);
			getContext().getContentResolver().notifyChange(_uri, null);
			// el notifychange avisa a los demas cursores de volver a tomar los datos
			return _uri;
			
		}
		
		throw new SQLException("Failed to insert row into "+uri);
		
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		
		//count: es para devolver la cantidad de filas afectadas	
	     int count = 0;
	      switch (uriMatcher.match(uri)){
	      case LUGARES: //actualizar TODO (no se contempla en el enunciado)
	         count = dbR.update(NOMBRE_TABLA, values, 
	                 selection, selectionArgs);
	         break;
	         
	      case LUGARES_ID: //actualizar un solo lugar
	         count = dbR.update(NOMBRE_TABLA, values, _ID + 
	                 " = " + uri.getPathSegments().get(1) + 
	                 (!TextUtils.isEmpty(selection) ? " AND (" +
	                 selection + ')' : ""), selectionArgs);
	         
	         //la clase TextUtils detecta si un valor es nulo en la nueva
	         //insercion para determinar si se actualiza -hay un nuevo valor
	         //o si no se hace porque isEmpty
	         
	         break;
	      default: 
	         throw new IllegalArgumentException("Unknown URI " + uri );
	      }
	      
	      getContext().getContentResolver().notifyChange(uri, null);
	      //se notifica de volver a tomar datos	      
	      return count;//devuelve las filas afectadas
	      
	}

	/* args -
	projection: array con todos los campos que deseamos devuelva (solo los que queremos usar)
	selection: clausula WHERE (SQL) de campos que queremos controlar (comodin $1 - posicion 1) del siguiente...
	selectionargs: valores a comprobar (seleccion $1 - comprueba posicion 1)
	sortorder: qué campo queremos que ordene ascendente o descendente (SQL) nombrecampo _ asc/desc
	*/
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		
		// Constructor de consultas
		SQLiteQueryBuilder sqlbuilder= new SQLiteQueryBuilder();
		sqlbuilder.setTables(NOMBRE_TABLA); // asignamos una tabla
		
		// Verficamos uri: si pide un lugar o varios
		if (uriMatcher.match(uri) == LUGARES_ID) {
			
			sqlbuilder.appendWhere(_ID + " = " + uri.getPathSegments().get(1));
			// añadimos a las condiciones _ID: constante; a las consultas de seleccion que...
			// uri, divide las rutas de la uri y coge el ultimo campo (0= tabla, 1= posicion)
		}
		
		// Verificamos el orden
		if (sortOrder == null | sortOrder == "") {
			
			sortOrder = _NOMBRE;
		}
		
		// Lanzamos la peticion, pues los pasos anteriores nos han dado la tabla, los datos y el orden
		Cursor c= sqlbuilder.query(dbR, projection, selection, selectionArgs, null, null, sortOrder);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		
		return c; // devolvemos los datos en un cursor a la app que los haya pedido
	}
	
	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
	
		int count; //count: es para devolver la cantidad de filas afectadas

		switch (uriMatcher.match(uri)) {
		  case LUGARES://ELIMINAR TODO (no se contempla en el enunciado)
		    count = dbR.delete(NOMBRE_TABLA, where, whereArgs);
		    
		    break;

		  case LUGARES_ID:
		    String segment = uri.getPathSegments().get(1);
		    count = dbR.delete(NOMBRE_TABLA, _ID + "="
		                                + segment
		                                + (!TextUtils.isEmpty(where) ? " AND (" 
		                                + where + ')' : ""), whereArgs);
		    
		    //la clase TextUtils detecta si un valor es nulo en la nueva
	        //insercion para determinar si se actualiza -hay un nuevo valor
	        //o si no se hace porque isEmpty
		    
		    break;

		  default: throw new IllegalArgumentException("Unsupported URI: " + uri);
		}

		//se notifica de volver a tomar datos	
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
}