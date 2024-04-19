import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class Console {
    private JTextArea textArea;
    private PipedOutputStream pipedOutputStream;
    private BufferedReader consoleReader;
    private Habitat habitat;
    private JDialog dialog;

    public Console(Habitat habitat) {
        this.habitat = habitat;
    }

    public void openConsoleDialog() {
        habitat.pause();
        // Создаем диалоговое окно с многострочным текстовым полем
        dialog = new JDialog(Main.frame, "Консоль", false);

        // Создаем многострочное текстовое поле
        textArea = new JTextArea(13, 40);

        // Устанавливаем чёрный фон и белый шрифт для текстового поля
        textArea.setBackground(Color.BLACK);
        textArea.setForeground(Color.WHITE);

        // Добавляем текстовое поле в полосу прокрутки
        JScrollPane scrollPane = new JScrollPane(textArea);

        // Добавляем полосу прокрутки на панель диалогового окна
        dialog.add(scrollPane);

        dialog.pack();
        dialog.setLocationRelativeTo((JFrame) SwingUtilities.getWindowAncestor(habitat));
        dialog.setVisible(true);



        // Добавляем обработчик нажатия клавиши Enter для textArea
        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    processInputText(); // Обрабатываем ввод после нажатия Enter
                }
            }
        });

        // Добавляем слушатель событий окна для перехвата события закрытия окна
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                habitat.resume(); // Продолжаем работу программы при закрытии окна
                dialog.dispose(); // Закрываем диалоговое окно
            }
        });


        try {
            // Создаем каналы ввода-вывода
            PipedInputStream pipedInputStream = new PipedInputStream();
            pipedOutputStream = new PipedOutputStream(pipedInputStream);

            // Создаем поток для чтения данных из канала ввода-вывода
            InputStreamReader inputStreamReader = new InputStreamReader(pipedInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            // Создаем поток для обработки вводимых команд
            Thread processingThread = new Thread(() -> {
                try {
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        // Обрабатываем вводимую команду
                        processCommand(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            processingThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Создаем поток для чтения команд из консоли
        consoleReader = new BufferedReader(new InputStreamReader(System.in));
        Thread consoleInputThread = new Thread(this::readConsoleInput);
        consoleInputThread.start();
    }

    // Метод для обработки ввода после нажатия Enter
    private void processInputText() {
        String inputText = textArea.getText().trim(); // Получаем текст из textArea
        textArea.setText(""); // Очищаем textArea
        sendCommand(inputText); // Отправляем команду на обработку
    }

    // Метод для обработки введенной команды
    public void processCommand(String command) {
        if (command.startsWith("Сократить число мотоциклов на ")) {
            try {
                String[] parts = command.split(" ");
                if (parts.length >= 5) { // Добавлено условие проверки длины массива parts
                    int percent = Integer.parseInt(parts[4].replace("%", ""));
                    if (habitat.reduceMotorcyclesByPercent(percent))
                    {
                        appendText("Команда успешно выполнена.\n");
                    } else {
                        appendText("Некорректное значение процента, от 0 до 100%\n");
                    }
                } else {
                    appendText("Некорректный формат команды.\n");
                }
            } catch (NumberFormatException e) {
                appendText("Некорректный формат процента.\n");
            }
        } else {
            appendText("Неизвестная команда.\n");
        }
    }


    // Метод для добавления текста в JTextArea из другого потока
    private void appendText(String text) {
        SwingUtilities.invokeLater(() -> textArea.append(text));
    }

    // Метод для чтения команд из консоли
    private void readConsoleInput() {
        try {
            String line;
            while ((line = consoleReader.readLine()) != null) {
                sendCommand(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Метод для отправки команды в обработчик через канал вывода
    private void sendCommand(String command) {
        try {
            pipedOutputStream.write((command + "\n").getBytes());
            pipedOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
