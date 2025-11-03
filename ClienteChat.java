import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClienteChat {

    private Socket conexion;
    private DataInputStream entrada;
    private DataOutputStream salida;
    private Scanner lector;

    public static void main(String[] args) {
        ClienteChat app = new ClienteChat();
        app.iniciarSesion();
    }

    public void iniciarSesion() {
        lector = new Scanner(System.in);

        try {
            System.out.print("🟦 IP del servidor: ");
            String host = lector.nextLine();

            System.out.print("🟦 Puerto: ");
            int puerto = Integer.parseInt(lector.nextLine());

            conexion = new Socket(host, puerto);
            System.out.println("Conectado a " + host + ":" + puerto);

            entrada = new DataInputStream(conexion.getInputStream());
            salida = new DataOutputStream(conexion.getOutputStream());

            Thread receptor = new Thread(() -> {
                try {
                    while (true) {
                        String recibido = entrada.readUTF();
                        System.out.println("\n" + recibido);
                    }
                } catch (IOException e) {
                    System.out.println("Conexión terminada con el servidor.");
                }
            });
            receptor.start();

            while (true) {
                String linea = lector.nextLine();
                salida.writeUTF(linea);
            }

        } catch (IOException ex) {
            System.out.println("No fue posible establecer la conexión: " + ex.getMessage());
        }
    }
}
