package com.javadrift;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;

public class CarroRival extends Vehiculo {

    // Rival 1 — ruta exterior (margen 40px dentro del borde externo)
    private static final int[] RUTA_OVAL_EXT_X = {
            390, 460, 540, 615, 672, 712, 730, 725, 700,
            658, 600, 530, 450, 365, 280, 200, 135, 90,
            68,  70,  95,  140, 200, 275, 355
    };
    private static final int[] RUTA_OVAL_EXT_Y = {
            75,  68,  72,  90,  118, 158, 208, 265, 320,
            368, 405, 432, 448, 454, 450, 435, 405, 365,
            310, 250, 195, 152, 120, 95,  76
    };

    // Rival 2 — ruta interior (margen 35px fuera del borde interno)
    private static final int[] RUTA_OVAL_INT_X = {
            390, 445, 505, 555, 592, 612, 610, 593, 562,
            520, 468, 410, 350, 292, 242, 205, 185, 183,
            200, 232, 275, 328, 373
    };
    private static final int[] RUTA_OVAL_INT_Y = {
            155, 148, 157, 175, 202, 235, 270, 305, 334,
            355, 368, 373, 370, 358, 336, 305, 268, 230,
            197, 170, 152, 148, 150
    };

    // Rival 1 — ruta exterior pista rapida
    private static final int[] RUTA_RAP_EXT_X = {
            390, 465, 555, 635, 695, 732, 745, 738, 710,
            655, 565, 455, 350, 242, 152, 88,  55,  48,
            68,  120, 218, 320
    };
    private static final int[] RUTA_RAP_EXT_Y = {
            75,  65,  62,  72,  100, 142, 200, 268, 338,
            398, 442, 462, 465, 452, 418, 368, 298, 220,
            148, 98,  70,  68
    };

    // Rival 2 — ruta interior pista rapida
    private static final int[] RUTA_RAP_INT_X = {
            390, 445, 512, 568, 608, 628, 628, 610, 580,
            535, 472, 400, 328, 265, 215, 185, 178, 195,
            228, 278, 342, 385
    };
    private static final int[] RUTA_RAP_INT_Y = {
            198, 192, 198, 212, 238, 270, 308, 342, 368,
            388, 400, 404, 398, 378, 348, 308, 265, 225,
            200, 196, 198, 198
    };

    private int[] rutaX;
    private int[] rutaY;
    private int puntoActual = 0;
    double angulo = 0;
    public double velocidadActual = 0;
    public int vueltas = 0;
    public boolean enMeta = false;
    public boolean termino = false;
    public long tiempoFinal = 0;
    final double VEL_MAX;
    final double ACELERACION;
    private int pista;

    public CarroRival(int x, int y, Color color, String nombre,
                      String rutaImagen, int pista) {
        super(x, y, 2, color, nombre, rutaImagen);
        this.pista = pista;
        this.angulo = 90;
        this.puntoActual = 0;

        if (pista == 0) {
            // Oval: negro exterior, naranja interior
            if (nombre.equals("Rival 1")) {
                rutaX = RUTA_OVAL_EXT_X;
                rutaY = RUTA_OVAL_EXT_Y;
            } else {
                rutaX = RUTA_OVAL_INT_X;
                rutaY = RUTA_OVAL_INT_Y;
            }
        } else {
            // Rapida: negro exterior, naranja interior
            if (nombre.equals("Rival 1")) {
                rutaX = RUTA_RAP_EXT_X;
                rutaY = RUTA_RAP_EXT_Y;
            } else {
                rutaX = RUTA_RAP_INT_X;
                rutaY = RUTA_RAP_INT_Y;
            }
        }

        if (nombre.equals("Rival 1")) {
            VEL_MAX = 3.5;
            ACELERACION = 0.12;
        } else {
            VEL_MAX = 4.5;
            ACELERACION = 0.18;
        }
    }

    @Override
    public void mover(KeyHandler teclado) {
        int destinoX = rutaX[puntoActual];
        int destinoY = rutaY[puntoActual];

        double dx = destinoX - x;
        double dy = destinoY - y;
        double anguloDestino = Math.toDegrees(Math.atan2(dx, -dy));

        double diff = anguloDestino - angulo;
        if (diff > 180) diff -= 360;
        if (diff < -180) diff += 360;
        angulo += diff * 0.08;

        velocidadActual += ACELERACION;
        if (velocidadActual > VEL_MAX) velocidadActual = VEL_MAX;
        if (velocidadActual < 0.8) velocidadActual = 0.8;

        double rad = Math.toRadians(angulo);
        x += (int)(Math.sin(rad) * velocidadActual);
        y -= (int)(Math.cos(rad) * velocidadActual);

        double distancia = Math.sqrt(dx * dx + dy * dy);
        if (distancia < 25) {
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
            g2d.drawImage(imagen, x, y, 36, 22, null);
        } else {
            g2d.setColor(color);
            g2d.fillRect(x, y, 50, 30);
        }

        g2d.setTransform(transformOriginal);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 12));
        g2d.drawString(nombre, x, y - 5);
    }
}