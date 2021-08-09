package com.example.myapplicationc;

import com.opencsv.bean.CsvBindByName;

public class Data {

    @CsvBindByName(column = "Nombre",required = true)
    private String Nombre;

    @CsvBindByName(column = "Archivo",required = true)
    private String Archivo;

    @CsvBindByName(column = "Tamanio B",required = true)
    private Double Tamanio;

    @CsvBindByName(column = "Key",required = true)
    private String Key;

    @CsvBindByName(column = "Tiempo de Encriptación",required = true)
    private float Tiempo_E;

    @CsvBindByName(column = "Tiempo de Desencriptación",required = true)
    private float Tiempo_D;

    @CsvBindByName(column = "Tiempo Total",required = true)
    private float Tiempo_T;

    @CsvBindByName(column = "Uso de Memoria KB",required = true)
    private int Memoria;

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getArchivo() {
        return Archivo;
    }

    public void setArchivo(String archivo) {
        Archivo = archivo;
    }

    public Double getTamanio() {
        return Tamanio;
    }

    public void setTamanio(Double tamanio) {
        Tamanio = tamanio;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public float getTiempo_E() {
        return Tiempo_E;
    }

    public void setTiempo_E(float tiempo_E) {
        Tiempo_E = tiempo_E;
    }

    public float getTiempo_D() {
        return Tiempo_D;
    }

    public void setTiempo_D(float tiempo_D) {
        Tiempo_D = tiempo_D;
    }

    public float getTiempo_T() {
        return Tiempo_T;
    }

    public void setTiempo_T(float tiempo_T) {
        Tiempo_T = tiempo_T;
    }

    public int getMemoria() {
        return Memoria;
    }

    public void setMemoria(int memoria) {
        Memoria = memoria;
    }

    public Data(String nombre, String archivo, Double tamanio, String key, float tiempo_E, float tiempo_D, float tiempo_T, int memoria) {
        Nombre = nombre;
        Archivo = archivo;
        Tamanio = tamanio;
        Key = key;
        Tiempo_E = tiempo_E;
        Tiempo_D = tiempo_D;
        Tiempo_T = tiempo_T;
        Memoria = memoria;
    }

    @Override
    public String toString() {
        return "Data{" +
                "Nombre='" + Nombre + '\'' +
                ", Archivo='" + Archivo + '\'' +
                ", Tamanio=" + Tamanio +
                ", Key='" + Key + '\'' +
                ", Tiempo_E=" + Tiempo_E +
                ", Tiempo_D=" + Tiempo_D +
                ", Tiempo_T=" + Tiempo_T +
                ", Memoria=" + Memoria +
                '}';
    }
}
