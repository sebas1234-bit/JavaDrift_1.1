package com.javadrift;

import java.awt.Color;

public class CarroJugador extends Vehiculo {

    public CarroJugador(int x, int y, Color color, String nombre) {
        super(x, y, 3, color, nombre);
    }

    @Override
    public void mover(KeyHandler teclado) {
        if (teclado.arribaPresionada)    y -= velocidad;
        if (teclado.abajoPresionada)     y += velocidad;
        if (teclado.izquierdaPresionada) x -= velocidad;
        if (teclado.derechaPresionada)   x += velocidad;
    }
}