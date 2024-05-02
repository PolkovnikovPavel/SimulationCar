import java.sql.SQLException;
import java.util.ArrayList;

public class DB {
    private final Habitat habitat;
    public DB(Habitat habitat) throws ClassNotFoundException, SQLException {
        this.habitat = habitat;
        ConnDB.Conn();
        ConnDB.CreateDB();
    }

    public void closeDB() throws SQLException {ConnDB.CloseDB();}
    public void clearDB() throws SQLException {ConnDB.clearDB();}

    public void writeObject(GameObject object) throws SQLException {
        ConnDB.writeObject(object, habitat);
    }
    public ArrayList<GameObject> ReadObjects() throws SQLException {
        return ConnDB.ReadObjects(habitat);
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        ConnDB.Conn();
        ConnDB.CreateDB();
        ConnDB.CloseDB();
    }

}