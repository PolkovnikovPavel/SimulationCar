import javax.swing.*;

// Базовый абстрактный класс для интеллектуального поведения объектов
public abstract class BaseAi implements Runnable {

    protected Thread thread;

    public static int frequency = 100;
    protected Habitat habitat;
    public String priority = "Normal";

    public BaseAi() {}

    public void setThreadPriority(String priority) {
        this.priority = priority;
        switch(priority) {
            case("Min"):
                thread.setPriority(Thread.MIN_PRIORITY);
            case("Normal"):
                thread.setPriority(Thread.NORM_PRIORITY);
            case("Max"):
                thread.setPriority(Thread.MAX_PRIORITY);
        }
    }

}
