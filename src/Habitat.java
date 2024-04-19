import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.*;
import java.io.*;


class Habitat extends JPanel {
    public final static boolean[] objectsRunning = {true, true};
    public final Object monitor;
    private final Timer timer;
    private final int width;
    private final int height;
    protected ArrayList<GameObject> objects;
    private final HashSet<Integer> allObjecksID;
    private final TreeMap<Integer, Integer> objectBirthdayByID;
    private int speed = 20;
    private int count_car = 0;
    private int count_motorcycle = 0;
    private int timeElapsed;
    private int lastTimeElapsed;
    private boolean is_start;
    private boolean is_lastStart = false;
    private boolean is_text;
    private boolean is_dialog_check;
    private int N_car = 2;
    private int N_motorcycle = 3;
    private final int[] liveTime;
    public CarAi carsAi;
    public MotorcycleAi motorcyclesAi;
    public final ConfigManager configManager;

    public Habitat(int width, int height, Object monitor_) {
        this.width = width;
        this.height = height;
        this.monitor = monitor_;
        this.objects = new ArrayList<>();
        this.allObjecksID = new HashSet<>();
        this.objectBirthdayByID = new TreeMap<>();
        this.configManager = new ConfigManager(this);


        this.is_start = false;
        this.is_text = true;
        this.is_dialog_check = true;
        this.timeElapsed = 0;

        liveTime = new int[]{50, 50};

        carsAi = new CarAi(this);
        motorcyclesAi = new MotorcycleAi(this);


        timer = new Timer(200, e -> {
            synchronized (monitor) {
                String selectedCarProbabilityString = (String) Main.carProbabilityComboBox.getSelectedItem();
                double carProbability = Double.parseDouble(selectedCarProbabilityString.replace("%", "")) / 100.0;

                String selectedMotorcycleProbabilityString = (String) Main.motorcycleProbabilityComboBox.getSelectedItem();
                double motorcycleProbability = Double.parseDouble(selectedMotorcycleProbabilityString.replace("%", "")) / 100.0;

                update(carProbability, motorcycleProbability);
            }
        });
    }

    private void calculateStatistics() {
        count_car = 0;
        count_motorcycle = 0;
        for (GameObject object : objects) {
            if (object instanceof Car) {count_car++;}
            if (object instanceof Motorcycle) {count_motorcycle++;}
        }
    }

    public void pause() {
        timer.stop();
        lastTimeElapsed = timeElapsed;
        is_lastStart = is_start;
        this.is_start = false;
        for (GameObject object : objects) {
            object.is_start = false;
        }
    }
    public void resume() {
        if (!is_lastStart) {return;}
        this.is_start = true;
        for (GameObject object : objects) {
            object.is_start = true;
        }
        timer.start();
    }

    public void startRunning() {
        objectsRunning[0] = true; objectsRunning[1] = true;
    }
    public void stopRunning() {
        objectsRunning[0] = false; objectsRunning[1] = false;
    }

    public void setRunning(int i, boolean state) {
        if (i < 2) {
            objectsRunning[i] = state;
        }
    }

    public boolean stop() {
        calculateStatistics();

        String message = "Количество машин: " + count_car + "\n"
                + "Количество мотоциклов: " + count_motorcycle + "\n"
                + "Время симуляции: " + timeElapsed + " сек.";

        JTextArea textArea = new JTextArea(message);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        Object[] options = {"ОК", "Отмена"};
        int choice;
        pause();
        if (this.is_dialog_check) {
            choice = JOptionPane.showOptionDialog(this,
                    scrollPane,
                    "Информация о симуляции",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]);
        } else {
            choice = JOptionPane.OK_OPTION;
        }


        if (choice == JOptionPane.OK_OPTION) {
            timer.stop();
//            stopRunning(); // не нужна т.к теперь есть конфигурация
            lastTimeElapsed = timeElapsed;
            timeElapsed = 0;
            objects.clear();
            repaint();
            Main.stopButton.setEnabled(false);
            Main.startButton.setEnabled(true);
            return false;
        } else {
            resume();
            return true;
        }
    }

    public void start() {
        objects.clear();
        timeElapsed = 0;
        lastTimeElapsed = 0;
        is_start = true;
        count_car = 0;
        count_motorcycle = 0;
        System.out.println("START!...");
        timer.start();
//        startRunning(); // не нужна т.к теперь есть конфигурация
        Main.startButton.setEnabled(false);
        Main.stopButton.setEnabled(true);
    }

    public void show_hide_text() {
        this.is_text = !this.is_text;
    }
    public void show_hide_dialog() {
        this.is_dialog_check = !this.is_dialog_check;
    }

    public int cheng_N_car(int new_N) {
        if (new_N > 0) {
            this.N_car = new_N;
            return new_N;
        }
        this.N_car = 2;
        this.is_start = false;
        JOptionPane.showMessageDialog(this,
                new String[] {"Нельзя использовать значения меньше 1",
                        "Период появления автомобилей по умолчанию = " + this.N_car},
                "Ошибка",
                JOptionPane.ERROR_MESSAGE);
        this.is_start = true;
        return this.N_car;
    }

    public int cheng_N_motorcycle(int new_N) {
        if (new_N > 0) {
            this.N_motorcycle = new_N;
            return new_N;
        }
        this.N_motorcycle = 3;
        this.is_start = false;
        JOptionPane.showMessageDialog(this,
                new String[] {"Нельзя использовать значения меньше 1",
                        "Период появления мотоциклов по умолчанию = " + this.N_motorcycle},
                "Ошибка",
                JOptionPane.ERROR_MESSAGE);
        this.is_start = true;
        return this.N_motorcycle;
    }

    public void cheng_live_time(int new_time, int index) {
        liveTime[index] = new_time;
    }

    public void setLiveTime(int new_time, int index) {
        liveTime[index] = new_time;
        Component[] components = Main.liveTimeGrid.getComponents();
        ((JSpinner) components[index * 2 + 1]).setValue(new_time);
    }

    public void chengGameObjectSpeed(int newSpeed) {
        speed = newSpeed;
        for (GameObject object : objects) {object.chengSpeed(newSpeed);}
    }

    public int getTimeElapsed() {return timeElapsed;}
    public int getN_car() {return N_car;}
    public int getN_motorcycle() {return N_motorcycle;}
    public int getLiveTime(int i) {return liveTime[i];}
    public int getSpeed() {return speed;}
    public boolean getIs_text() {return is_text;}
    public boolean getIs_dialog_check() {return is_dialog_check;}
    public void toggleInformationDisplay(boolean showInfo) {
        is_text = showInfo;
        repaint();
    }

    public void saveAllObjects(String fileName) {
        try {
            FileOutputStream fileOut = new FileOutputStream(fileName); // создаёт новый файловый поток
            ObjectOutputStream out = new ObjectOutputStream(fileOut); // создаёт по файловому потоку поток объектов
            out.writeObject(objects);
            out.writeObject(timeElapsed);
            out.close();
            fileOut.close();
            System.out.println("Список объектов сохранен в файле gameObjects.ser");
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void loadAllObjects(String fileName) {
        // Десериализация списка из файла
        try {
            FileInputStream fileIn = new FileInputStream(fileName);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            loadAllObjects(in);
            in.close();
            fileIn.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void loadAllObjects(ObjectInputStream in) {
        // Десериализация списка из потока объектов и перезагрузка
        objects.clear();
        start();
        is_lastStart = true;
        try {
            objects = (ArrayList<GameObject>) in.readObject();
            timeElapsed = (int) in.readObject();

            // Востановление полей img у загруженных объектов
            for (GameObject object : objects) {
                object.setImg();
                object.chengSpeed(speed);
            }
        } catch(IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public void addObject(GameObject object) {
        objects.add(object);
        allObjecksID.add(object.ID);
        objectBirthdayByID.put(object.ID, object.birthday);
    }

    public void viewObjects(JFrame frame) {
        ViewObjectsDialog dialog = new ViewObjectsDialog(frame, objectBirthdayByID);
        dialog.setVisible(true);
    }

    private void generateObject(double carProbability, double motorcycleProbability) {
        if (timeElapsed % N_car == 0 && Math.random() < carProbability) {
            int x = (int) (Math.random() * width);
            int y = (int) (Math.random() * height);
            Car obj = new Car(x, y, timeElapsed, speed);
            addObject(obj);
        }

        if (timeElapsed % N_motorcycle == 0 && Math.random() < motorcycleProbability) {
            int x = (int) (Math.random() * width);
            int y = (int) (Math.random() * height);
            Motorcycle obj = new Motorcycle(x, y, timeElapsed, speed);
            addObject(obj);
        }
    }

    public void removeObject(GameObject object) {
        objects.remove(object);
        allObjecksID.remove(object.ID);
        objectBirthdayByID.remove(object.ID);
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D graphic2d = (Graphics2D) g;
        graphic2d.setColor(Color.WHITE);
        graphic2d.fillRect(0, 0, width, height);

        graphic2d.setColor(Color.red);
        graphic2d.fillRect(0, 0, width, 2);
        graphic2d.fillRect(width - 2, 0, 2, height);
        graphic2d.fillRect(0, height - 2, width, 2);
        graphic2d.fillRect(0, 0, 2, height);

        for (GameObject object : objects) {
            object.draw(graphic2d);
        }

        graphic2d.setColor(Color.BLACK);
        Font font = new Font("Arial", Font.PLAIN, 20);
        graphic2d.setFont(font);
        String str;
        if (this.is_text) {

            if (!is_start) {
                graphic2d.setColor(Color.RED);
                str = "Количество машин: " + count_car;
                graphic2d.drawString(str, 20, 80);

                font = new Font("Serif", Font.BOLD, 25);
                graphic2d.setFont(font);
                graphic2d.setColor(Color.CYAN);
                str = "Количество мотоциклов: " + count_motorcycle;
                graphic2d.drawString(str, 20, 130);
                font = new Font("Arial", Font.PLAIN, 20);
                str = "Time: " + this.lastTimeElapsed;
            } else {
                font = new Font("Arial", Font.PLAIN, 20);
                str = "Time: " + this.timeElapsed;
            }
            graphic2d.setFont(font);
            graphic2d.setColor(Color.BLACK);
            graphic2d.drawString(str, 20, 30);
        }
    }

    public void update(double carProbability, double motorcycleProbability) {
        if (!is_start) {
            return;
        }
        timeElapsed += 1;

        if (Habitat.objectsRunning[0] || Habitat.objectsRunning[1]) {monitor.notify();}

        generateObject(carProbability, motorcycleProbability);

        GameObject[] toRemoveList = new GameObject[objects.size()];
        int i = 0;

        for (GameObject object : objects) {
            if (timeElapsed - object.birthday >= liveTime[object.type]) {
                toRemoveList[i++] = object;
            }
        }

        for (int j = 0; j < i; j++) {
            removeObject(toRemoveList[j]);
        }
    }

    public void keyTyped(KeyEvent e) {
        char k = e.getKeyChar();
        if (k == 'e' || k == 'E' || k == 'у' || k == 'У') this.stop();
        if (k == 'b' || k == 'B' || k == 'и' || k == 'И') this.start();
        if (k == 't' || k == 'T' || k == 'е' || k == 'Е') this.show_hide_text();
    }

    public boolean reduceMotorcyclesByPercent(int percent) {
        if (percent < 0 || percent > 100) {
            System.out.println("Некорректное значение процента.");
            return false;
        }

        int numToRemove = (int) (objects.stream()
                .filter(object -> object instanceof Motorcycle)
                .count() * percent / 100);

        int removedCount = 0;
        for (int i = objects.size() - 1; i >= 0 && removedCount < numToRemove; i--) {
            if (objects.get(i) instanceof Motorcycle) {
                removeObject(objects.get(i));
                removedCount++;
            }
        }

        return true;
    }

}