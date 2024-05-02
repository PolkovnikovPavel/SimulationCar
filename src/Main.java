import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;

public class Main {
    static String version = "0.7.0";
    public static JFrame frame;
    public static JPanel mainPanel = new JPanel(null);   // Используем null layout
    public static JPanel controlPanel = new JPanel();
    public static JButton startButton = new JButton("Старт");;
    public static JButton stopButton = new JButton("Стоп");
    public static JButton saveButton = new JButton("Сохранить");
    public static JButton openButton = new JButton("Открыть");
    public static JCheckBox is_dialog_check = new JCheckBox("Показывать диологовое окно", true);
    public static JLabel isTimeSimulationLabel = new JLabel("Время симулиции:");
    public static JButton showTextButton = new JButton("Показать");
    public static JButton hideTextButton = new JButton("Скрыть");
    public static JSpinner carBirthPeriodField = new JSpinner();
    public static JSpinner motorcycleBirthPeriodField = new JSpinner();
    public static JPanel BirthGrid = new JPanel(new GridLayout(2, 2));
    public static JLabel BirthLabel = new JLabel("Период появления (сек):");
    public static JButton changeN = new JButton("Применить");
    public static JLabel liveTimeLabel = new JLabel("Время жизни объектов:");
    public static JPanel liveTimeGrid = new JPanel(new GridLayout(2, 2));
    public static JComboBox<String> carProbabilityComboBox;
    public static JComboBox<String> motorcycleProbabilityComboBox;
    public static JLabel carProbabilityLabel = new JLabel("Вероятности появления:");
    public static JPanel probabilityGrid = new JPanel(new GridLayout(4, 2));
    public static JSlider sliderV = new JSlider(JSlider.HORIZONTAL, 0, 200, 20);
    public static JButton viewObjectsButton = new JButton("Текущие объекты");
    public static JButton viewConnectedUsersButton = new JButton("Все пользователи");
    public static JButton loadObjectsFromDBButton = new JButton("Загрузить из БД");
    public static JButton clearDBButton = new JButton("Очистить БД");

    public static JPanel intelligenceGrid = new JPanel(new GridLayout(2, 2));
    public static JPanel priorityGrid = new JPanel(new GridLayout(2, 2));
    public static JComboBox<String> carPrioritiesComboBox;
    public static JComboBox<String> motorcyclePrioritiesComboBox;
    public static JMenuBar menuBar = new JMenuBar();
    public static JMenu mainMenu = new JMenu("Главное меню");
    public static Client client;




    public static void main(String[] args) {
        int habitatWidth = 800;
        int habitatHeight = 600;
        int controlWidth = 300;
        Object monitor = new Object(); // Для синхронизации

        Habitat habitat = new Habitat(habitatWidth, habitatHeight, monitor);

        // настройка клиента
        client = new Client(habitat);
        Thread clientThread = new Thread(client);
        clientThread.start();





        // настройка окна
        frame = new JFrame("Автомобили и мотоциклы (" + client.ClientID + ")");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(habitatWidth + controlWidth + 19, habitatHeight + 64);
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBounds(0, 0, controlWidth, habitatHeight);
        habitat.setBounds(controlWidth, 0, habitatWidth, habitatHeight);
        controlPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10)); // отступы по 10 пикселей


        mainPanel.add(controlPanel);
        mainPanel.add(habitat);
        frame.getContentPane().add(mainPanel);

        // базовые элементы управления
        JPanel baseGrid = new JPanel(new GridLayout(2, 2, 30, 2));
        baseGrid.add(startButton);
        baseGrid.add(saveButton);
        baseGrid.add(stopButton);
        baseGrid.add(openButton);
        startButton.setBackground(new Color(40, 212, 80));
        stopButton.setBackground(new Color(255, 77, 57));
        saveButton.setBackground(new Color(252, 217, 61));
        openButton.setBackground(new Color(156, 123, 255, 245));

        stopButton.setEnabled(false);
        showTextButton.setEnabled(false);



        // Блок верхних элементов управления
        controlPanel.add(baseGrid);
        controlPanel.add(is_dialog_check);

        controlPanel.add(isTimeSimulationLabel);
        controlPanel.add(showTextButton);
        controlPanel.add(hideTextButton);


        // Блок периода появления
        carBirthPeriodField.setValue(2);
        motorcycleBirthPeriodField.setValue(3);
        BirthGrid.add(new JLabel("Автомобилей:"));
        BirthGrid.add(carBirthPeriodField);
        BirthGrid.add(new JLabel("Мотоциклов:"));
        BirthGrid.add(motorcycleBirthPeriodField);

        controlPanel.add(BirthLabel);
        controlPanel.add(BirthGrid);
        controlPanel.add(changeN);



        // Блок времени жизни
        controlPanel.add(liveTimeLabel);
        controlPanel.add(liveTimeGrid);


        // Блок вероятности появления и скорости
        String[] carProbabilities = {"10%", "20%", "30%", "40%", "50%", "60%", "70%", "80%", "90%", "100%"};
        carProbabilityComboBox = new JComboBox<>(carProbabilities);
        carProbabilityComboBox.setSelectedIndex(4);
        String[] motorcycleProbabilities = {"10%", "20%", "30%", "40%", "50%", "60%", "70%", "80%", "90%", "100%"};
        motorcycleProbabilityComboBox = new JComboBox<>(motorcycleProbabilities);
        motorcycleProbabilityComboBox.setSelectedIndex(4);
        // таблица
        probabilityGrid.add(new JLabel("Автомобилей")); // вероятность
        probabilityGrid.add(carProbabilityComboBox);
        probabilityGrid.add(new JLabel("Мотоциклов"));
        probabilityGrid.add(motorcycleProbabilityComboBox);
        probabilityGrid.add(new JLabel("Изменение V:")); // скорость
        probabilityGrid.add(sliderV);

        controlPanel.add(carProbabilityLabel);
        controlPanel.add(probabilityGrid);



        // Блок остановки интелекта
        controlPanel.add(new JLabel("Остановка транспорта:"));
        controlPanel.add(intelligenceGrid);


        // Блок выставления приоритетов
        String[] priorities = {"Min", "Normal", "Max"};
        carPrioritiesComboBox = new JComboBox<>(priorities);
        carPrioritiesComboBox.setSelectedIndex(1);
        motorcyclePrioritiesComboBox = new JComboBox<>(priorities);
        motorcyclePrioritiesComboBox.setSelectedIndex(1);
        // таблица
        priorityGrid.add(new JLabel("Автомобилей"));
        priorityGrid.add(carPrioritiesComboBox);
        priorityGrid.add(new JLabel("Мотоциклов"));
        priorityGrid.add(motorcyclePrioritiesComboBox);

        controlPanel.add(new JLabel("Приоритет потоков:"));
        controlPanel.add(priorityGrid);



        // Блок кнопока внизу
        JPanel base2Grid = new JPanel(new GridLayout(1, 2, 20, 2));
        base2Grid.add(viewObjectsButton);
        base2Grid.add(viewConnectedUsersButton);

        JPanel base3Grid = new JPanel(new GridLayout(1, 2));
        base3Grid.add(loadObjectsFromDBButton);
        base3Grid.add(clearDBButton);
        loadObjectsFromDBButton.setBackground(new Color(105, 179, 253));
        clearDBButton.setBackground(new Color(255, 71, 100, 255));

        controlPanel.add(base2Grid);
        controlPanel.add(base3Grid);



        // Блок главного меню
        menuBar.add(mainMenu);
        frame.setJMenuBar(menuBar);



        AddingActionListener actionListener = new AddingActionListener(habitat);

        frame.addKeyListener(actionListener);
        frame.setFocusable(true);
        frame.requestFocusInWindow();
        frame.setVisible(true);

        habitat.configManager.open(); // открытие файла конфигурации

        // Поток отрисовки
        int FPS = 120;
        Timer timer = new Timer((int) 1000 / FPS, e -> {
            synchronized (monitor) {
                frame.repaint();
            }
        });
        timer.start();
    }
}