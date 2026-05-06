package com.javadrift;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

public class GamePanel extends JPanel implements Runnable {

    final int ANCHO = 800;
    final int ALTO = 600;

    Thread hiloJuego;
    KeyHandler teclado = new KeyHandler();

    CarroJugador jugador = new CarroJugador(375, 275);

    // Lista de rivales - polimorfismo en accion
    ArrayList<CarroRival> rivales = new ArrayList<>();

    public GamePanel() {
        this.setPreferredSize(new Dimension(ANCHO, ALTO));
        this.setBackground(Color.DARK_GRAY);
        this.setFocusable(true);
        this.addKeyListener(teclado);

        // Crear dos rivales con diferentes colores y nombres
        rivales.add(new CarroRival(100, 100, Color.BLUE, "Rival 1"));
        rivales.add(new CarroRival(200, 100, Color.YELLOW, "Rival 2"));
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
        // Mover jugador
        jugador.mover(teclado);

        // Mover cada rival autonomamente
        for (CarroRival rival : rivales) {
            rival.mover(teclado);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Fondo verde
        g.setColor(Color.GREEN);
        g.fillRect(0, 0, ANCHO, ALTO);

        // Dibujar jugador
        jugador.dibujar(g);

        // Dibujar cada rival
        for (CarroRival rival : rivales) {
            rival.dibujar(g);
        }
    }
}