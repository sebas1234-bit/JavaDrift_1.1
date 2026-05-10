package com.javadrift;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class LobbyPanel extends JPanel implements KeyListener {

    final int ANCHO = 800;
    final int ALTO = 600;

    // Colores disponibles para el carro
    Color[] colores = {Color.RED, Color.BLUE, Color.YELLOW, Color.ORANGE, Color.PINK};
    String[] nombresColores = {"Rojo", "Azul", "Amarillo", "Naranja", "Rosado"};
    int colorSeleccionado = 0;
    String[] pistas = {"Pista Ovalo", "Pista rapida"};
    int pistaSeleccionada = 0;

    // Nombre del jugador
    String nombreJugador = "";

    // Referencia a la ventana principal
    javax.swing.JFrame ventana;

    public LobbyPanel(javax.swing.JFrame ventana) {
        this.ventana = ventana;
        this.setPreferredSize(new Dimension(ANCHO, ALTO));
        this.setFocusable(true);
        this.addKeyListener(this);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Fondo
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, ANCHO, ALTO);

        // Titulo
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 50));
        g.drawString("JAVADRIFT", 250, 100);

        // Nombre del jugador
        g.setFont(new Font("Arial", Font.BOLD, 22));
        g.drawString("Nombre: " + nombreJugador + "_", 250, 200);

        // Selector de color
        g.drawString("Color del carro:", 250, 270);
        g.setColor(colores[colorSeleccionado]);
        g.fillRect(250, 290, 80, 40);
        g.setColor(Color.WHITE);
        g.drawString(nombresColores[colorSeleccionado], 350, 318);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("(flechas izquierda/derecha para cambiar)", 250, 350);

        // Selector de pista
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 22));
        g.drawString("Pista:", 250, 400);
        g.setColor(Color.CYAN);
        g.drawString("< " + pistas[pistaSeleccionada] + " >", 320, 400);
        g.setColor(Color.LIGHT_GRAY);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("(flechas arriba/abajo para cambiar pista)", 250, 425);

        // Instrucciones
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.setColor(Color.GREEN);
        g.drawString("ENTER para iniciar la carrera", 250, 450);

        g.setColor(Color.LIGHT_GRAY);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("Escribe tu nombre y elige tu color", 250, 500);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int codigo = e.getKeyCode();

        // Cambiar color con flechas
        if (codigo == KeyEvent.VK_LEFT) {
            colorSeleccionado--;
            if (colorSeleccionado < 0) colorSeleccionado = colores.length - 1;
        }
        if (codigo == KeyEvent.VK_RIGHT) {
            colorSeleccionado++;
            if (colorSeleccionado >= colores.length) colorSeleccionado = 0;
        }

        // Escribir nombre con teclado
        if (codigo == KeyEvent.VK_BACK_SPACE && nombreJugador.length() > 0) {
            nombreJugador = nombreJugador.substring(0, nombreJugador.length() - 1);
        }

        // Iniciar juego con Enter
        if (codigo == KeyEvent.VK_ENTER && !nombreJugador.isEmpty()) {
            iniciarJuego();
        }

        //Selector de pista
        if (codigo == KeyEvent.VK_UP) {
            pistaSeleccionada--;
            if (pistaSeleccionada < 0) pistaSeleccionada = pistas.length - 1;
        }
        if (codigo == KeyEvent.VK_DOWN) {
            pistaSeleccionada++;
            if (pistaSeleccionada >= pistas.length) pistaSeleccionada = 0;
        }

        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        char c = e.getKeyChar();
        if (Character.isLetterOrDigit(c) && nombreJugador.length() < 12) {
            nombreJugador += c;
        }
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    private void iniciarJuego() {
        GamePanel gamePanel = new GamePanel(nombreJugador, colores[colorSeleccionado], pistaSeleccionada);

        ventana.getContentPane().removeAll();
        ventana.getContentPane().add(gamePanel);
        ventana.revalidate();
        gamePanel.iniciarJuego();
        gamePanel.requestFocus();
    }
}