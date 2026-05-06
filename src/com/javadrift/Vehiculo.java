package com.javadrift;

import java.awt.Color;
import java.awt.Graphics;


public abstract class Vehiculo {
    protected int x;
    protected int y;
    protected int velocidad;
    protected Color color;
    protected String nombre;

    public Vehiculo(int x, int y, int velocidad, Color color, String nombre) {
        this.x = x;
        this.y = y;
        this.velocidad = velocidad;
        this.color = color;
        this.nombre = nombre;
    }

    public abstract void mover(KeyHandler teclado);

    public void dibujar(Graphics g) {
        g.setColor(this.color);
        g.fillRect(x, y, 50, 30);
        g.setColor(Color.WHITE);
        g.drawString(nombre, x, y - 5);
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public String getNombre() { return nombre; }

}
