package com.javadrift;

import com.javadrift.physics.Vector2D;
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

    // Colores de la pista — asfalto gris es donde se corre, verde es pasto
    final Color COLOR_ASFALTO_OVAL   = new Color(80, 80, 80);
    final Color COLOR_ASFALTO_RAPIDA = new Color(60, 60, 60);
    final Color COLOR_PASTO          = new Color(50, 150, 50);

    // Centro de la pista usado como referencia para el empuje vectorial
    final Vector2D CENTRO_PISTA = new Vector2D(400, 300);

    // Checkpoints pista Oval — derecha, abajo, izquierda
    final int[][] CHECKPOINTS_OVAL = {
            {680, 230, 80, 10}, // derecha
            {480, 490, 10, 80}, // abajo
            {100, 230, 80, 10}, // izquierda
    };

    // Checkpoints pista Rapida — derecha, abajo, izquierda, arriba
    final int[][] CHECKPOINTS_RAPIDA = {
            {710, 270, 50, 10}, // derecha
            {380, 510, 10, 60}, // abajo
            {100, 270, 50, 10}, // izquierda
            {380, 90,  10, 60}, // arriba (antes de meta)
    };

    Thread hiloJuego;
    KeyHandler teclado = new KeyHandler();
    CarroJugador jugador;
    ArrayList<CarroRival> rivales = new ArrayList<>();

    int vueltasJugador = 0;
    boolean jugadorEnMeta = false;
    int checkpointActualJugador = 0;

    long tiempoInicio;
    long tiempoTranscurrido;
    boolean juegoTerminado = false;
    int pistaActual = 0;

    // Imagen de la pista capturada una sola vez al iniciar
    java.awt.image.BufferedImage pistaCacheada;

    public GamePanel(String nombreJugador, String rutaImagenJugador, int pistaSeleccionada) {
        this.setPreferredSize(new Dimension(ANCHO, ALTO));
        this.setBackground(Color.DARK_GRAY);
        this.setFocusable(true);
        this.addKeyListener(teclado);
        this.pistaActual = pistaSeleccionada;

        jugador = new CarroJugador(370, 80, rutaImagenJugador, nombreJugador);
        rivales.add(new CarroRival(300, 80, Color.BLUE, "Rival 1", "car_black_4.png"));
        rivales.add(new CarroRival(230, 80, Color.RED, "Rival 2", "car_red_4.png"));

        tiempoInicio = System.currentTimeMillis();
        pistaCacheada = capturarPista();
    }

    // Retorna los checkpoints de la pista actual
    private int[][] getCheckpoints() {
        return pistaActual == 0 ? CHECKPOINTS_OVAL : CHECKPOINTS_RAPIDA;
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
        // Volver al lobby solo si todos terminaron y se presiona R
        if (juegoTerminado && teclado.rPresionada) {
            boolean todosTerminaron = true;
            for (CarroRival rival : rivales) {
                if (!rival.termino) todosTerminaron = false;
            }
            if (todosTerminaron) volverAlLobby();
        }

        // Mover jugador solo si la carrera no termino
        if (!juegoTerminado) {
            jugador.mover(teclado);
        }

        // Mover rivales si no han terminado
        for (CarroRival rival : rivales) {
            if (!rival.termino) {
                rival.mover(teclado);
            }
        }

        // Colision jugador con rivales usando Vector2D
        if (!juegoTerminado) {
            for (CarroRival rival : rivales) {
                Vector2D posJugador = new Vector2D(jugador.getX(), jugador.getY());
                Vector2D posRival   = new Vector2D(rival.getX(),   rival.getY());
                double distancia = posJugador.distanciaA(posRival);

                if (distancia < 50 && distancia > 0) {
                    Vector2D separacion = new Vector2D(
                            jugador.getX() - rival.getX(),
                            jugador.getY() - rival.getY()
                    );
                    Vector2D direccion = separacion.normalizar();

                    jugador.velocidadActual *= -0.5;
                    rival.velocidadActual   *= -0.5;

                    Vector2D empujeJugador = direccion.escalado(8);
                    Vector2D empujeRival   = direccion.escalado(-8);

                    jugador.x += (int) empujeJugador.x;
                    jugador.y += (int) empujeJugador.y;
                    rival.x   += (int) empujeRival.x;
                    rival.y   += (int) empujeRival.y;
                }
            }
        }

        // Colision entre rivales usando Vector2D
        for (int i = 0; i < rivales.size(); i++) {
            for (int j = i + 1; j < rivales.size(); j++) {
                CarroRival r1 = rivales.get(i);
                CarroRival r2 = rivales.get(j);
                if (r1.termino && r2.termino) continue;

                Vector2D pos1 = new Vector2D(r1.getX(), r1.getY());
                Vector2D pos2 = new Vector2D(r2.getX(), r2.getY());
                double distancia = pos1.distanciaA(pos2);

                if (distancia < 35 && distancia > 0) {
                    Vector2D separacion = new Vector2D(
                            r1.getX() - r2.getX(),
                            r1.getY() - r2.getY()
                    );
                    Vector2D direccion = separacion.normalizar();

                    r1.velocidadActual *= -0.3;
                    r2.velocidadActual *= -0.3;

                    Vector2D empuje1 = direccion.escalado(8);
                    Vector2D empuje2 = direccion.escalado(-8);

                    r1.x += (int) empuje1.x;
                    r1.y += (int) empuje1.y;
                    r2.x += (int) empuje2.x;
                    r2.y += (int) empuje2.y;
                }
            }
        }

        // Colision con bordes de pista usando empuje vectorial
        if (carroPisaPasto(pistaCacheada, jugador.getX(), jugador.getY())) {
            jugador.velocidadActual *= -0.4;
            empujarAlAsfalto(jugador);
        }

        for (CarroRival rival : rivales) {
            if (carroPisaPasto(pistaCacheada, rival.getX(), rival.getY())) {
                rival.velocidadActual *= 0.3;
                empujarAlAsfalto(rival);
            }
        }

        // Actualizar tiempo mientras la carrera sigue
        if (!juegoTerminado) {
            tiempoTranscurrido = System.currentTimeMillis() - tiempoInicio;
        }

        // Detectar checkpoints del jugador en orden segun pista actual
        if (!juegoTerminado) {
            int[][] checkpoints = getCheckpoints();
            if (checkpointActualJugador < checkpoints.length) {
                int[] cp = checkpoints[checkpointActualJugador];
                boolean enCheckpoint = jugador.getX() >= cp[0] &&
                        jugador.getX() <= cp[0] + cp[2] &&
                        jugador.getY() >= cp[1] &&
                        jugador.getY() <= cp[1] + cp[3];
                if (enCheckpoint) {
                    checkpointActualJugador++;
                }
            }
        }

        // Detectar vuelta del jugador — solo cuenta si paso todos los checkpoints
        if (!juegoTerminado) {
            boolean enMetaAhora = jugador.getX() >= META_X &&
                    jugador.getX() <= META_X + META_ANCHO &&
                    jugador.getY() >= META_Y &&
                    jugador.getY() <= META_Y + META_ALTO;

            if (enMetaAhora && !jugadorEnMeta) {
                if (checkpointActualJugador >= getCheckpoints().length) {
                    vueltasJugador++;
                    checkpointActualJugador = 0;
                    if (vueltasJugador >= TOTAL_VUELTAS) {
                        tiempoTranscurrido = System.currentTimeMillis() - tiempoInicio;
                        juegoTerminado = true;
                    }
                }
                jugadorEnMeta = true;
            }
            if (!enMetaAhora) jugadorEnMeta = false;
        }

        // Detectar vuelta de los rivales
        for (CarroRival rival : rivales) {
            if (rival.termino) continue;
            boolean rivalEnMeta = rival.getX() >= META_X - 60 &&
                    rival.getX() <= META_X + META_ANCHO + 60 &&
                    rival.getY() >= META_Y - 30 &&
                    rival.getY() <= META_Y + META_ALTO + 30;
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

    // Empuja el vehiculo fuera del pasto usando Vector2D puro sin ajuste manual de ejes
    private void empujarAlAsfalto(Vehiculo v) {
        Vector2D posCarro = new Vector2D(v.getX() + 25, v.getY() + 15);
        Vector2D haciaCarro = new Vector2D(
                posCarro.x - CENTRO_PISTA.x,
                posCarro.y - CENTRO_PISTA.y
        );

        double distancia = haciaCarro.getMagnitud();
        if (distancia == 0) return;

        Vector2D direccion = haciaCarro.normalizar();
        int signo = distancia > 200 ? -1 : 1;

        double tx = v.x;
        double ty = v.y;

        for (int i = 0; i < 80; i++) {
            tx += signo * direccion.x;
            ty += signo * direccion.y;
            v.x = (int) tx;
            v.y = (int) ty;
            if (!carroPisaPasto(pistaCacheada, v.getX(), v.getY())) break;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (pistaActual == 0) {
            g.setColor(COLOR_PASTO);
            g.fillRect(0, 0, ANCHO, ALTO);
            g.setColor(COLOR_ASFALTO_OVAL);
            g.fillOval(30, 30, 740, 540);
            g.setColor(COLOR_PASTO);
            g.fillOval(180, 150, 440, 300);
            g.setColor(new Color(255, 255, 255, 80));
            g.drawOval(30, 30, 740, 540);
            g.drawOval(180, 150, 440, 300);
        } else {
            g.setColor(COLOR_PASTO);
            g.fillRect(0, 0, ANCHO, ALTO);
            g.setColor(COLOR_ASFALTO_RAPIDA);
            g.fillRoundRect(30, 50, 740, 500, 140, 140);
            g.setColor(COLOR_PASTO);
            g.fillRoundRect(200, 190, 400, 220, 80, 80);
            g.setColor(new Color(255, 255, 255, 80));
            g.drawRoundRect(30, 50, 740, 500, 140, 140);
            g.drawRoundRect(200, 190, 400, 220, 80, 80);
        }

        // Linea de meta
        g.setColor(Color.WHITE);
        g.fillRect(META_X, META_Y, META_ANCHO, META_ALTO);

        // Dibujar checkpoints de la pista actual
        int[][] checkpoints = getCheckpoints();
        for (int i = 0; i < checkpoints.length; i++) {
            int[] cp = checkpoints[i];
            if (i == checkpointActualJugador) {
                // Checkpoint actual — resaltado en cyan
                g.setColor(new Color(0, 255, 255, 150));
            } else if (i < checkpointActualJugador) {
                // Ya pasado — gris transparente
                g.setColor(new Color(150, 150, 150, 80));
            } else {
                // Pendiente — amarillo transparente
                g.setColor(new Color(255, 255, 0, 80));
            }
            g.fillRect(cp[0], cp[1], cp[2], cp[3]);
        }

        // Dibujar carros
        jugador.dibujar(g);
        for (CarroRival rival : rivales) {
            rival.dibujar(g);
        }

        // HUD - fondo
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRoundRect(10, 10, 220, 115, 15, 15);

        // HUD - textos
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Vuelta: " + vueltasJugador + " / " + TOTAL_VUELTAS, 20, 35);
        g.drawString("Tiempo: " + (tiempoTranscurrido / 1000) + "s", 20, 60);

        // Indicador de checkpoints
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        g.setColor(checkpointActualJugador >= checkpoints.length ? Color.GREEN : Color.CYAN);
        g.drawString("CP: " + checkpointActualJugador + " / " + checkpoints.length, 20, 82);

        // Velocimetro
        int velActual = (int)(Math.abs(jugador.velocidadActual) * 20);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Vel: " + velActual + " km/h", 20, 102);
        g.setColor(new Color(50, 50, 50));
        g.fillRoundRect(20, 107, 160, 12, 5, 5);
        g.setColor(velActual > 80 ? Color.RED : Color.GREEN);
        g.fillRoundRect(20, 107, Math.min(velActual * 160 / 100, 160), 12, 5, 5);

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

        // Ordenar clasificacion por tiempo (bubble sort)
        String[] nombres = {jugador.getNombre(),
                rivales.get(0).getNombre(),
                rivales.get(1).getNombre()};
        long[] tiempos = {tiempoTranscurrido,
                rivales.get(0).tiempoFinal,
                rivales.get(1).tiempoFinal};

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2 - i; j++) {
                if (tiempos[j] > tiempos[j + 1]) {
                    long tempT = tiempos[j]; tiempos[j] = tiempos[j + 1]; tiempos[j + 1] = tempT;
                    String tempN = nombres[j]; nombres[j] = nombres[j + 1]; nombres[j + 1] = tempN;
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

    // Dibuja la pista en imagen invisible para leer colores de pixeles
    private java.awt.image.BufferedImage capturarPista() {
        java.awt.image.BufferedImage imagen =
                new java.awt.image.BufferedImage(ANCHO, ALTO,
                        java.awt.image.BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics g = imagen.getGraphics();
        if (pistaActual == 0) {
            g.setColor(COLOR_PASTO);
            g.fillRect(0, 0, ANCHO, ALTO);
            g.setColor(COLOR_ASFALTO_OVAL);
            g.fillOval(30, 30, 740, 540);
            g.setColor(COLOR_PASTO);
            g.fillOval(180, 150, 440, 300);
        } else {
            g.setColor(COLOR_PASTO);
            g.fillRect(0, 0, ANCHO, ALTO);
            g.setColor(COLOR_ASFALTO_RAPIDA);
            g.fillRoundRect(30, 50, 740, 500, 140, 140);
            g.setColor(COLOR_PASTO);
            g.fillRoundRect(200, 190, 400, 220, 80, 80);
        }
        g.dispose();
        return imagen;
    }

    // Revisa 9 puntos del carro para detectar si alguno pisa pasto
    private boolean carroPisaPasto(java.awt.image.BufferedImage pista, int x, int y) {
        int[][] puntos = {
                {x + 25, y + 15}, // centro
                {x + 2,  y + 2},  // esquina superior izquierda
                {x + 48, y + 2},  // esquina superior derecha
                {x + 2,  y + 28}, // esquina inferior izquierda
                {x + 48, y + 28}, // esquina inferior derecha
                {x + 25, y + 2},  // punto medio arriba
                {x + 25, y + 28}, // punto medio abajo
                {x + 2,  y + 15}, // punto medio izquierda
                {x + 48, y + 15}  // punto medio derecha
        };
        for (int[] p : puntos) {
            if (p[0] < 0 || p[1] < 0 || p[0] >= ANCHO || p[1] >= ALTO) return true;
            if (pista.getRGB(p[0], p[1]) == COLOR_PASTO.getRGB()) return true;
        }
        return false;
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