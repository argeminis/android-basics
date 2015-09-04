package es.argenito.trabajofinalandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

/* Actividad principal, se lanza al iniciar la aplicación. Contiene dos botones (“lista” y “mapa”) para lanzar 
 * ListaLugaresActivity y MapaLugaresActivity.*/

public class PrincipalActivity extends Activity {
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.principal_layout);
		
		//Buscamos y asignamos los botones segun id
		ImageButton button_lista=(ImageButton)this.findViewById(R.id.button_lista_lugares);
		ImageButton button_mapa=(ImageButton)this.findViewById(R.id.button_mapa_lugares);
		ImageButton button_HARD_RESET=(ImageButton)this.findViewById(R.id.button_HARD_RESET);
		
		//Asignamos accion al click
		button_lista.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {	
				//intent para ListaLugares
				Intent intentListaLugares = new Intent(PrincipalActivity.this, ListaLugaresActivity.class);
				startActivity(intentListaLugares);	
			}
		});
		
		//Asignamos accion al click
		button_mapa.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//intent para MapaLugares
				Intent intentMapaLugares = new Intent(PrincipalActivity.this, MapaLugaresActivity.class);
				startActivity(intentMapaLugares);				
			}
		});
		
		//Asignamos accion al click
		button_HARD_RESET.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Mediante el objeto DBHelper recreamos la tabla y se informa de ello
				DBHelper dbr= new DBHelper(getApplicationContext());
				dbr.resetTable();
				
				Toast.makeText(PrincipalActivity.this,
				           "Tabla en base de datos RECREADA",
				           Toast.LENGTH_SHORT).show();
			}
		});
		
	}
}	