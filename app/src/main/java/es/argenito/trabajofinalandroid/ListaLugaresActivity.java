package es.argenito.trabajofinalandroid;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

/* Presenta una lista de todos los lugares guardados en la base de datos. Para cada uno se muestra su nombre y descripción. 
* Al pulsar en uno de ellos se lanza MostrarLugarActivity para verlo en detalle */

public class ListaLugaresActivity extends ListActivity {
	
	//Cursor global de clase
	private Cursor cursorListaLugares;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listalugares_layout);
		
		final Uri uriList= Uri.parse("content://"+LugaresDataSource.PROVIDER_NAME+"/jugadores/*");// /* para mostrar todos
		//Managedquery es un metodo "deprecated" (no deberia usarse); deberia emplearse la forma 
		//que se describe abajo (donde this es la actividad en que se ejecuta):
		cursorListaLugares= this.getContentResolver().query(uriList, DatosDB.columnas, null,null,null);
		
		//Creamos la proyeccion y hacia donde proyectara la consulta
			String[] camposDB= new String[] {
					DatosDB.COLUMN_NOMBRE,
					DatosDB.COLUMN_DESCRIPCION,
					DatosDB.COLUMN_LATITUD,
					DatosDB.COLUMN_LONGITUD,
					DatosDB.COLUMN_FOTO
			};
			
			int[] camposView = new int[] { 
					R.id.lugar_info_nombre,
				    R.id.lugar_info_descripcion,
				    R.id.lugar_info_latitude,
				    R.id.lugar_info_longitude,
				    R.id.lugar_info_uri_foto
				    };
			
			/*El CONSTRUCTOR es "deprecated".
			SimpleCursorAdapter llama con flags en FLAG_AUTO_REQUERY (deprecated)			
			Este flag no es necesario al usar un CursorLoader y se puede pasar 0.*/
			
			//Para hacer el "requery": 1 = true ; 0 = false (ultimo argumento, tras camposView).
			final SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.lugar_info, cursorListaLugares, camposDB, camposView, 1);
			ListView _list= this.getListView();
			_list.setAdapter(adapter);
			
			//Para capturar un click LARGO sobre un item en la lista y editarlo DIRECTAMENTE
			_list.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> parent,
						View view, int position, long id) {
					
					//Mueve el cursor a la posicion en la que se selecciona
					//en List y obtiene el ID desde la Base de datos
					cursorListaLugares.moveToPosition(position);
					int c_id= cursorListaLugares.getInt(0);
					
					//Se envia el ID para editar DIRECTAMENTE
	                Intent intentEditarLugar = new Intent(getApplicationContext(), EditarLugarActivity.class);
	                intentEditarLugar.putExtra("ID_editar", c_id);
	                
					//finalizamos ESTA actividad para que se actualice al
				    //terminar o volver de las otras (SDK-8 no utiliza
				    //"recreate()" que podria servir al mismo fin
					finish();
	                startActivity(intentEditarLugar);
	                
	                return true; // true: el evento se ha utilizado
				}
			});

			//Para capturar un click en un item y ejecutar la actividad MostrarLugar
			_list.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

					//Mueve el cursor a la posicion en la que se selecciona
					//en List y obtiene el ID desde la Base de datos
					cursorListaLugares.moveToPosition(position);
					int c_id= cursorListaLugares.getInt(0);
					
					//Pasar el _id (int) de la consulta (cursor) sobre el lugar a MostrarLugar
					Intent intentMostrarlugar_lista = new Intent(getApplicationContext(), MostrarLugarActivity.class);
					intentMostrarlugar_lista.putExtra("ID_resultado_cursor", c_id);

					//finalizamos ESTA actividad para que se actualice al
				    //terminar o volver de las otras (SDK-8 no utiliza
				    //"recreate()" que podria servir al mismo fin
					finish();
	                startActivity(intentMostrarlugar_lista);					
				}
			});
	}
}