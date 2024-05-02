package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Server {
    static String version = "0.7.0";
    private static final int PORT = 12333;
    protected static final ArrayList<ClientHandler> allClients = new ArrayList<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("TCP Server started. On port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                ClientHandler client = new ClientHandler(clientSocket);
                allClients.add(client);

                Thread clientHandler = new Thread(client);
                clientHandler.start();

                sendConnectedClientsList();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void sendObject(Object object, String type) {
        try {
            for (ClientHandler client : allClients) {
                client.writerObject.writeObject(type);
                client.writerObject.writeObject(object);
                client.writerObject.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void sendObject(Object object, String type, int who) {
        try {
            for (ClientHandler client : allClients) {
                if (client.ID == who) {
                    client.writerObject.writeObject(type);
                    client.writerObject.writeObject(object);
                    client.writerObject.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendObject(Object object1, Object object2, String type, int who) {
        try {
            for (ClientHandler client : allClients) {
                if (client.ID == who) {
                    client.writerObject.writeObject(type);
                    client.writerObject.writeObject(object1);
                    client.writerObject.writeObject(object2);
                    client.writerObject.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendConnectedClientsList() {
        ArrayList<Integer> ob = new ArrayList<>();
        for (ClientHandler client : allClients) {
            ob.add(client.ID);
        }

        sendObject(ob, "LIST_CLIENTS");
    }
}
