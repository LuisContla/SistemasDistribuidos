/*
 * Proyecto 2. Elaborado por Luis Andrés Contla Mota. Sistemas Distribuidos 7CV3
 *
 * Clase principal (JFrame):
 * - Inicializa la ventana y agrega el panel de juego.
 * - Permite pasar por CLI el número de asteroides (por defecto en Config).
 */
import javax.swing.*;

public class SimpleGui2 extends JFrame {

    public static void main(String[] args) {
        int n = Config.DEFAULT_ASTEROIDS; // número de asteroides (CLI opcional)
        if (args != null && args.length > 0) {
            try { n = Integer.parseInt(args[0]); } catch (Exception ignored) {}
        }
        SimpleGui2 gui = new SimpleGui2(n);
        gui.setVisible(true);
    }

    public SimpleGui2(int nAsteroids) {
        setTitle("Proyecto 2 – Asteroides (Swing/AWT)");
        setSize(Config.SCREEN_W, Config.SCREEN_H);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        add(new GamePanel(nAsteroids));
    }
}
