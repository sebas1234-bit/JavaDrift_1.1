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
    int pistaActual = 0;

    public GamePanel(String nombreJugador, String rutaImagenJugador, int pistaSeleccionada) {
        this.setPreferredSize(new Dimension(ANCHO, ALTO));
        this.setBackground(Color.DARK_GRAY);
        this.setFocusable(true);
        this.addKeyListener(teclado);
        this.pistaActual = pistaSeleccionada;

        jugador = new CarroJugador(370, 80, rutaImagenJugador, nombreJugador);
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
        if (juegoTerminado && teclado.rPresionada) {
            boolean todosTerminaron = true;
            for (CarroRival rival : rivales) {
                if (!rival.termino) todosTerminaron = false;
            }
            if (todosTerminaron) volverAlLobby();
        }

        if (!juegoTerminado) {
            jugador.mover(teclado);
        }

        for (CarroRival rival : rivales) {
            if (!rival.termino) {
                rival.mover(teclado);
            }
        }

        if (!juegoTerminado) {
            for (CarroRival rival : rivales) {
                int dx = jugador.getX() - rival.getX();
                int dy = jugador.getY() - rival.getY();
                double dist = Math.sqrt(dx * dx + dy * dy);
                if (dist < 50) {
                    jugador.velocidadActual *= -0.5;
                    rival.velocidadActual *= -0.5;
                    jugador.x += dx > 0 ? 8 : -8;
                    jugador.y += dy > 0 ? 8 : -8;
                    rival.x -= dx > 0 ? 8 : -8;
                    rival.y -= dy > 0 ? 8 : -8;
                }
            }
        }

        for (int i = 0; i < rivales.size(); i++) {
            for (int j = i + 1; j < rivales.size(); j++) {
                CarroRival r1 = rivales.get(i);
                CarroRival r2 = rivales.get(j);
                if (r1.termino && r2.termino) continue;
                int dx = r1.getX() - r2.getX();
                int dy = r1.getY() - r2.getY();
                double dist = Math.sqrt(dx * dx + dy * dy);
                if (dist < 50) {
                    r1.velocidadActual *= -0.3;
                    r2.velocidadActual *= -0.3;
                    r1.x += dx > 0 ? 8 : -8;
                    r1.y += dy > 0 ? 8 : -8;
                    r2.x -= dx > 0 ? 8 : -8;
                    r2.y -= dy > 0 ? 8 : -8;
                }
            }
        }

        if (!juegoTerminado) {
            tiempoTranscurrido = System.currentTimeMillis() - tiempoInicio;
        }

        if (!juegoTerminado) {
            boolean enMetaAhora = jugador.getX() >= META_X &&
                    jugador.getX() <= META_X + META_ANCHO &&
                    jugador.getY() >= META_Y &&
                    jugador.getY() <= META_Y + META_ALTO;
            if (enMetaAhora && !jugadorEnMeta) {
                vueltasJugador++;
                jugadorEnMeta = true;
                if (vueltasJugador >= TOTAL_VUELTAS) {
                    tiempoTranscurrido = System.currentTimeMillis() - tiempoInicio;
                    juegoTerminado = true;
                }
            }
            if (!enMetaAhora) jugadorEnMeta = false;
        }

        for (CarroRival rival : rivales) {
            if (rival.termino) continue;
            boolean rivalEnMeta = rival.getX() >= META_X - 5 &&
                    rival.getX() <= META_X + META_ANCHO + 5 &&
                    rival.getY() >= META_Y &&
                    rival.getY() <= META_Y + META_ALTO;
            if (rivalEnMeta && !rival.enMeta && rival.vueltas < TOTAL_VUELTAS) {
                rival.vueltas++;
                rival.enMeta = true;
                if (rival.vueltas >= TOTAL_VUELTAS) {
                    rival.termino = true;
                    rival.tiempoFinal = System.currentTimeMillis() - tiempoInicio;
                }
            }
            if (!rivalEnMeta) rival.enMeta = false;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (pistaActual == 0) {
            g.setColor(new Color(80, 80, 80));
            g.fillRect(0, 0, ANCHO, ALTO);
            g.setColor(new Color(50, 150, 50));
            g.fillOval(50, 50, 700, 500);
            g.setColor(new Color(80, 80, 80));
            g.fillOval(150, 130, 500, 340);
            g.setColor(new Color(255, 255, 255, 80));
            g.drawOval(50, 50, 700, 500);
            g.drawOval(150, 130, 500, 340);
        } else {
            g.setColor(new Color(60, 60, 60));
            g.fillRect(0, 0, ANCHO, ALTO);
            g.setColor(new Color(50, 150, 50));
            g.fillRoundRect(50, 80, 700, 440, 120, 120);
            g.setColor(new Color(60, 60, 60));
            g.fillRoundRect(180, 180, 440, 240, 80, 80);
            g.setColor(new Color(255, 255, 255, 80));
            g.drawRoundRect(50, 80, 700, 440, 120, 120);
            g.drawRoundRect(180, 180, 440, 240, 80, 80);
        }

        g.setColor(Color.WHITE);
        g.fillRect(META_X, META_Y, META_ANCHO, META_ALTO);

        jugador.dibujar(g);
        for (CarroRival rival : rivales) {
            rival.dibujar(g);
        }

        g.setColor(new Color(0, 0, 0, 150));
        g.fillRoundRect(10, 10, 200, 100, 15, 15);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Vuelta: " + vueltasJugador + " / " + TOTAL_VUELTAS, 20, 35);
        g.drawString("Tiempo: " + (tiempoTranscurrido / 1000) + "s", 20, 60);

        int velActual = (int)(Math.abs(jugador.velocidadActual) * 20);
        g.drawString("Vel: " + velActual + " km/h", 20, 85);
        g.setColor(new Color(50, 50, 50));
        g.fillRoundRect(20, 92, 160, 12, 5, 5);
        g.setColor(velActual > 80 ? Color.RED : Color.GREEN);
        g.fillRoundRect(20, 92, Math.min(velActual * 160 / 100, 160), 12, 5, 5);

        // Zona de deteccion de meta para rivales (temporal para debug)
        g.setColor(new Color(255, 0, 0, 100));
        g.fillRect(META_X - 5, META_Y, META_ANCHO + 10, META_ALTO);

        if (juegoTerminado) {
            dibujarClasificacion(g);
        }
    }

    public void dibujarClasificacion(Graphics g) {
        boolean todosTerminaron = true;
        for (CarroRival rival : rivales) {
            if (!rival.termino) todosTerminaron = false;
        }

        if (!todosTerminaron) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRoundRect(200, 250, 400, 100, 20, 20);
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 28));
            g.drawString("Esperando rivales...", 230, 290);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 16));
            for (int i = 0; i < rivales.size(); i++) {
                CarroRival rival = rivales.get(i);
                g.drawString(rival.getNombre() + ": vuelta " +
                                rival.vueltas + "/" + TOTAL_VUELTAS,
                        230, 315 + (i * 25));
            }
            return;
        }

        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, ANCHO, ALTO);
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 45));
        g.drawString("CARRERA TERMINADA", 150, 100);

        String[] nombres = {jugador.getNombre(),
                rivales.get(0).getNombre(),
                rivales.get(1).getNombre()};
        long[] tiempos = {tiempoTranscurrido,
                rivales.get(0).tiempoFinal,
                rivales.get(1).tiempoFinal};

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2 - i; j++) {
                if (tiempos[j] > tiempos[j + 1]) {
                    long tempT = tiempos[j]; tiempos[j] = tiempos[j+1]; tiempos[j+1] = tempT;
                    String tempN = nombres[j]; nombres[j] = nombres[j+1]; nombres[j+1] = tempN;
                }
            }
        }

        Color[] coloresPodio = {Color.YELLOW, Color.LIGHT_GRAY, new Color(205, 127, 50)};
        String[] posiciones = {"1°", "2°", "3°"};
        int[] posY = {200, 300, 400};

        for (int i = 0; i < 3; i++) {
            g.setColor(coloresPodio[i]);
            g.setFont(new Font("Arial", Font.BOLD, 25));
            g.drawString(posiciones[i] + " " + nombres[i], 200, posY[i]);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Tiempo: " + (tiempos[i] / 1000) + "s", 220, posY[i] + 35);
        }

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