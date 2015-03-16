package es.dlacalle.motonotify;


import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ConfiguracionActivity extends ListActivity {

    MiArrayAdapter listaAppDispArrayAdapter;

    //Utilizamos package manager para listar las aplicaciones

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        /* Pasos para crear mi propio ArrayAdapter a fin de utilizar un ListView personalizado
        * 1. Crear la clase que guardará los objetos, con sus propiedades, getters y setters.(Ver FilaAppList)
        * 2. Crear la clase MiArrayAdapter que extiende ArrayAdapter<FilaAppList>
        * 3. Creamos una List<FilaAppList> que rellenaremos con un ArrayList de objetos tipo FilaAppList
        * 4. Cargamos los datos en el ArrayList
        * 5. Instanciamos MiArrayAdapter pasándole la List de objetos FilaAppList
        * 6. Asignamos el ArrayAdapter a la ListView de la ListActivity mediante setListAdapter
        *   porque la Activity es de tipo ListActivity, si fuera tipo Activity habría que crear
        *   el ListView, asociarlo con su elemento del Layout y utilizar el método setAdapter. */

        List<FilaAppList> aplicaciones = new ArrayList<>();

        final PackageManager pm = getPackageManager();
        List<ApplicationInfo> paquetes = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for(ApplicationInfo infoPaquete : paquetes ){

            FilaAppList app = new FilaAppList();
            app.setNombreApp(infoPaquete.loadLabel(getPackageManager()).toString());
            //if(app.getNombreApp().equals("WhatsApp") || app.getNombreApp().equals("Maps")) {
                app.setIcon(infoPaquete.loadIcon(getPackageManager()));
                app.setNombrePaquete(infoPaquete.packageName);
                aplicaciones.add(app);
            //}
        }

        //Preparamos los campos
        listaAppDispArrayAdapter = new MiArrayAdapter(this, aplicaciones);
        //Asignamos el arrayadapter personalizado a la lista
        setListAdapter(listaAppDispArrayAdapter);

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        if(this.listaAppDispArrayAdapter.getItem(position).getSeleccionado())
            this.listaAppDispArrayAdapter.getItem(position).setSeleccionado(false);
        else this.listaAppDispArrayAdapter.getItem(position).setSeleccionado(true);
        this.listaAppDispArrayAdapter.notifyDataSetChanged();
    }

    public void buttonClickedConfig(View v){

        switch (v.getId()){
            case R.id.BotonPermisos:
            Intent intent=new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);
            break;

            case R.id.BotonSelectAll:

                for(int j=0; j<listaAppDispArrayAdapter.getCount(); j++)
                    listaAppDispArrayAdapter.getItem(j).setSeleccionado(true);
                listaAppDispArrayAdapter.notifyDataSetChanged();

                break;

            case R.id.BotonUnselectAll:
                for(int j=0; j<listaAppDispArrayAdapter.getCount(); j++)
                    listaAppDispArrayAdapter.getItem(j).setSeleccionado(false);
                listaAppDispArrayAdapter.notifyDataSetChanged();

                break;

            case R.id.BotonConfirMarcados:
                Intent volver = new Intent();
                int nApps = listaAppDispArrayAdapter.getCount();
                String sApps="";
                int nSelectedApps=0;
                for(int i=0; i<nApps; i++){
                    if(listaAppDispArrayAdapter.getItem(i).getSeleccionado()) {
                        sApps+=listaAppDispArrayAdapter.getItem(i).getNombrePaquete()+"\n";
                        nSelectedApps++;
                    }
                }
                if (nSelectedApps==0) Toast.makeText(this, "Debes marcar al menos 1", Toast.LENGTH_SHORT).show();
                else {
                    volver.putExtra("aplicaciones_marcadas", sApps);
                    setResult(Activity.RESULT_OK, volver);
                    finish();
                }
                break;

        }
    }

}

