package Chat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class ChatClienteGUI {
    private JPanel panel;
    private JTextArea textArea1;
    private JTextField textField1;
    private JButton conectarButton;
    private JButton enviarButton;
    private JPanel main;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public ChatClienteGUI() {
        // Aplicar estilos
        aplicarEstilos();

        textArea1.setEditable(false);

        conectarButton.addActionListener(e -> {
            String serverAddress = JOptionPane.showInputDialog("Ingrese la IP del servidor (localhost si es local):");
            if (serverAddress == null || serverAddress.isEmpty()) {
                serverAddress = "localhost";
            }
            String finalServerAddress = serverAddress;
            new Thread(() -> conectarAlServidor(finalServerAddress)).start();
        });

        enviarButton.addActionListener(e -> enviarMensaje());

        textField1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    enviarMensaje();
                }
            }
        });
    }

    private void enviarMensaje() {
        String mensaje = textField1.getText().trim();

        if (!mensaje.isEmpty()) {
            if (out != null) {
                out.println(mensaje);
                actualizarTextArea("C. " + mensaje + "\n");
                textField1.setText("");
            } else {
                actualizarTextArea("Error: No estás conectado al servidor.\n");
            }
        }
    }

    private void conectarAlServidor(String serverAddress) {
        try {
            socket = new Socket(serverAddress, 12345);
            out = new PrintWriter(socket.getOutputStream(), true); // Auto-flush activado
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            actualizarTextArea("Conectado al servidor.\n");

            new Thread(this::recibirMensajes).start();

        } catch (IOException e) {
            actualizarTextArea("Error al conectar al servidor: " + e.getMessage() + "\n");
        }
    }

    private void recibirMensajes() {
        try {
            String mensaje;
            while ((mensaje = in.readLine()) != null) {
                if (mensaje.equalsIgnoreCase("salir")) {
                    actualizarTextArea("Servidor ha cerrado la conexión.\n");
                    break;
                }
                actualizarTextArea("S. " + mensaje + "\n");
            }
        } catch (IOException e) {
            actualizarTextArea("Error al recibir mensajes: " + e.getMessage() + "\n");
        } finally {
            try {
                if (socket != null) socket.close();
            } catch (IOException ex) {
                actualizarTextArea("Error al cerrar la conexión: " + ex.getMessage() + "\n");
            }
        }
    }

    private void actualizarTextArea(String mensaje) {
        SwingUtilities.invokeLater(() -> textArea1.append(mensaje));
    }

    public JPanel getMainPanel() {
        return main;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Cliente de Chat");
        ChatClienteGUI chatClienteGUI = new ChatClienteGUI();
        frame.setContentPane(chatClienteGUI.getMainPanel());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(600, 400);
        frame.setVisible(true);
    }

    private void aplicarEstilos() {
        Font fuenteCampos = new Font("Serif", Font.PLAIN, 15);
        Font fuenteBotones = new Font("Serif", Font.BOLD, 15);
        Color colorFondo = new Color(216, 196, 164); // beige claro
        Color colorTexto = new Color(59, 42, 27);    // marrón oscuro
        Color colorBotonFondo = colorFondo;
        Color colorBotonTexto = Color.WHITE;
        Color colorBordeBoton = Color.WHITE;

        main.setBackground(colorFondo);

        // Estilos para el área de texto (JTextArea)
        textArea1.setFont(fuenteCampos);
        textArea1.setForeground(colorTexto);
        textArea1.setBackground(Color.WHITE);
        textArea1.setBorder(BorderFactory.createLineBorder(colorTexto));

        // Estilos para el campo de texto de entrada
        textField1.setFont(fuenteCampos);
        textField1.setBackground(Color.WHITE);
        textField1.setForeground(colorTexto);
        textField1.setBorder(BorderFactory.createLineBorder(colorTexto));

        // Estilos para los botones
        conectarButton.setFont(fuenteBotones);
        conectarButton.setBackground(colorBotonFondo);
        conectarButton.setForeground(colorBotonTexto);
        conectarButton.setBorder(BorderFactory.createLineBorder(colorBordeBoton));
        conectarButton.setFocusPainted(false);

        enviarButton.setFont(fuenteBotones);
        enviarButton.setBackground(colorBotonFondo);
        enviarButton.setForeground(colorBotonTexto);
        enviarButton.setBorder(BorderFactory.createLineBorder(colorBordeBoton));
        enviarButton.setFocusPainted(false);
    }
}
