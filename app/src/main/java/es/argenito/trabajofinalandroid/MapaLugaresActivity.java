package es.argenito.trabajofinalandroid;

import java.util.Random;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/*Define el mapa (Google apis) desde donde seleccionar puntos y guardarlos o editarlos*/

public class MapaLugaresActivity extends FragmentActivity {

	//Definimos el mapa y el cursor como globales de la clase
	GoogleMap map;
	private Cursor cursorDatosMarcadores;
	/* Para generar un color al azar */
	static Random rand = new Random(); 
    static int min_col= 0, max_col= 360;

    public static float randInt(int min, int max) {
        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
       float randomNum= rand.nextInt((max - min) + 1) + min;
       return randomNum;
    }
	
    // ----------------
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapalugares_layout);
        
        //Inicializamos el mapa
        SupportMapFragment smf= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        map = smf.getMap();
        
        //Al seleccionar UN PUNTO en el MAPA
        map.setOnMapClickListener(
        		
        		new OnMapClickListener() {
					
					@Override
					public void onMapClick(LatLng point) {
						
					   map.getProjection(); //LatLng: latitud, longitud
					   
					   //Envia en el bundle coordenadas recogidas en el click para el intent
					   //a EditarLugarActivity
					   Intent intentEditarLugarActivity = new Intent(getApplicationContext(),EditarLugarActivity.class);
					   
		               Bundle b = new Bundle();
					   b.putDouble("latitude", point.latitude);
					   b.putDouble("longitude", point.longitude);
					   intentEditarLugarActivity.putExtras(b);
					   
					   //finalizamos ESTA actividad para que se actualice al
					   //terminar o volver de las otras (SDK-8 no utiliza
					   // "recreate()" que podria servir al mismo fin
					   finish();
					   startActivity(intentEditarLugarActivity);
					}
						
				});
        
    /* ***Creando un objeto marcador con la informacion en la Base de datos*** */
        
        //Uri a consultar mediante ContentResolver.query en el cursor
        Uri uriList= Uri.parse("content://"+LugaresDataSource.PROVIDER_NAME+"/lugares/*");
        cursorDatosMarcadores= this.getContentResolver().query(uriList, DatosDB.columnas, null,null,null);

        //Comprobar si el cursor esta en el primer registro
		if (cursorDatosMarcadores.moveToFirst() == false){
			   //el cursor está vacío
			   return;
			}
		
		//Iteracion DEL CURSOR para llenar cada campo
			/*condicion inicial; condicion que termina la iteracion; accion a repetir*/
			/* mover al primer registro; cuando ESTÉ DESPUES del ultimo registro; mover al SIGUIENTE registro */
		for(cursorDatosMarcadores.moveToFirst(); !cursorDatosMarcadores.isAfterLast(); cursorDatosMarcadores.moveToNext()) {
			
			//Objeto Marker
	        MarkerOptions marker= new MarkerOptions();

	        	//Asignando lo que el cursor devuelve por campo
	        	marker.title(cursorDatosMarcadores.getString(1));
				marker.snippet(cursorDatosMarcadores.getString(2));
				
		        LatLng latLng= new LatLng(cursorDatosMarcadores.getDouble(3), cursorDatosMarcadores.getDouble(4));
		        marker.position(latLng);
		        
		        //Asignamos un color aleatorio al marcador
		        float color_marcador= randInt(min_col, max_col);
		        marker.icon(BitmapDescriptorFactory.defaultMarker(color_marcador));
		        
		        //Nuevo marcador para el mapa
		        map.addMarker(marker);
				
		}// FIN bucle FOR
        
    /* *****Al seleccionar un MARCADOR***** */
        map.setOnMarkerClickListener(new OnMarkerClickListener() {
			
			@Override
			public boolean onMarkerClick(Marker marker) {
				
				//Recogemos la posicion del marker (Click)
				//(no se puede guardar dos marker EXACTAMENTE en el mismo lugar)
				
				LatLng ll= marker.getPosition();
				double lat= ll.latitude;
				double lon= ll.longitude;
								
				//Inicializamos variable para poner en el intent
				int id= -1;	// -1: para controlar si se envía un ID o no en EditarLugar
							// -1 indica que NO se ha pasado un ID, asi que es un NUEVO lugar
				
				//Cursor: busca el id segun la posicion (latitude, longitude)
				for(cursorDatosMarcadores.moveToFirst(); !cursorDatosMarcadores.isAfterLast(); cursorDatosMarcadores.moveToNext()) {
					double _lat= cursorDatosMarcadores.getDouble(3);
					double _lon= cursorDatosMarcadores.getDouble(4);
					
					//Si latitude y longitude coinciden con el valor del marker
					// (no encontré una forma mas sencilla o directa de hacerlo)
					if (_lat== lat && _lon== lon) {
						//extrae y asigna el numero de ID a id
						id= cursorDatosMarcadores.getInt(0);
					}
					   
				}// FIN bucle FOR
				
				//intent para enviar el ID del marker 
				Intent intentMostrarLugar_mapa = new Intent(getApplicationContext(), MostrarLugarActivity.class);

					Bundle b = new Bundle();
					b.putDouble("latitude", lat);
					b.putDouble("longitude", lon);
					b.putInt("ID_resultado_cursor", id);	// un ID existente, o -1
					intentMostrarLugar_mapa.putExtras(b);
								        
				startActivity(intentMostrarLugar_mapa);
				
				//finalizamos ESTA actividad para que se actualice al
				//terminar o volver de las otras (SDK-8 no utiliza
				// "recreate()" que podria servir al mismo fin
				finish();
				
				return true;//true: porque el evento (clickMarker) se ha utilizado
			}
		});
        
    /* *****Para gestionar un click LARGO en el mapa***** */
        map.setOnMapLongClickListener(
        		new OnMapLongClickListener() {
					
					@Override
					public void onMapLongClick(LatLng point) {
						
						//Hace Zoom sobre la zona que se ha hecho el click largo
						//!Para hacer zoom hemos de llamar un constructor de camara y asignarlo al mapa
						CameraPosition cameraPosition = new CameraPosition.Builder()
						    .target(point)     		// hacia donde se hará el zoom (centrando)
						    .zoom(8)                // zoom
						    .tilt(20)               // inclinacion
						    .build();               // constructor
						
						map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
					}
				});
        
        //detectar localizacion del dispositivo...
        map.setMyLocationEnabled(true); // se han de añadir los permisos de localizacion
        		
    }
}