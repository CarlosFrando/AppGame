package com.example.appgame;

public class Carta {
    private int id;
    private int imagenCara; // ID de la imagen de la cara
    private int imagenReverso; // ID de la imagen del reverso
    private boolean volteada;

    public Carta(int id, int imagenReverso, int imagenCara) {
        this.id = id;
        this.imagenReverso = imagenReverso;
        this.imagenCara = imagenCara;
        this.volteada = false;
    }

    public int getImagenReverso() {
        return imagenReverso;
    }

    public int getImagenCara() {
        return imagenCara;
    }

}