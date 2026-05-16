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
    int cuentaRegresiva = 3;
    long tiempoUltimaCuenta;
    boolean carreraIniciada = false;
    int pistaActual = 0;

    // Imagen de la pista capturada una sola vez al iniciar
    java.awt.image.BufferedImage pistaCacheada;

    public GamePanel(String nombreJugador, String rutaImagenJugador, int pistaSeleccionada) {
        this.setPreferredSize(new Dimension(ANCHO, ALTO));
        this.setBackground(Color.DARK_GRAY);
        this.setFocusable(true);
        this.addKeyListener(teclado);
        this.pistaActual = pistaSeleccionada;

        jugador = new CarroJugador(390, 75, rutaImagenJugador, nombreJugador);
        rivales.add(new CarroRival(390, 108, Color.BLUE, "Rival 1", "car_black_4.png", pistaActual));
        rivales.add(new CarroRival(390, 141, Color.RED, "Rival 2", "car_red_4.png", pistaActual));

        tiempoInicio = System.currentTimeMillis();
        pistaCacheada = capturarPista();
        tiempoUltimaCuenta = System.currentTimeMillis();
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
        // Cuenta regresiva antes de arrancar
        if (!carreraIniciada) {
            long ahora = System.currentTimeMillis();
            if (ahora - tiempoUltimaCuenta >= 1000) {
                cuentaRegresiva--;
                tiempoUltimaCuenta = ahora;
                if (cuentaRegresiva <= 0) {
                    carreraIniciada = true;
                    tiempoInicio = System.currentTimeMillis();
                }
            }
            return;
        }

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

        // Colision jugador con rivales — separacion sin rebote
        if (!juegoTerminado) {
            for (CarroRival rival : rivales) {
                Vector2D posJugador = new Vector2D(jugador.getX(), jugador.getY());
                Vector2D posRival   = new Vector2D(rival.getX(),   rival.getY());
                double distancia = posJugador.distanciaA(posRival);

                if (distancia < 36 && distancia > 0) {
                    Vector2D separacion = new Vector2D(
                            jugador.getX() - rival.getX(),
                            jugador.getY() - rival.getY()
                    );
                    Vector2D direccion = separacion.normalizar();
                    double overlap = 36 - distancia;

                    jugador.x += (int)(direccion.x * overlap * 0.5);
                    jugador.y += (int)(direccion.y * overlap * 0.5);
                    rival.x   -= (int)(direccion.x * overlap * 0.5);
                    rival.y   -= (int)(direccion.y * overlap * 0.5);
                }
            }
        }

        // Colision entre rivales — separacion sin rebote
        for (int i = 0; i < rivales.size(); i++) {
            for (int j = i + 1; j < rivales.size(); j++) {
                CarroRival r1 = rivales.get(i);
                CarroRival r2 = rivales.get(j);
                if (r1.termino && r2.termino) continue;

                Vector2D pos1 = new Vector2D(r1.getX(), r1.getY());
                Vector2D pos2 = new Vector2D(r2.getX(), r2.getY());
                double distancia = pos1.distanciaA(pos2);

                if (distancia < 36 && distancia > 0) {
                    Vector2D separacion = new Vector2D(
                            r1.getX() - r2.getX(),
                            r1.getY() - r2.getY()
                    );
                    Vector2D direccion = separacion.normalizar();
                    double overlap = 36 - distancia;

                    r1.x += (int)(direccion.x * overlap * 0.5);
                    r1.y += (int)(direccion.y * overlap * 0.5);
                    r2.x -= (int)(direccion.x * overlap * 0.5);
                    r2.y -= (int)(direccion.y * overlap * 0.5);
                }
            }
        }

        // Pasto exterior — frena progresivamente sin detener
        if (carroPisaPasto(pistaCacheada, jugador.getX(), jugador.getY())) {
            int cx = jugador.x + 25;
            int cy = jugador.y + 15;
            double dx = cx - CENTRO_PISTA.x;
            double dy = cy - CENTRO_PISTA.y;
            double dist = Math.sqrt(dx * dx + dy * dy);
            // Mientras mas lejos del anillo, mas lento
            double penetracion = Math.abs(dist - 220) / 100.0;
            penetracion = Math.min(penetracion, 0.85);
            jugador.velocidadActual *= (1.0 - penetracion * 0.15);
            if (jugador.velocidadActual > 0 && jugador.velocidadActual < 0.3)
                jugador.velocidadActual = 0.3;
            if (jugador.velocidadActual < 0 && jugador.velocidadActual > -0.3)
                jugador.velocidadActual = -0.3;
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

    // Empuja el vehículo fuera del pasto usando Vector2D puro sin ajuste manual de ejes
    private void empujarAlAsfalto(Vehiculo v) {
        // Intentar empujar en 8 direcciones hasta salir del pasto
        int[][] direcciones = {
                {0, -1}, {0, 1}, {-1, 0}, {1, 0},
                {-1, -1}, {1, -1}, {-1, 1}, {1, 1}
        };

        for (int[] dir : direcciones) {
            int intentos = 0;
            int originalX = v.x;
            int originalY = v.y;

            while (carroPisaPasto(pistaCacheada, v.x, v.y) && intentos < 60) {
                v.x += dir[0] * 2;
                v.y += dir[1] * 2;
                intentos++;
            }

            if (!carroPisaPasto(pistaCacheada, v.x, v.y)) {
                return; // Salio del pasto exitosamente
            }

            // Esta direccion no funciono, volver a la posicion original
            v.x = originalX;
            v.y = originalY;
        }

        // Si ninguna direccion funciono, detener el carro
        if (v instanceof CarroJugador) {
            ((CarroJugador) v).velocidadActual = 0;
        } else if (v instanceof CarroRival) {
            ((CarroRival) v).velocidadActual = 0;
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

        // Dibujar cuenta regresiva
        if (!carreraIniciada) {
            // Fondo semitransparente central
            g.setColor(new Color(0, 0, 0, 180));
            g.fillRoundRect(280, 180, 240, 220, 25, 25);

            // Numero
            g.setColor(cuentaRegresiva == 3 ? Color.RED :
                    cuentaRegresiva == 2 ? Color.YELLOW : Color.GREEN);
            g.setFont(new Font("Arial", Font.BOLD, 130));
            int numX = cuentaRegresiva == 1 ? 355 : 330;
            g.drawString(String.valueOf(cuentaRegresiva), numX, 340);

            // Texto preparate
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 18));
            g.drawString("Preparate para correr!", 295, 375);
        }

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