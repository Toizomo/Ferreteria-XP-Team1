package Chat;

import MenuPrincipal.MenuPrincipal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServidorGUI {
    private JTextArea textArea1;
    private JTextField textField1;
    private JButton iniciarButton;
    private JButton enviarButton;
    private JPanel main;
    private JButton chatCLienteButton;
    private JButton volverButton;

    private PrintWriter out;
    private Socket clientSocket;

    public ChatServidorGUI() {
        // Aplicar estilos
        aplicarEstilos();

        textArea1.setEditable(false);

        iniciarButton.addActionListener(e -> new Thread(this::iniciar).start());
        enviarButton.addActionListener(e -> enviarMensaje());

        textField1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    enviarMensaje();
                }
            }
        });

        chatCLienteButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                ChatClienteGUI clienteGUI = new ChatClienteGUI();
                JFrame frame = new JFrame("Cliente de Chat");
                frame.setContentPane(clienteGUI.getMainPanel());
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.pack();
                frame.setSize(600, 400);
                frame.setVisible(true);
            });
        });

        volverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame jFrame = (JFrame) SwingUtilities.getWindowAncestor(volverButton);
                jFrame.dispose();
                MenuPrincipal.main(null);
            }
        });

    }

    public void iniciar() {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            actualizarTextArea("Servidor iniciado. Esperando conexión...\n");

            while (true) {
                clientSocket = serverSocket.accept();
                actualizarTextArea("Cliente conectado\n");

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                new Thread(() -> recibirMensajes(in)).start();
            }
        } catch (IOException e) {
            actualizarTextArea("Error en el servidor: " + e.getMessage() + "\n");
        }
    }

    public void enviarMensaje() {
        String mensaje = textField1.getText().trim();

        if (!mensaje.isEmpty()) {
            if (out != null) {
                out.println(mensaje);
                actualizarTextArea("S. " + mensaje + "\n");
                textField1.setText("");
            } else {
                actualizarTextArea("Error: No hay cliente conectado.\n");
            }
        }
    }

    public void recibirMensajes(BufferedReader in) {
        try {
            String receivedMessage;
            while ((receivedMessage = in.readLine()) != null) {
                if (receivedMessage.equalsIgnoreCase("salir")) {
                    actualizarTextArea("Cliente ha abandonado el chat\n");
                    break;
                }
                actualizarTextArea("C. " + receivedMessage + "\n");
            }
        } catch (IOException e) {
            actualizarTextArea("Error al recibir mensajes: " + e.getMessage() + "\n");
        } finally {
            cerrarRecursos();
        }
    }

    private void cerrarRecursos() {
        try {
            if (out != null) out.close();
            if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
            actualizarTextArea("Cliente desconectado. Esperando nueva conexión...\n");
        } catch (IOException e) {
            actualizarTextArea("Error al cerrar recursos: " + e.getMessage() + "\n");
        }
    }

    private void actualizarTextArea(String mensaje) {
        SwingUtilities.invokeLater(() -> textArea1.append(mensaje));
    }

    public JPanel getMainPanel() {
        return main;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Servidor de Chat");
        ChatServidorGUI servidorGUI = new ChatServidorGUI();
        frame.setContentPane(servidorGUI.getMainPanel());
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
        iniciarButton.setFont(fuenteBotones);
        iniciarButton.setBackground(colorBotonFondo);
        iniciarButton.setForeground(colorBotonTexto);
        iniciarButton.setBorder(BorderFactory.createLineBorder(colorBordeBoton));
        iniciarButton.setFocusPainted(false);


        enviarButton.setFont(fuenteBotones);
        enviarButton.setBackground(colorBotonFondo);
        enviarButton.setForeground(colorBotonTexto);
        enviarButton.setBorder(BorderFactory.createLineBorder(colorBordeBoton));
        enviarButton.setFocusPainted(false);

        chatCLienteButton.setFont(fuenteBotones);
        chatCLienteButton.setBackground(colorBotonFondo);
        chatCLienteButton.setForeground(colorBotonTexto);
        chatCLienteButton.setBorder(BorderFactory.createLineBorder(colorBordeBoton));
        chatCLienteButton.setFocusPainted(false);

        volverButton.setFont(fuenteBotones);
        volverButton.setBackground(colorBotonFondo);
        volverButton.setForeground(colorBotonTexto);
        volverButton.setBorder(BorderFactory.createLineBorder(colorBordeBoton));
        volverButton.setFocusPainted(false);
    }
}
