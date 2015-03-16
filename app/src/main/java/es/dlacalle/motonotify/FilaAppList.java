package es.dlacalle.motonotify;

import android.graphics.drawable.Drawable;

public class FilaAppList {

    private Drawable icon;
    private String nombreApp;
    private String nombrePaquete;
    private String nombreVersion;
    private int codigoVersion;
    private Boolean seleccionado;

    public FilaAppList(){
        this.seleccionado = false;
    }

    public FilaAppList(Drawable icon, String nombreApp, String nombrePaquete, String nombreVersion, int codigoVersion) {
        this.icon = icon;
        this.nombreApp = nombreApp;
        this.nombrePaquete = nombrePaquete;
        this.nombreVersion = nombreVersion;
        this.codigoVersion = codigoVersion;
        this.seleccionado = false;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getNombreApp() {
        return nombreApp;
    }

    public void setNombreApp(String nombreApp) {
        this.nombreApp = nombreApp;
    }

    public String getNombrePaquete() {
        return nombrePaquete;
    }

    public void setNombrePaquete(String nombrePaquete) {
        this.nombrePaquete = nombrePaquete;
    }

    public String getNombreVersion() {
        return nombreVersion;
    }

    public void setNombreVersion(String nombreVersion) {
        this.nombreVersion = nombreVersion;
    }

    public int getCodigoVersion() {
        return codigoVersion;
    }

    public void setCodigoVersion(int codigoVersion) {
        this.codigoVersion = codigoVersion;
    }

    public Boolean getSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(Boolean seleccionado) {
        this.seleccionado = seleccionado;
    }
}

