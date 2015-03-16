package es.dlacalle.motonotify;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


public class MiArrayAdapter extends ArrayAdapter<FilaAppList> {

    private final Context context;
    private final List<FilaAppList> aplicaciones;

    /* Constructor del ArrayAdapter personalizado
     * Parámetros:
     * @param context: el contexto de nuestra aplicación en el que se crea el ArrayAdapter
     * @param aplicaciones: lista de FilaAppList que contiene los datos para rellenar el ArrayAdapter
     */
    public MiArrayAdapter(Context context, List<FilaAppList> aplicaciones){
        /* Lo primero es llamar al constructor del ArrayAdapter pasandole el contexto
         * el layout que utilizará y los datos para rellenar
         */
        super(context, R.layout.app_row, aplicaciones);
        /* y nos guardamos el contexto y la lista de objetos para rellenar */
        this.context=context;
        this.aplicaciones=aplicaciones;
        //inflater = activity.getWindow().getLayoutInflater();

    }

    /* Debemos sobreescribir este método que será llamado para crear cada fila del ListView.
     * La llamada se realiza por sí sola, no es necesario que lo llamemos en el Activity en ningun
     * momento.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        /* Creamos el inflater */
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        /* Creamos el View para el LayoutInflater indicando el layout de cada fila y
         * el grupo en el que se encuentra*/
        View rowView = inflater.inflate(R.layout.app_row, parent, false);

        /* Creamos los campos de layout en el código y los asociamos a sus homólogos
         * del layout
         */

        TextView textView = (TextView) rowView.findViewById(R.id.app_row_label);
        TextView textViewPkg = (TextView) rowView.findViewById(R.id.app_row_pkglabel);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.app_row_icon);
        CheckBox checkbox = (CheckBox) rowView.findViewById(R.id.app_row_checkbox);

        /* Asignamos los valores */
        textView.setText(aplicaciones.get(position).getNombreApp());
        textViewPkg.setText(aplicaciones.get(position).getNombrePaquete());
        imageView.setImageDrawable(aplicaciones.get(position).getIcon());
        checkbox.setChecked(aplicaciones.get(position).getSeleccionado());

        return rowView;
    }
}
