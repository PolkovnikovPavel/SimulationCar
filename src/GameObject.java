import java.awt.*;
import java.io.Serializable;
import java.util.Comparator;

public abstract class GameObject implements IBehaviour, Serializable {
    protected final int ID;
    protected final int birthday;
    protected int type;
    protected int x;
    protected int y;
    protected double x_f;
    protected double y_f;
    protected double speed;
    transient protected Image img;
    protected boolean isImg;
    protected boolean is_start = true;

    public GameObject(int x, int y, int timeElapsed, int speed) {
        ID = ((int) (Math.random() * 1000)) + (timeElapsed * 1000);
        birthday = timeElapsed;   // Время рождения
        this.x = x;
        this.y = y;
        x_f = x;
        y_f = y;
        this.speed = (double) speed / 10;
        type = -1;

    }

    public void setImg() {}

    public abstract void draw(Graphics2D graphic2d);

    public void chengSpeed(int newSpeed) {
        speed = (double) newSpeed / 10;
    }
}
