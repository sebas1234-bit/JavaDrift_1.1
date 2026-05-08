package com.javadrift.physics;

public class Vector2D {
    public double x, y;

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // Sumar otro vector (ej: Posición + Velocidad)
    public void sumar(Vector2D v) {
        this.x += v.x;
        this.y += v.y;
    }

    // Escalar el vector (ej: Velocidad * DeltaTime)
    public void escalar(double factor) {
        this.x *= factor;
        this.y *= factor;
    }

    // Obtener la magnitud (velocidad actual en km/h o px/s)
    public double getMagnitud() {
        return Math.sqrt(x * x + y * y);
    }
}