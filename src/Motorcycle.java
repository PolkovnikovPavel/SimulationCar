import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;

public class Motorcycle extends GameObject implements IBehaviour {
    public Motorcycle(int x, int y, int timeElapsed, int speed) {
        super(x, y, timeElapsed, speed);
        type = 1;
        setImg();
    }

    public void setImg() {
        try {
            File file = new File("images/bike1.png");
            img = ImageIO.read(file);
            isImg = true;

        } catch (java.io.IOException e) {isImg = false;}
    }
    @Override
    public void draw(Graphics2D graphic2d) {
        // Реализация метода draw для автомобиля
        if (isImg) {
            graphic2d.drawImage(img, x, y, null);
        } else {
            graphic2d.setColor(Color.yellow);
            graphic2d.fillRect(x, y, 30, 30);
        }
    }

    @Override
    public void move() {
        // Реализация метода move для автомобиля
        if (!is_start) {
            return;
        }
        x_f += speed;
        y_f += speed * 0.15;
        if (y_f > 800) {y_f = -100;}
        if (x_f > 1000) {x_f = -100;}

        x = (int) x_f;
        y = (int) y_f;
    }
}