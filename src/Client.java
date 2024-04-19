import javax.swing.table.TableRowSorter;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Client implements Runnable{
    public int ClientID;
    private static final String SERVER_IP = "127.0.0.1"; // IP-адрес сервера
    private static final int SERVER_PORT = 12333; // Порт сервера
    private ObjectOutputStream writerObject;
    private ObjectInputStream readerObject;
    final private Habitat habitat;
    public ArrayList<Integer> listUsers;

    public Client(Habitat habitat) {
        this.habitat = habitat;
        ClientID = -1;
    }

    public Object getDeepCopyObjects(Object original) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(original);

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        ArrayList<GameObject> newObj = null;
        try {
            newObj = (ArrayList<GameObject>) ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return newObj;
    }

    public void sendObjects(int id) throws IOException {
        writerObject.writeObject("SEND_OBJECTS");
        writerObject.writeObject(id);

        Object objCopy = getDeepCopyObjects(habitat.objects);

        writerObject.writeObject(objCopy);
        writerObject.writeObject(habitat.getTimeElapsed());
        writerObject.flush();
    }



    @Override
    public void run() {
        try {
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            System.out.println("Connected to server.");

            writerObject = new ObjectOutputStream(socket.getOutputStream());
            readerObject = new ObjectInputStream(socket.getInputStream());

            // Прием сообщений от сервера:
            while (true) {
                String type = (String) readerObject.readObject();
                if (type.startsWith("LIST_CLIENTS")) {
                    listUsers = (ArrayList<Integer>) readerObject.readObject();
                    System.out.println("Сообщение от сервера: " + listUsers);
                } else if (type.startsWith("INIT")) {
                    int num = (int) readerObject.readObject();
                    ClientID = num;
                    System.out.println("ID от сервера: " + ClientID);
                    Main.frame.setTitle("Автомобили и мотоциклы (" + ClientID + ")");

                    writerObject.writeObject("VERSION");
                    writerObject.writeObject(Main.version);
                    writerObject.flush();

                } else if (type.startsWith("NEW_OBJECTS")) {
                    synchronized (habitat.monitor) {
                        habitat.loadAllObjects(readerObject);
                    }
                    habitat.resume();
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
