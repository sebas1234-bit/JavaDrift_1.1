package com.javadrift;
import java.awt.Color;
import java.awt.Graphics;

public class CarroJugador extends Vehiculo {
    public CarroJugador(int x, int y) {
        super(x, y, 3, Color.RED, "Jugador");
    }

    @Override
    public void mover(KeyHandler teclado) {
        if (teclado.arribaPresionada)    y -= velocidad;
        if (teclado.abajoPresionada)     y += velocidad;
        if (teclado.izquierdaPresionada) x -= velocidad;
        if (teclado.derechaPresionada)   x += velocidad;
    }

}
