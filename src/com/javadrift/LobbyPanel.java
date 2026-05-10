package com.javadrift;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.imageio.ImageIO;

public class LobbyPanel extends JPanel implements KeyListener {

    final int ANCHO = 800;
    final int ALTO = 600;

    // Nombre
    String nombreJugador = "";

    // Colores y formas
    String[] colores = {"blue", "red", "green", "yellow", "black"};
    String[] nombresColores = {"Azul", "Rojo", "Verde", "Amarillo", "Negro"};
    int colorSeleccionado = 0;
    int formaSeleccionada = 0;
    final int TOTAL_FORMAS = 5;

    // Pistas
    String[] pistas = {"Pista Oval", "Pista Rapida"};
    int pistaSeleccionada = 0;

    // Selector activo: 0=nombre, 1=color, 2=forma, 3=pista
    int selectorActivo = 0;
    String[] etiquetasSelector = {"Nombre", "Color", "Forma", "Pista"};

    // Preview del carro
    BufferedImage imagenPreview;

    javax.swing.JFrame ventana;

    public LobbyPanel(javax.swing.JFrame ventana) {
        this.ventana = ventana;
        this.setPreferredSize(new Dimension(ANCHO, ALTO));
        this.setFocusable(true);
        this.addKeyListener(this);
        cargarPreview();
    }

    private void cargarPreview() {
        try {
            String ruta = "resources/car_" + colores[colorSeleccionado] +
                    "_" + (formaSeleccionada + 1) + ".png";
            imagenPreview = ImageIO.read(
                    getClass().getClassLoader().getResourceAsStream(ruta)
            );
        } catch (Exception e) {
            imagenPreview = null;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // Fondo
        g.setColor(new Color(30, 30, 30));
        g.fillRect(0, 0, ANCHO, ALTO);

        // Titulo
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 55));
        g.drawString("JAVADRIFT", 230, 70);

        // Instruccion de navegacion
        g.setColor(Color.LIGHT_GRAY);
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        g.drawString("ESPACIO = cambiar selector  |  FLECHAS = modificar valor  |  ENTER = iniciar", 110, 95);

        // Preview del carro
        g.setColor(new Color(50, 50, 50));
        g.fillRoundRect(580, 150, 180, 280, 20, 20);
        g.setColor(Color.GRAY);
        g.drawRoundRect(580, 150, 180, 280, 20, 20);
        if (imagenPreview != null) {
            g.drawImage(imagenPreview, 620, 220, 100, 60, null);
        }
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("Vista previa", 610, 180);
        g.setColor(Color.LIGHT_GRAY);
        g.setFont(new Font("Arial", Font.PLAIN, 13));
        g.drawString(nombresColores[colorSeleccionado] + " - Forma " +
                (formaSeleccionada + 1), 595, 390);

        // Opciones del lobby
        dibujarOpcion(g, 0, "Nombre", nombreJugador + "_", 130, 160);
        dibujarOpcion(g, 1, "Color", "< " + nombresColores[colorSeleccionado] + " >", 130, 250);
        dibujarOpcion(g, 2, "Forma del carro", "< Forma " + (formaSeleccionada + 1) + " >", 130, 340);
        dibujarOpcion(g, 3, "Pista", "< " + pistas[pistaSeleccionada] + " >", 130, 430);

        // Boton iniciar
        if (!nombreJugador.isEmpty()) {
            g.setColor(new Color(0, 180, 0));
            g.fillRoundRect(130, 500, 280, 50, 15, 15);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 22));
            g.drawString("ENTER para iniciar", 155, 532);
        } else {
            g.setColor(new Color(80, 80, 80));
            g.fillRoundRect(130, 500, 280, 50, 15, 15);
            g.setColor(Color.GRAY);
            g.setFont(new Font("Arial", Font.BOLD, 22));
            g.drawString("Escribe tu nombre", 155, 532);
        }
    }

    private void dibujarOpcion(Graphics g, int indice, String etiqueta,
                               String valor, int x, int y) {
        boolean activo = selectorActivo == indice;

        // Fondo de la opcion
        if (activo) {
            g.setColor(new Color(0, 120, 200, 180));
            g.fillRoundRect(x - 10, y - 30, 420, 65, 12, 12);
            g.setColor(Color.CYAN);
            g.drawRoundRect(x - 10, y - 30, 420, 65, 12, 12);
        } else {
            g.setColor(new Color(50, 50, 50, 180));
            g.fillRoundRect(x - 10, y - 30, 420, 65, 12, 12);
            g.setColor(new Color(100, 100, 100));
            g.drawRoundRect(x - 10, y - 30, 420, 65, 12, 12);
        }

        // Etiqueta
        g.setColor(activo ? Color.CYAN : Color.GRAY);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString(etiqueta, x, y - 8);

        // Valor
        g.setColor(activo ? Color.WHITE : new Color(180, 180, 180));
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString(valor, x, y + 22);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int codigo = e.getKeyCode();

        // Espacio cambia el selector activo
        if (codigo == KeyEvent.VK_SPACE) {
            selectorActivo = (selectorActivo + 1) % 4;
            repaint();
            return;
        }

        // ENTER inicia el juego
        if (codigo == KeyEvent.VK_ENTER && !nombreJugador.isEmpty()) {
            iniciarJuego();
            return;
        }

        // Borrar letra del nombre
        if (codigo == KeyEvent.VK_BACK_SPACE && selectorActivo == 0
                && nombreJugador.length() > 0) {
            nombreJugador = nombreJugador.substring(0, nombreJugador.length() - 1);
            repaint();
            return;
        }

        // Flechas segun selector activo
        if (selectorActivo == 1) {
            // Color
            if (codigo == KeyEvent.VK_LEFT) {
                colorSeleccionado--;
                if (colorSeleccionado < 0) colorSeleccionado = colores.length - 1;
            }
            if (codigo == KeyEvent.VK_RIGHT) {
                colorSeleccionado++;
                if (colorSeleccionado >= colores.length) colorSeleccionado = 0;
            }
            cargarPreview();
        } else if (selectorActivo == 2) {
            // Forma
            if (codigo == KeyEvent.VK_LEFT) {
                formaSeleccionada--;
                if (formaSeleccionada < 0) formaSeleccionada = TOTAL_FORMAS - 1;
            }
            if (codigo == KeyEvent.VK_RIGHT) {
                formaSeleccionada++;
                if (formaSeleccionada >= TOTAL_FORMAS) formaSeleccionada = 0;
            }
            cargarPreview();
        } else if (selectorActivo == 3) {
            // Pista
            if (codigo == KeyEvent.VK_LEFT) {
                pistaSeleccionada--;
                if (pistaSeleccionada < 0) pistaSeleccionada = pistas.length - 1;
            }
            if (codigo == KeyEvent.VK_RIGHT) {
                pistaSeleccionada++;
                if (pistaSeleccionada >= pistas.length) pistaSeleccionada = 0;
            }
        }

        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Solo escribir nombre cuando el selector de nombre esta activo
        if (selectorActivo == 0) {
            char c = e.getKeyChar();
            if (Character.isLetterOrDigit(c) && nombreJugador.length() < 12) {
                nombreJugador += c;
                repaint();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    private void iniciarJuego() {
        String rutaImagen = "car_" + colores[colorSeleccionado] +
                "_" + (formaSeleccionada + 1) + ".png";
        GamePanel gamePanel = new GamePanel(
                nombreJugador,
                rutaImagen,
                pistaSeleccionada
        );
        ventana.getContentPane().removeAll();
        ventana.getContentPane().add(gamePanel);
        ventana.revalidate();
        gamePanel.iniciarJuego();
        gamePanel.requestFocus();
    }
}