package com.javadrift;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;

public class CarroRival extends Vehiculo {

    private int[] rutaX = {400, 550, 680, 650, 400, 150, 80, 150};
    private int[] rutaY = {80,  120, 280, 450, 520, 450, 280, 120};
    private int puntoActual = 0;
    double angulo = 0;
    double velocidadActual = 0;
    final double VEL_MAX = 2.5;
    final double ACELERACION = 0.1;

    public CarroRival(int x, int y, Color color, String nombre, String rutaImagen) {
        super(x, y, 2, color, nombre, rutaImagen);
    }

    @Override
    public void mover(KeyHandler teclado) {
        int destinoX = rutaX[puntoActual];
        int destinoY = rutaY[puntoActual];

        // Calcular angulo hacia el destino
        double dx = destinoX - x;
        double dy = destinoY - y;
        double anguloDestino = Math.toDegrees(Math.atan2(dx, -dy));

        // Rotar suavemente hacia el destino
        double diff = anguloDestino - angulo;
        if (diff > 180) diff -= 360;
        if (diff < -180) diff += 360;
        angulo += diff * 0.08;

        // Acelerar
        velocidadActual += ACELERACION;
        if (velocidadActual > VEL_MAX) velocidadActual = VEL_MAX;

        // Mover en la direccion que apunta
        double rad = Math.toRadians(angulo);
        x += (int)(Math.sin(rad) * velocidadActual);
        y -= (int)(Math.cos(rad) * velocidadActual);

        // Pasar al siguiente punto si llego
        double distancia = Math.sqrt(dx * dx + dy * dy);
        if (distancia < 20) {
            puntoActual = (puntoActual + 1) % rutaX.length;
        }
    }

    @Override
    public void dibujar(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        AffineTransform transformOriginal = g2d.getTransform();

        g2d.rotate(Math.toRadians(angulo), x + 25, y + 15);

        if (imagen != null) {
            g2d.drawImage(imagen, x, y, 50, 30, null);
        } else {
            g2d.setColor(color);
            g2d.fillRect(x, y, 50, 30);
        }

        g2d.setTransform(transformOriginal);
        g.setColor(Color.WHITE);
        g.drawString(nombre, x, y - 5);
    }
}