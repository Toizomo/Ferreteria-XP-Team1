package MenuPrincipal;

import javax.swing.*;
import java.awt.*;

public class JPanelBackground extends JPanel {
    private Image imagenFondo;

    public JPanelBackground() {
        // Cambia la ruta a la imagen según donde la tengas guardada
        ImageIcon icon = new ImageIcon("src/MenuPrincipal/fondo_menu.png");
        imagenFondo = icon.getImage();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imagenFondo != null) {
            g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
