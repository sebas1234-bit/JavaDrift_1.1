package com.javadrift;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
    public boolean arribaPresionada, abajoPresionada;
    public boolean izquierdaPresionada, derechaPresionada;
    public boolean rPresionada;

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int codigo = e.getKeyCode();

        if (codigo == KeyEvent.VK_UP) arribaPresionada = true;
        if (codigo == KeyEvent.VK_DOWN) abajoPresionada = true;
        if (codigo == KeyEvent.VK_LEFT) izquierdaPresionada = true;
        if (codigo == KeyEvent.VK_RIGHT) derechaPresionada = true;
        if (codigo == KeyEvent.VK_R) rPresionada = true;
    }
    @Override
    public void keyReleased(KeyEvent e) {
        int codigo = e.getKeyCode();

        if (codigo == KeyEvent.VK_UP)    arribaPresionada = false;
        if (codigo == KeyEvent.VK_DOWN)  abajoPresionada = false;
        if (codigo == KeyEvent.VK_LEFT)  izquierdaPresionada = false;
        if (codigo == KeyEvent.VK_RIGHT) derechaPresionada = false;
        if (codigo == KeyEvent.VK_R) rPresionada = false;
    }

}
