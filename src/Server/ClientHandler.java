package Server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Objects;

public class ClientHandler implements Runnable {
    protected Socket clientSocket;
    protected final ObjectOutputStream writerObject;
    protected ObjectInputStream readerObject;
    protected final int ID;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        ID = clientSocket.getPort();
        try {
            writerObject = new ObjectOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean init() throws IOException, ClassNotFoundException {
        Server.sendObject(ID, "INIT", ID);
        String type = (String) readerObject.readObject();
        if (type.startsWith("VERSION")) {  // проверка соответствия версии
            String ver = (String) readerObject.readObject();
            if (Objects.equals(ver, Server.version)) {return true;}
            System.out.println("Версии клиента и сервера не сходятся (" + ver + " и " + Server.version + ")");
            Server.sendObject("Версии клиента и сервера не сходятся", "TEXT", ID);
        } else {
            System.out.println("Не верная инициализация от клиента, ожидалась его версия!");
            Server.sendObject("Не верная инициализация от клиента, ожидалась его версия!", "TEXT", ID);
        }
        return false;
    }

    @Override
    public void run() {
        try { // Прием сообщений от клиентов
            readerObject = new ObjectInputStream(clientSocket.getInputStream());
            if (!init()) {return;};

            while (true) {
                String type = (String) readerObject.readObject();
                if (type.startsWith("TEXT")) {
                    String message = (String) readerObject.readObject();
                    System.out.println("Сообщение от клиента: " + message);
                } else if (type.startsWith("SEND_OBJECTS")) {
                    int toClient = (int) readerObject.readObject();
                    Object object = readerObject.readObject();
                    Object time = readerObject.readObject();
                    Server.sendObject(object, time, "NEW_OBJECTS", toClient);
                    System.out.println("Объекты от клиента " + ID + " перенаправлены клиенту " + toClient);
                }
                else {readerObject.readObject();System.out.println("Не известный тип команды");}
            }
        } catch (SocketException e) {
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                // Client disconnected
                System.out.println("Client disconnected: " + clientSocket);
                Server.allClients.remove(this);
                Server.sendConnectedClientsList();

                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}