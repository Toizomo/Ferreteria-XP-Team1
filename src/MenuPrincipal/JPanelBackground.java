package MenuPrincipal;

import javax.swing.*;
import java.awt.*;

public class JPanelBackground extends JPanel {
    private Image backgroundImage;

    public JPanelBackground() {
        // Cargar la imagen desde el paquete img
        backgroundImage = new ImageIcon(getClass().getResource("/img/ferre.jpg")).getImage();

        // Establecer layout si quieres agregar componentes encima del fondo
        setLayout(new BorderLayout());

        // Crear y agregar un panel para el título
        JPanel mainPanel = new JPanel();
        mainPanel.setOpaque(false); // Hacerlo transparente para ver la imagen de fondo
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("FERETERIA", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(Box.createVerticalStrut(20)); // Espaciador arriba
        mainPanel.add(titleLabel);

        add(mainPanel, BorderLayout.NORTH);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Dibujar la imagen de fondo
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
