package Server;

import javax.swing.table.TableRowSorter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class TCPClient {
    static String version = "0.6.0";
    private static int ClientID;
    private static final String SERVER_IP = "127.0.0.1"; // IP-адрес сервера
    private static final int SERVER_PORT = 12333; // Порт сервера

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            System.out.println("Connected to server.");

            ObjectOutputStream writerObject = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream readerObject = new ObjectInputStream(socket.getInputStream());
            Scanner userInput = new Scanner(System.in);


            // Ввод сообщений с консоли и отправка на сервер
            new Thread(() -> {
                while (true) {
                    String message = userInput.nextLine();
                    int id = userInput.nextInt();
                    try {
                        writerObject.writeObject("SEND_OBJECTS");
                        writerObject.writeObject(id);
                        writerObject.writeObject(message);
                        writerObject.flush();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();

            // Прием сообщений от сервера и вывод на консоль
            while (true) {
                String type = (String) readerObject.readObject();
                if (type.startsWith("LIST_CLIENTS")) {
                    // Десериализация объекта
                    ArrayList<Integer> message = (ArrayList<Integer>) readerObject.readObject();
                    System.out.println("Сообщение от сервера: " + message);
                } else if (type.startsWith("INIT")) {
                    int num = (int) readerObject.readObject();
                    ClientID = num;
                    System.out.println("ID от сервера: " + ClientID);

                    writerObject.writeObject("VERSION");
                    writerObject.writeObject(version);
                    writerObject.flush();

                } else if (type.startsWith("NEW_OBJECTS")) {
                    readerObject.readObject();
                    // TODO десереализация по анологии из файла, но пепред этим спросить пользователя
                    System.out.println("Отправленные объекты успешно загружены");
                } else if (type.startsWith("TEXT")) {
                    String message = (String) readerObject.readObject();
                    System.out.println("Сообщение от сервера: " + message);
                }
                else {readerObject.readObject();}

            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
