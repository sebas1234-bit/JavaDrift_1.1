package com.javadrift;
import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        JFrame ventana = new JFrame("JavaDrift");
        GamePanel gamePanel = new GamePanel();

        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setResizable(false);
        ventana.add(gamePanel);
        ventana.pack();
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);

        gamePanel.iniciarJuego();
    }
}
