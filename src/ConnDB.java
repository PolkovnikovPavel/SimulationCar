import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


public class ConnDB {
    public static Connection conn;
    public static Statement statmt;
    public static ResultSet resSet;
    public static String nameDB = "test.db";

    // --------ПОДКЛЮЧЕНИЕ К БАЗЕ ДАННЫХ--------
    public static void Conn() throws ClassNotFoundException, SQLException
    {
        conn = null;
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:" + nameDB);

        System.out.println("База Подключена!");
    }

    // --------Создание таблицы--------
    public static void CreateDB() throws SQLException
    {
        statmt = conn.createStatement();
        statmt.execute("CREATE TABLE if not exists 'objects' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'type' INT, 'lifeTime' INT, 'x' INT, 'y' INT);");

        System.out.println("Таблица создана или уже существует.");
    }

    public static void writeObject(GameObject object, Habitat habitat) throws SQLException {
        int lifeTime = habitat.getTimeElapsed() - object.birthday;
        if (object instanceof Car) {
            statmt.execute("INSERT INTO 'objects' ('type', 'lifeTime', 'x', 'y') VALUES (0, " + lifeTime + ", " + object.x + ", " + object.y + "); ");
        } else if (object instanceof Motorcycle) {
            statmt.execute("INSERT INTO 'objects' ('type', 'lifeTime', 'x', 'y') VALUES (1, " + lifeTime + ", " + object.x + ", " + object.y + "); ");

        }
    }

    public static ArrayList<GameObject> ReadObjects(Habitat habitat) throws SQLException {
        ArrayList<GameObject> objects = new ArrayList<>();
        resSet = statmt.executeQuery("SELECT * FROM objects");
        // Получаем все записи из базы данных об объектах
        while(resSet.next())
        {
            int id = resSet.getInt("id");
            int type = resSet.getInt("type");
            int birthday = habitat.getTimeElapsed() - resSet.getInt("lifeTime");
            int x = resSet.getInt("x");
            int y = resSet.getInt("y");

            GameObject obj;
            if (type == 0) {
                obj = new Car(x, y, 0, habitat.getSpeed());
            } else if (type == 1) {
                obj = new Motorcycle(x, y, 0, habitat.getSpeed());
            } else {continue;}
            obj.loadObject(birthday);
            objects.add(obj);
        }

        return objects;
    }

    public static void clearDB()  throws SQLException {
        statmt.execute("DELETE FROM objects;");
    }


    // --------Закрытие--------
    public static void CloseDB() throws SQLException
    {
        conn.close();
        if (statmt != null) {
            statmt.close();
        }
        if (resSet != null) {
            resSet.close();
        }

        System.out.println("Соединения закрыты");
    }

}