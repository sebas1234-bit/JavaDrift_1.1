package com.javadrift;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public abstract class Vehiculo {

    protected int x;
    protected int y;
    protected int velocidad;
    protected Color color;
    protected String nombre;
    protected BufferedImage imagen;

    public Vehiculo(int x, int y, int velocidad, Color color, String nombre, String rutaImagen) {
        this.x = x;
        this.y = y;
        this.velocidad = velocidad;
        this.color = color;
        this.nombre = nombre;

        try {
            imagen = ImageIO.read(getClass().getClassLoader().getResourceAsStream("resources/" + rutaImagen));
        } catch (Exception e) {
            imagen = null;
        }
    }

    public abstract void mover(KeyHandler teclado);

    public void dibujar(Graphics g) {
        if (imagen != null) {
            g.drawImage(imagen, x, y, 36, 22, null);
        } else {
            g.setColor(this.color);
            g.fillRect(x, y, 36, 22);
        }
        g.setColor(Color.WHITE);
        g.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 11));
        g.drawString(nombre, x, y - 4);
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public String getNombre() { return nombre; }
}