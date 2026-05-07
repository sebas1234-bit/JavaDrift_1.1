package com.javadrift;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;

public class GamePanel extends JPanel implements Runnable {

    final int ANCHO = 800;
    final int ALTO = 600;

    // Meta: rectangulo invisible que detecta cuando el carro la cruza
    final int META_X = 350;
    final int META_Y = 250;
    final int META_ANCHO = 10;
    final int META_ALTO = 80;
    final int TOTAL_VUELTAS = 3;

    Thread hiloJuego;
    KeyHandler teclado = new KeyHandler();

    CarroJugador jugador = new CarroJugador(375, 275);
    ArrayList<CarroRival> rivales = new ArrayList<>();

    // Contador de vueltas y cronometro
    int vueltasJugador = 0;
    boolean jugadorEnMeta = false;
    long tiempoInicio;
    long tiempoTranscurrido;
    boolean juegoTerminado = false;

    public GamePanel() {
        this.setPreferredSize(new Dimension(ANCHO, ALTO));
        this.setBackground(Color.DARK_GRAY);
        this.setFocusable(true);
        this.addKeyListener(teclado);

        rivales.add(new CarroRival(100, 100, Color.BLUE, "Rival 1"));
        rivales.add(new CarroRival(200, 100, Color.YELLOW, "Rival 2"));

        tiempoInicio = System.currentTimeMillis();
    }

    public void iniciarJuego() {
        hiloJuego = new Thread(this);
        hiloJuego.start();
    }

    @Override
    public void run() {
        while (hiloJuego != null) {
            actualizar();
            repaint();
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void actualizar() {
        if (juegoTerminado) return;

        jugador.mover(teclado);
        for (CarroRival rival : rivales) {
            rival.mover(teclado);
        }

        // Actualizar cronometro
        tiempoTranscurrido = System.currentTimeMillis() - tiempoInicio;

        // Detectar si el jugador cruza la meta
        boolean enMetaAhora = jugador.getX() >= META_X &&
                jugador.getX() <= META_X + META_ANCHO &&
                jugador.getY() >= META_Y &&
                jugador.getY() <= META_Y + META_ALTO;

        if (enMetaAhora && !jugadorEnMeta) {
            vueltasJugador++;
            jugadorEnMeta = true;
            if (vueltasJugador >= TOTAL_VUELTAS) {
                juegoTerminado = true;
            }
        }

        if (!enMetaAhora) {
            jugadorEnMeta = false;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Fondo verde
        g.setColor(Color.GREEN);
        g.fillRect(0, 0, ANCHO, ALTO);

        // Dibujar meta
        g.setColor(Color.WHITE);
        g.fillRect(META_X, META_Y, META_ANCHO, META_ALTO);

        // Dibujar carros
        jugador.dibujar(g);
        for (CarroRival rival : rivales) {
            rival.dibujar(g);
        }

        // HUD - informacion en pantalla
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Vuelta: " + vueltasJugador + " / " + TOTAL_VUELTAS, 20, 30);
        g.drawString("Tiempo: " + (tiempoTranscurrido / 1000) + "s", 20, 55);

        // Pantalla de fin de juego
        if (juegoTerminado) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, ANCHO, ALTO);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("¡Ganaste!", 300, 250);
            g.setFont(new Font("Arial", Font.BOLD, 25));
            g.drawString("Tiempo: " + (tiempoTranscurrido / 1000) + "s", 320, 300);
        }
    }
}