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

    final int META_X = 350;
    final int META_Y = 55;
    final int META_ANCHO = 10;
    final int META_ALTO = 80;
    final int TOTAL_VUELTAS = 3;

    Thread hiloJuego;
    KeyHandler teclado = new KeyHandler();

    CarroJugador jugador;
    ArrayList<CarroRival> rivales = new ArrayList<>();

    int vueltasJugador = 0;
    boolean jugadorEnMeta = false;
    long tiempoInicio;
    long tiempoTranscurrido;
    boolean juegoTerminado = false;

    public GamePanel(String nombreJugador, Color colorJugador) {
        this.setPreferredSize(new Dimension(ANCHO, ALTO));
        this.setBackground(Color.DARK_GRAY);
        this.setFocusable(true);
        this.addKeyListener(teclado);

        jugador = new CarroJugador(370, 80, colorJugador, nombreJugador);
        rivales.add(new CarroRival(320, 80, Color.BLUE, "Rival 1", "car_black_4.png"));
        rivales.add(new CarroRival(270, 80, Color.RED, "Rival 2", "car_red_4.png"));

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
        if (juegoTerminado) {
            if (teclado.rPresionada) {
                volverAlLobby();
            }
            return;
        }

        jugador.mover(teclado);
        for (CarroRival rival : rivales) {
            rival.mover(teclado);
        }

        // Colision entre jugador y rivales
        // Colision jugador con rivales
        for (CarroRival rival : rivales) {
            int dx = jugador.getX() - rival.getX();
            int dy = jugador.getY() - rival.getY();
            double distancia = Math.sqrt(dx * dx + dy * dy);

            if (distancia < 50) {
                jugador.velocidadActual *= -0.5;
                rival.velocidadActual *= -0.5;
                jugador.x += dx > 0 ? 8 : -8;
                jugador.y += dy > 0 ? 8 : -8;
                rival.x -= dx > 0 ? 8 : -8;
                rival.y -= dy > 0 ? 8 : -8;
            }
        }

        // Colision entre rivales
        for (int i = 0; i < rivales.size(); i++) {
            for (int j = i + 1; j < rivales.size(); j++) {
                CarroRival r1 = rivales.get(i);
                CarroRival r2 = rivales.get(j);
                int dx = r1.getX() - r2.getX();
                int dy = r1.getY() - r2.getY();
                double distancia = Math.sqrt(dx * dx + dy * dy);

                if (distancia < 50) {
                    r1.velocidadActual *= -0.3;
                    r2.velocidadActual *= -0.3;
                    r1.x += dx > 0 ? 8 : -8;
                    r1.y += dy > 0 ? 8 : -8;
                    r2.x -= dx > 0 ? 8 : -8;
                    r2.y -= dy > 0 ? 8 : -8;
                }
            }
        }

        tiempoTranscurrido = System.currentTimeMillis() - tiempoInicio;

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

        // Fondo gris (area fuera de pista)
        g.setColor(new Color(80, 80, 80));
        g.fillRect(0, 0, ANCHO, ALTO);

        // Pista oval (area verde)
        g.setColor(new Color(50, 150, 50));
        g.fillOval(50, 50, 700, 500);

        // Interior de la pista (area gris interna)
        g.setColor(new Color(80, 80, 80));
        g.fillOval(150, 130, 500, 340);

        // Linea de meta
        g.setColor(Color.WHITE);
        g.fillRect(META_X, META_Y, META_ANCHO, META_ALTO);

        // Lineas de la pista
        g.setColor(new Color(255, 255, 255, 80));
        g.drawOval(50, 50, 700, 500);
        g.drawOval(150, 130, 500, 340);

        jugador.dibujar(g);
        for (CarroRival rival : rivales) {
            rival.dibujar(g);
        }

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Vuelta: " + vueltasJugador + " / " + TOTAL_VUELTAS, 20, 30);
        g.drawString("Tiempo: " + (tiempoTranscurrido / 1000) + "s", 20, 55);

        if (juegoTerminado) {
            dibujarClasificacion(g);
        }
    }

    public void dibujarClasificacion(Graphics g) {
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, ANCHO, ALTO);

        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 45));
        g.drawString("CARRERA TERMINADA", 150, 100);

        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 25));
        g.drawString("1° " + jugador.getNombre(), 200, 200);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("Tiempo: " + (tiempoTranscurrido / 1000) + "s", 220, 235);

        g.setColor(Color.LIGHT_GRAY);
        g.setFont(new Font("Arial", Font.BOLD, 25));
        g.drawString("2° " + rivales.get(0).getNombre(), 200, 300);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("Tiempo: " + ((tiempoTranscurrido / 1000) + 5) + "s", 220, 335);

        g.setColor(new Color(205, 127, 50));
        g.setFont(new Font("Arial", Font.BOLD, 25));
        g.drawString("3° " + rivales.get(1).getNombre(), 200, 400);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("Tiempo: " + ((tiempoTranscurrido / 1000) + 10) + "s", 220, 435);

        g.setColor(Color.GREEN);
        g.setFont(new Font("Arial", Font.BOLD, 22));
        g.drawString("Presiona R para volver al lobby", 220, 530);
    }

    private void volverAlLobby() {
        javax.swing.JFrame ventana = (javax.swing.JFrame)
                javax.swing.SwingUtilities.getWindowAncestor(this);
        LobbyPanel lobby = new LobbyPanel(ventana);
        ventana.getContentPane().removeAll();
        ventana.getContentPane().add(lobby);
        ventana.revalidate();
        lobby.requestFocus();
        hiloJuego = null;
    }
}