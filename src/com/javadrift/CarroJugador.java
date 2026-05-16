package com.javadrift;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;

public class CarroJugador extends Vehiculo {

    double angulo = 0;        // angulo actual en grados
    double velocidadActual = 0;  // velocidad actual
    final double ACELERACION = 0.15;
    final double FRICCION = 0.92;
    final double VEL_MAX = 5.0;
    final double VEL_ROTACION = 3.0;

    public CarroJugador(int x, int y, String rutaImagen, String nombre) {
        super(x, y, 3, Color.WHITE, nombre, rutaImagen);
        angulo = 90;
    }

    @Override
    public void mover(KeyHandler teclado) {
        // Rotar con flechas izquierda/derecha
        if (teclado.izquierdaPresionada) angulo -= VEL_ROTACION;
        if (teclado.derechaPresionada)   angulo += VEL_ROTACION;

        // Acelerar con flecha arriba
        if (teclado.arribaPresionada) {
            velocidadActual += ACELERACION;
            if (velocidadActual > VEL_MAX) velocidadActual = VEL_MAX;
        }

        // Frenar con flecha abajo
        if (teclado.abajoPresionada) {
            velocidadActual -= ACELERACION * 2;
            if (velocidadActual < -VEL_MAX / 2) velocidadActual = -VEL_MAX / 2;
        }

        // Aplicar friccion
        if (!teclado.arribaPresionada && !teclado.abajoPresionada) {
            velocidadActual *= FRICCION;
        }

        // Mover en la direccion que apunta el carro
        double rad = Math.toRadians(angulo);
        x += (int)(Math.sin(rad) * velocidadActual);
        y -= (int)(Math.cos(rad) * velocidadActual);

        // Limites de pantalla
        if (x < 0) x = 0;
        if (x > 750) x = 750;
        if (y < 0) y = 0;
        if (y > 570) y = 570;
    }

    @Override
    public void dibujar(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        AffineTransform transformOriginal = g2d.getTransform();

        // Rotar alrededor del centro del carro
        g2d.rotate(Math.toRadians(angulo), x + 25, y + 15);

        if (imagen != null) {
            g.drawImage(imagen, x, y, 36, 22, null);
        } else {
            g2d.setColor(color);
            g2d.fillRect(x, y, 50, 30);
        }

        // Nombre encima del carro
        g2d.setTransform(transformOriginal);
        g.setColor(Color.WHITE);
        g.drawString(nombre, x, y - 5);
    }
}