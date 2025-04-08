package Img;

import javax.swing.*;
import java.awt.*;

public class img extends JPanel {
    private Image backgroundImage;

    public img() {
        // Carga la imagen desde /img/ferre.jpg (en src)
        backgroundImage = new ImageIcon(getClass().getResource("/img/ostia.png")).getImage();
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // Importante: conservar layout
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Dibuja la imagen escalada al tamaño del panel
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
}
