import javax.imageio.ImageIO;
import java.io.File;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Car extends GameObject implements IBehaviour {
    public Car(int x, int y, int timeElapsed, int speed) {
        super(x, y, timeElapsed, speed);
        type = 0;
        setImg();
    }

    public void setImg() {
        try {
            File file = new File("images/car1.png");
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
            graphic2d.setColor(Color.blue);
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
        if (x_f > 1000) {x_f = -150;}

        x = (int) x_f;
    }
}