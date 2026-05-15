package com.javadrift.physics;

public class Vector2D {
    public double x, y;

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // Sumar otro vector (ej: Posicion + Velocidad)
    public void sumar(Vector2D v) {
        this.x += v.x;
        this.y += v.y;
    }

    // Restar otro vector (ej: direccion entre dos puntos)
    public void restar(Vector2D v) {
        this.x -= v.x;
        this.y -= v.y;
    }

    // Escalar el vector modificando el original (ej: Velocidad * friccion)
    public void escalar(double factor) {
        this.x *= factor;
        this.y *= factor;
    }

    // Retorna una copia escalada sin modificar el original (ej: empuje direccional)
    public Vector2D escalado(double factor) {
        return new Vector2D(this.x * factor, this.y * factor);
    }

    // Obtener la magnitud — distancia o rapidez del vector
    public double getMagnitud() {
        return Math.sqrt(x * x + y * y);
    }

    // Normalizar: retorna una copia del vector con magnitud 1
    // Util para obtener solo la direccion sin importar la distancia
    public Vector2D normalizar() {
        double mag = getMagnitud();
        if (mag == 0) return new Vector2D(0, 0);
        return new Vector2D(x / mag, y / mag);
    }

    // Distancia entre este vector y otro (ej: distancia entre dos carros)
    public double distanciaA(Vector2D otro) {
        double dx = this.x - otro.x;
        double dy = this.y - otro.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
}