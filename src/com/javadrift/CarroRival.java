package com.javadrift;

import java.awt.Color;

public class CarroRival extends Vehiculo{
    private int[] rutaX = {100, 300, 600, 650, 600, 300, 100, 50};
    private int[] rutaY = {100, 80, 80, 300, 500, 500, 500, 300};
    private int puntoActual = 0;

    public CarroRival(int x, int y, Color color, String nombre) {
        super(x, y, 2, color, nombre);
    }

    @Override
    public void mover(KeyHandler teclado) {
        int destinoX = rutaX[puntoActual];
        int destinoY = rutaY[puntoActual];

        if (x < destinoX) x += velocidad;
        if (x > destinoX) x -= velocidad;
        if (y < destinoY) y += velocidad;
        if (y > destinoY) y -= velocidad;

        int distancia = Math.abs(x - destinoX) + Math.abs(y - destinoY);
        if (distancia < 10 ) {
            puntoActual = (puntoActual + 1) % rutaX.length;
        }
    }


}
