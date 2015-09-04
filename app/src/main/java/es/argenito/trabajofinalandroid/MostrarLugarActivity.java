package es.argenito.trabajofinalandroid;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/* Muestra todos los datos de un lugar: nombre, descripción y foto. La foto debe visualizarse en una vista a partir de la URI asociada 
 * al lugar (el campo “foto” de la BD). Debe haber un botón “Editar” que abrirá EditarLugarActivity para modificar el lugar en cuestión. */

public class MostrarLugarActivity extends Activity{	
	
	static int RESULT_EDITARLUGAR = 5;
	private Cursor cursorMostrarLugar;
	Uri urilist, uri_foto;
	String nom, des, fot;
	double lat,lon;
	
	TextView 	mostrarlugar_nombre, mostrarlugar_descripcion, 
				mostrarlugar_latitude, mostrarlugar_longitude;
	ImageView 	mostrarlugar_imageview;
	
	
	public void setActivityBackgroundColor(int color) {
		
	    View view = this.getWindow().getDecorView();
	    view.setBackgroundColor(color);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.mostrarlugar_layout);

		//Obtenemos _id del intent y guardamos 
				
		Bundle b = getIntent().getExtras();
		final int id= b.getInt("ID_resultado_cursor");	//ID
		
	try {
		
		urilist= Uri.parse("content://"+LugaresDataSource.PROVIDER_NAME+"/lugares/*");// /* para mostrar todos
		//>>(formato actualizado; en lugar de managedquery)...
		cursorMostrarLugar= this.getContentResolver().query(urilist, DatosDB.columnas, null,null,null);
		
		/*19-03-15*/
		//Comprobar si el cursor esta en el primer registro
		if (cursorMostrarLugar.moveToFirst() == false){
			   //el cursor está vacío
			   return;
			}
		
		//Condicion inicial; condicion final (DESPUES del ultimo registro; ++
		for(cursorMostrarLugar.moveToFirst(); !cursorMostrarLugar.isAfterLast(); cursorMostrarLugar.moveToNext()) {
			
			//Si el int(columna _id) devuelto coincide con el _id_cursor(en intent)
			if(cursorMostrarLugar.getInt(0)== id) {
				nom = cursorMostrarLugar.getString(1);
				des = cursorMostrarLugar.getString(2);
				lat = cursorMostrarLugar.getDouble(3);
				lon = cursorMostrarLugar.getDouble(4);
				fot= cursorMostrarLugar.getString(5);
			}
			
		} // ----------------------------------------------------------------------------------------
				
		mostrarlugar_nombre=(TextView)findViewById(R.id.mostrarlugar_nombre);
		mostrarlugar_descripcion=(TextView)findViewById(R.id.mostrarlugar_descripcion);
		mostrarlugar_latitude=(TextView)findViewById(R.id.mostrarlugar_latitude);
		mostrarlugar_longitude=(TextView)findViewById(R.id.mostrarlugar_longitude);
		mostrarlugar_imageview= (ImageView) findViewById(R.id.mostrarlugar_imageview);
		
		mostrarlugar_nombre.setText(nom);
		mostrarlugar_descripcion.setText(des);
		
		String s_lat = Double.toString(lat);
		String s_lon = Double.toString(lon);		
		
		mostrarlugar_latitude.setText(s_lat);
		mostrarlugar_longitude.setText(s_lon);
		
		uri_foto= Uri.parse(fot);
		mostrarlugar_imageview.setImageURI(uri_foto);
		
		} catch (NullPointerException e) {
			
			Toast.makeText(MostrarLugarActivity.this, "Uno de los campos no contiene valores", Toast.LENGTH_LONG).show();
		
		} finally {
			
			cursorMostrarLugar.close();
			
		}

		ImageButton button_editar_lugar=(ImageButton)findViewById(R.id.button_editar_lugar);
		
		//Al hacer click en boton EDITAR
		button_editar_lugar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				int id= getIntent().getIntExtra("ID_resultado_cursor", -1);
				
                Intent intentEditarLugar = new Intent(getApplicationContext(), EditarLugarActivity.class);
                intentEditarLugar.putExtra("ID_editar", id);
                intentEditarLugar.putExtra("latitude", lat);
                intentEditarLugar.putExtra("longitude", lon);
                
                //finalizamos ESTA actividad para que se actualice al
                //terminar o volver de las otras (SDK-8 no utiliza
                // "recreate()" que podria servir al mismo fin
                finish();
                startActivity(intentEditarLugar);
				
			}
		});
				
	}// fin onCREATE
	
}