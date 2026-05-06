package com.javadrift;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

public class GamePanel extends JPanel implements Runnable {

    final int ANCHO = 800;
    final int ALTO = 600;

    int carroX = 375;
    int carroY = 275;
    int velocidad = 3;

    Thread hiloJuego;
    KeyHandler teclado = new KeyHandler();

    public GamePanel() {
        this.setPreferredSize(new Dimension(ANCHO, ALTO));
        this.setBackground(Color.DARK_GRAY);
        this.setFocusable(true);
        this.addKeyListener(teclado);
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
        if (teclado.arribaPresionada) carroY -= velocidad;
        if (teclado.abajoPresionada) carroY += velocidad;
        if (teclado.izquierdaPresionada) carroX -= velocidad;
        if (teclado.derechaPresionada) carroX += velocidad;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.GREEN);
        g.fillRect(0, 0, ANCHO, ALTO);

        g.setColor(Color.RED);
        g.fillRect(carroX, carroY, 50, 30);
    }
}





