package Chat;

import javax.swing.*;
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

    private PrintWriter out;
    private Socket clientSocket;

    public ChatServidorGUI() {
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

    public static void main(String[] args) {
        JFrame frame = new JFrame("Servidor de Chat");
        ChatServidorGUI servidorGUI = new ChatServidorGUI();
        frame.setContentPane(servidorGUI.main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(600, 400);
        frame.setVisible(true);
    }
}