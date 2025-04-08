package Chat;

import javax.swing.*;
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

    public static void main(String[] args) {
        JFrame frame = new JFrame("Cliente de Chat");
        ChatClienteGUI chatClienteGUI = new ChatClienteGUI();
        frame.setContentPane(chatClienteGUI.main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(600, 400);
        frame.setVisible(true);
    }
}