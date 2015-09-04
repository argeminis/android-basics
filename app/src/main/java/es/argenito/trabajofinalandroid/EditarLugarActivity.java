package es.argenito.trabajofinalandroid;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/* Presenta los datos del lugar (nombre y descripción) en cuadros de texto
	 * editables. La foto también debe ser visible y clickable.*/

/*
 * Para cambiar el layout segun orientacion vertical u horizontal existe una carpeta
 * llamada layout-land (horizontal)
 * (http://developer.android.com/training/basics/supporting-devices/screens.html) 
 */

public class EditarLugarActivity extends Activity {
	
	//Codigo para ActivityforResult; Cursor global de clase; Cursor para intent
	// Imagen; uriString de imagen
	private static int RESULT_LOAD_IMAGE = 1;
	private Cursor cursorEditar, cursor;
	ImageView editandolugar_seleccionimagen;	
	String uri_String, cadena;

	// --------------
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editarlugar_layout);
		
		final int				id; // ID a manejar
		final double 	latitude, longitude; 
		
		final EditText 	editText_nombre;
		final EditText 	editText_descripcion;
		EditText 		editText_latitude, editText_longitude;
		TextView 		editandolugar_nombre, editandolugar_descripcion,
						editandolugar_latitude, editandolugar_longitude;

		ImageButton 	button_crear_lugar, button_eliminar_lugar, button_guardaractualizar_lugar;
		
		editandolugar_nombre=(TextView)findViewById(R.id._editandolugar_nombre);
		editandolugar_descripcion=(TextView)findViewById(R.id._editandolugar_descripcion);
		editandolugar_latitude=(TextView)findViewById(R.id._editandolugar_latitude);
		editandolugar_longitude=(TextView)findViewById(R.id._editandolugar_longitude);

		editandolugar_seleccionimagen= (ImageView) findViewById(R.id._editandolugar_seleccionimagen);
		
		editText_nombre=(EditText)findViewById(R.id.editText_nombre);
		editText_descripcion=(EditText)findViewById(R.id.editText_descripcion);
		editText_latitude=(EditText)findViewById(R.id.editText_latitude);
		editText_longitude=(EditText)findViewById(R.id.editText_longitude);
		
		button_crear_lugar=(ImageButton)findViewById(R.id.button_crear_lugar);
	    button_eliminar_lugar=(ImageButton)findViewById(R.id.button_eliminar_lugar);
		button_guardaractualizar_lugar=(ImageButton)findViewById(R.id.button_guardaractualizar_lugar);
		
		//Revision del intent (Bundle) para las coordenadas (point: array de latitud y longitud [double]
		Bundle b = getIntent().getExtras();
		id= b.getInt("ID_editar",-1);				// ID de lugar a editar
		latitude = b.getDouble("latitude");			// DESDE ClickOnMap 
		longitude = b.getDouble("longitude");		// DESDE ClickOnMap

		//Proyeccion de columnas
		final String[] _columnas = DatosDB.columnas;

		//AÑADIR (uriNew) - ACTUALIZAR/ELIMINAR (uriEdit/uriDel) un registro 
		final Uri uriNew = LugaresDataSource.CONTENT_URI; 
		final Uri uriEdit = Uri.parse("content://"+LugaresDataSource.PROVIDER_NAME+"/lugares/"+id); 
		final Uri uriDel= uriEdit;
		
		//Valores de contenido
		final ContentValues cv= new ContentValues();		
				
		//Enseñar como "hint" datos por defecto
		editText_nombre.setHint("Lugar*");
		editText_descripcion.setHint("Descripcion*");
				
		String s_lat= Double.toString(latitude);
		editText_latitude.setHint(s_lat);
		String s_lon= Double.toString(longitude);
		editText_longitude.setHint(s_lon);
		
		//Para hacer visibles/invisibles los botones segun ID (-1 = no hay id; View.GONE[elimina])
		if (id== -1) {
			button_guardaractualizar_lugar.setVisibility(View.GONE);
			button_eliminar_lugar.setVisibility(View.GONE);
			
		} else {
			button_crear_lugar.setVisibility(View.GONE);

			//Consulta DATOS del ID pasado: String de uri de foto ACTUAL
			final Uri urilist= Uri.parse("content://"+LugaresDataSource.PROVIDER_NAME+"/lugares/*");// /* para mostrar todos
			cursorEditar= this.getContentResolver().query(urilist, DatosDB.columnas, null,null,null);
			
			if (cursorEditar.moveToFirst() == false){
				   return;
				}
			
			//foto - desde CONSULTA segun id
			String edit_uri_String = null;
			
			for(cursorEditar.moveToFirst(); !cursorEditar.isAfterLast(); cursorEditar.moveToNext()) {
				if(cursorEditar.getInt(0)== id) {
					
					//Pone en "hint" los datos actuales que se van a cambiar
					editText_nombre.setHint(cursorEditar.getString(1));
					editText_descripcion.setHint(cursorEditar.getString(2));
					edit_uri_String= cursorEditar.getString(5);
					
					}
				}
			
			//cerramos cursor [onCreate]
			cursorEditar.close();
			
			//Parsear a uri el String devuelto por la consulta y asignamos al ImageView
				if (edit_uri_String!= null) {
					Uri uri_foto= Uri.parse(edit_uri_String);
					editandolugar_seleccionimagen.setImageURI(uri_foto);
					}
		}//FIN ELSE
		
		//Boton ACTUALIZAR - click
		button_guardaractualizar_lugar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//Recoge texto del edittext
				String nombre = editText_nombre.getText().toString();
				String descripcion = editText_descripcion.getText().toString();
				
				cv.put(_columnas[1], nombre);//nombre  
				cv.put(_columnas[2], descripcion);//descripcion
				cv.put(_columnas[3], latitude);//latitud
				cv.put(_columnas[4], longitude);//longitud
				cv.put(_columnas[5], uri_String);//foto - desde ACTIVITYRESULT
				
				//Aqui me hubiese gustado añadir un sistema que pusiese
				//texto por defecto si el usuario no introducia nada pero
				//las formas que utilicé no generaban indices de incremento
				//Por cuestiones personales de salud solo tuve tiempo de probar pocas
				//cosas
				
				int lugar= getContentResolver().update(uriEdit, cv, null, null);
				
				Toast.makeText(EditarLugarActivity.this, "Se ha actualizado "+lugar+" lugar.",
						Toast.LENGTH_LONG).show();
				
				//Presentamos el lugar en detalle ACTUALIZADO mediante el intent
				Intent i= new Intent(getApplicationContext(), MostrarLugarActivity.class);
				i.putExtra("ID_resultado_cursor", id);
				
				//finalizamos ESTA actividad para que se actualice al
				//terminar o volver de las otras (SDK-8 no utiliza
				// "recreate()" que podria servir al mismo fin
				startActivity(i);
				finish();				
			}
		});
		
		//Boton CREAR - click
		button_crear_lugar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String nombre = editText_nombre.getText().toString();
				String descripcion = editText_descripcion.getText().toString();
				
				//*****Añade los content y sus keys******
				cv.put(_columnas[1], nombre);//nombre  
				cv.put(_columnas[2], descripcion);//descripcion
				cv.put(_columnas[3], latitude);//latitud
				cv.put(_columnas[4], longitude);//longitud
				cv.put(_columnas[5], uri_String);//foto - ruta en String desde ACTIVITYRESULT
				
				//ContentResolver usando metodos del Provider
				getContentResolver().insert(uriNew, cv);
				
				//Se informa que se ha guardado EN LA BBDD al usuario
				   Toast.makeText(EditarLugarActivity.this,
				           "Se ha guardado ["+nombre+"] en la Base de datos.", Toast.LENGTH_LONG).show();
				   
				   Uri urilist= Uri.parse("content://"+LugaresDataSource.PROVIDER_NAME+"/lugares/*");
				   cursor= getContentResolver().query(urilist, DatosDB.columnas, null,null,null);
				   //Mover al ultimo registro (nuevo)
				   cursor.moveToLast();
				   int nuevo_id= cursor.getInt(0);// obtener el id
				   
				   cursor.close();
				   
					//Presentamos el lugar en detalle ACTUALIZADO mediante el intent
					Intent i= new Intent(getApplicationContext(), MostrarLugarActivity.class);
					i.putExtra("ID_resultado_cursor", nuevo_id);
					
					//finalizamos ESTA actividad para que se actualice al
					//terminar o volver de las otras (SDK-8 no utiliza
					// "recreate()" que podria servir al mismo fin
					startActivity(i);
					finish();
			}
		});
		
		//Eliminar lugar (click)
		button_eliminar_lugar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			
			/* **************** ELIMINAR LUGAR ******************/	
				int lugar= getContentResolver().delete(uriDel, null, null);
								
				Toast.makeText(
						EditarLugarActivity.this,
						"Se ha eliminado "+lugar+" lugar de la Base de datos.",
						Toast.LENGTH_LONG).show();
				
				Intent i= new Intent(getApplicationContext(), MapaLugaresActivity.class);

				//finalizamos ESTA actividad para que se actualice al
				//terminar o volver de las otras (SDK-8 no utiliza
				// "recreate()" que podria servir al mismo fin
				startActivity(i);
				finish();				
			}
		});
		
		//Al hacer click sobre la imagen
		editandolugar_seleccionimagen.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(
						//se utiliza el metodo de sistema ACTION_PICK y se consulta con el provider de
						//imagenes en el dispositivo
						Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
						startActivityForResult(i, RESULT_LOAD_IMAGE);
			}
		});
	}// FIN ONCREATE
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        //si la actividad devuelve todo correcto + DATOS
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
        	Uri selectedImage = data.getData();
            
        	//Guardamos el String de la uri de la imagen seleccionada
        	//en la variable global de clase uri_String
            uri_String= data.getDataString();
            
            //Indices de rutas a las imagenes asociadas a una proyeccion
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
 
            //Cursor para consultar la Provider de imagenes sobre la imagen seleccionada            
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
 
            //Extrae la ruta desde la columna 0 como string
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            
            //cerramos cursor [onActivityResult]
            cursor.close();
            
            //asignamos la nueva imagen segun Bitmap
            editandolugar_seleccionimagen.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            
        }
    }
}