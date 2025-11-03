import java.io.*;
import java.net.*;
import java.util.*;

public class ServidorChat {

    private static final Map<String, DataOutputStream> conexiones = Collections.synchronizedMap(new HashMap<>());

    public static void main(String[] args) {
        final int PUERTO = 8080;
        try (ServerSocket servidor = new ServerSocket(PUERTO)) {
            System.out.println("Chat activo en el puerto " + PUERTO);

            while (true) {
                Socket cliente = servidor.accept();
                System.out.println("Nuevo cliente desde: " + cliente.getInetAddress());
                Thread hilo = new Thread(new ManejadorCliente(cliente));
                hilo.start();
            }

        } catch (IOException e) {
            System.out.println("Falla en el servidor: " + e.getMessage());
        }
    }

    static class ManejadorCliente implements Runnable {
        private Socket socket;
        private String nombre;
        private DataInputStream entrada;
        private DataOutputStream salida;

        public ManejadorCliente(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                entrada = new DataInputStream(socket.getInputStream());
                salida = new DataOutputStream(socket.getOutputStream());

                iniciarConexion();

                while (true) {
                    String texto = entrada.readUTF();

                    if (texto.startsWith("/name ")) {
                        actualizarNombre(texto.substring(6));
                    } else if (texto.startsWith("/msg ")) {
                        enviarPrivado(texto);
                    } else if (texto.startsWith("/global ")) {
                        transmitirGlobal(texto.substring(8));
                    } else if (texto.equals("/clientes")) {
                        listarUsuarios();
                    } else {
                        salida.writeUTF("Comando inválido. Usa /name, /msg, /global o /clientes.");
                    }
                }

            } catch (IOException e) {
                if (nombre != null) {
                    System.out.println("Desconectado: " + nombre);
                    conexiones.remove(nombre);
                    try {
                        transmitirGlobal(nombre + " abandonó el chat.");
                    } catch (IOException ignored) {}
                }
            }
        }

        // iniciarConexion()
        private void iniciarConexion() throws IOException {
            salida.writeUTF("Bienvenido al servidor de chat. Ingresa tu nombre:");
            nombre = entrada.readUTF();

            synchronized (conexiones) {
                while (conexiones.containsKey(nombre)) {
                    salida.writeUTF("Ese nombre está ocupado, elige otro:");
                    nombre = entrada.readUTF();
                }
                conexiones.put(nombre, salida);
            }

            salida.writeUTF("Te has conectado como: " + nombre);
            transmitirGlobal(nombre + " ingresó al chat.");
        }

        // actualizarNombre()
        private void actualizarNombre(String nuevo) throws IOException {
            synchronized (conexiones) {
                if (conexiones.containsKey(nuevo)) {
                    salida.writeUTF("El nombre '" + nuevo + "' ya existe.");
                } else {
                    conexiones.remove(nombre);
                    conexiones.put(nuevo, salida);
                    transmitirGlobal(nombre + " ahora es conocido como " + nuevo);
                    nombre = nuevo;
                }
            }
        }

        // enviarPrivado()
        private void enviarPrivado(String comando) throws IOException {
            String[] partes = comando.split(" ", 3);
            if (partes.length < 3) {
                salida.writeUTF("Formato: /msg <usuario> <mensaje>");
                return;
            }

            String receptor = partes[1];
            String texto = partes[2];
            DataOutputStream destino = conexiones.get(receptor);

            if (destino != null) {
                destino.writeUTF("[Privado de " + nombre + "]: " + texto);
                salida.writeUTF("(Privado a " + receptor + "): " + texto);
            } else {
                salida.writeUTF("No se encontró al usuario '" + receptor + "'.");
            }
        }

        // transmitirGlobal()
        private void transmitirGlobal(String mensaje) throws IOException {
            synchronized (conexiones) {
                for (Map.Entry<String, DataOutputStream> entry : conexiones.entrySet()) {
                    if (!entry.getKey().equals(nombre)) {
                        entry.getValue().writeUTF("(Global) [" + nombre + "]: " + mensaje);
                    }
                }
            }
            System.out.println("(Global) " + nombre + ": " + mensaje);
        }

        // listarUsuarios()
        private void listarUsuarios() throws IOException {
            synchronized (conexiones) {
                StringBuilder sb = new StringBuilder("Usuarios en línea (" + conexiones.size() + "):\n");
                for (String usuario : conexiones.keySet()) {
                    sb.append(" * ").append(usuario).append("\n");
                }
                salida.writeUTF(sb.toString());
            }
        }
    }
}
