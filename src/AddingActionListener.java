import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.*;
import java.util.Objects;

public class AddingActionListener implements KeyListener {
    private final Habitat habitat;

    AddingActionListener(Habitat highway) {
        this.habitat = highway;

        initActionListeners();

        // Реакция на закрытия приложения
        Main.frame.addWindowListener(closeAction);

        // Боковое меню
        Main.startButton.addActionListener(startAction);
        Main.stopButton.addActionListener(stopAction);
        Main.showTextButton.addActionListener(showTextAction);
        Main.hideTextButton.addActionListener(showTextAction);
        Main.is_dialog_check.addActionListener(isDialogCheckAction);
        Main.changeN.addActionListener(changeNAction);
        Main.carProbabilityComboBox.addActionListener(carProbabilityComboBoxAction);
        Main.motorcycleProbabilityComboBox.addActionListener(motorcycleProbabilityComboBoxAction);
        Main.viewObjectsButton.addActionListener(viewObjectsButtonAction);
        Main.sliderV.addChangeListener(sliderVAction);
        Main.carPrioritiesComboBox.addActionListener(carPrioritiesAction);
        Main.motorcyclePrioritiesComboBox.addActionListener(motorcyclePrioritiesAction);
        Main.saveButton.addActionListener(saveAction);
        Main.openButton.addActionListener(openAction);
        Main.viewConnectedUsersButton.addActionListener(viewConnectedUsersAction);

        // Блок времени жизни
        String[] labels = {"Автомобили", "Мотоциклы"};
        for (int i = 0; i < 2; i++) {
            JLabel label = new JLabel(labels[i]);
            JSpinner spinner = new JSpinner();
            spinner.setValue(50);
            Main.liveTimeGrid.add(label);
            Main.liveTimeGrid.add(spinner);

            final int spinnerIndex = i; // Индекс текущего JSpinner

            spinner.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    int value = (int) spinner.getValue();
                    habitat.cheng_live_time(value, spinnerIndex);
                    Main.frame.requestFocusInWindow();
                }
            });
        }

        // Блок остановки/запуска потоков по движению
        for (int i = 0; i < 2; i++) {
            JLabel label = new JLabel(labels[i]);
            JButton button = new JButton("Вкл/Откл");
            Main.intelligenceGrid.add(label);
            Main.intelligenceGrid.add(button);

            final int buttonIndex = i; // Индекс текущего JButton

            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    habitat.setRunning(buttonIndex, !Habitat.objectsRunning[buttonIndex]);
                    Main.frame.requestFocusInWindow();
                }
            });
        }

        // Главное меню
        JMenuItem startItem = new JMenuItem("Старт");
        Main.mainMenu.add(startItem);
        startItem.addActionListener(startAction);
        JMenuItem stopItem = new JMenuItem("Стоп");
        Main.mainMenu.add(stopItem);
        stopItem.addActionListener(stopAction);
        JMenuItem showTextItem = new JMenuItem("Показать текст");
        Main.mainMenu.add(showTextItem);
        showTextItem.addActionListener(showTextAction);
        JMenuItem hideTextItem = new JMenuItem("Скрыть текст");
        Main.mainMenu.add(hideTextItem);
        hideTextItem.addActionListener(showTextAction);
        JMenuItem is_dialog_checkItem = new JMenuItem("Показать/скрыть диолог. меню");
        Main.mainMenu.add(is_dialog_checkItem);
        is_dialog_checkItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                habitat.show_hide_dialog();
                Main.is_dialog_check.setSelected(!Main.is_dialog_check.isSelected());
                Main.frame.requestFocusInWindow();
            }
        });

        JMenuItem changeNItem = new JMenuItem("Применить изменения N");
        Main.mainMenu.add(changeNItem);
        changeNItem.addActionListener(changeNAction);

        JMenuItem console = new JMenuItem("Консоль");
        Main.mainMenu.add(console);
        console.addActionListener(consoleAction);


    }

    @Override
    public void keyTyped(KeyEvent e) {
        habitat.keyTyped(e);
        char k = e.getKeyChar();
        if (k == 't' || k == 'T' || k == 'е' || k == 'Е') {
            if (Main.hideTextButton.isEnabled()) {
                Main.hideTextButton.setEnabled(false);
                Main.showTextButton.setEnabled(true);
            } else {
                Main.showTextButton.setEnabled(false);
                Main.hideTextButton.setEnabled(true);
            }
        };
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
    private ActionListener startAction;
    private ActionListener stopAction;
    private ActionListener showTextAction;
    private ActionListener isDialogCheckAction;
    private ActionListener changeNAction;
    private ActionListener carProbabilityComboBoxAction;
    private ActionListener motorcycleProbabilityComboBoxAction;
    private ActionListener viewObjectsButtonAction;
    private ChangeListener sliderVAction;
    private ActionListener carPrioritiesAction;
    private ActionListener motorcyclePrioritiesAction;
    private WindowAdapter closeAction;
    private ActionListener saveAction;
    private ActionListener openAction;
    private ActionListener consoleAction;
    private ActionListener viewConnectedUsersAction;
    void initActionListeners() {
        startAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                habitat.start();
                Main.frame.requestFocusInWindow();
            }
        };

        stopAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                habitat.stop();
                Main.frame.requestFocusInWindow();
            }
        };

        showTextAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                habitat.show_hide_text();
                Main.showTextButton.setEnabled(!Main.showTextButton.isEnabled());
                Main.hideTextButton.setEnabled(!Main.hideTextButton.isEnabled());
                Main.frame.requestFocusInWindow();
            }
        };

        isDialogCheckAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                habitat.show_hide_dialog();
                Main.frame.requestFocusInWindow();
            }
        };

        changeNAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.carBirthPeriodField.setValue(habitat.cheng_N_car((int) Main.carBirthPeriodField.getValue()));
                Main.motorcycleBirthPeriodField.setValue(habitat.cheng_N_motorcycle((int) Main.motorcycleBirthPeriodField.getValue()));
                Main.frame.requestFocusInWindow();
            }
        };

        carProbabilityComboBoxAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.frame.requestFocusInWindow();
            }
        };
        motorcycleProbabilityComboBoxAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.frame.requestFocusInWindow();
            }
        };

        viewObjectsButtonAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                habitat.viewObjects(Main.frame);
                Main.frame.requestFocusInWindow();
            }
        };

        sliderVAction = new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                habitat.chengGameObjectSpeed(Main.sliderV.getValue());
                Main.frame.requestFocusInWindow();
            }
        };

        carPrioritiesAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                habitat.carsAi.setThreadPriority((String) Objects.requireNonNull(Main.carPrioritiesComboBox.getSelectedItem()));
                Main.frame.requestFocusInWindow();
            }
        };

        motorcyclePrioritiesAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                habitat.motorcyclesAi.setThreadPriority((String) Objects.requireNonNull(Main.motorcyclePrioritiesComboBox.getSelectedItem()));
                Main.frame.requestFocusInWindow();
            }
        };

        closeAction = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                habitat.configManager.save();
                System.out.println("Окно закрывается...");
            }
        };


        saveAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                habitat.pause();
                JFileChooser fileChooser = new JFileChooser();

                // Устанавливаем значение по умолчанию для диологового окна
                File defaultDirectory = new File("gameObjects.ser");
                fileChooser.setCurrentDirectory(defaultDirectory);
                fileChooser.setSelectedFile(defaultDirectory);
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Сериализованные объекты (*.ser)", "ser");
                fileChooser.setFileFilter(filter);

                int returnValue = fileChooser.showSaveDialog(Main.frame);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    System.out.println("Файл сохранен: " + selectedFile.getAbsolutePath());
                    habitat.saveAllObjects(selectedFile.getAbsolutePath());
                } else {
                    System.out.println("Сохранение отменено.");
                }

                habitat.resume();
                Main.frame.requestFocusInWindow();
            }
        };

        openAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                habitat.pause();
                JFileChooser fileOpen = new JFileChooser();

                // Устанавливаем значение по умолчанию для диологового окна
                File defaultDirectory = new File("gameObjects.ser");
                fileOpen.setCurrentDirectory(defaultDirectory);
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Сериализованные объекты (*.ser)", "ser");
                fileOpen.setFileFilter(filter);

                int returnValue = fileOpen.showDialog(Main.frame, "Load");
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileOpen.getSelectedFile();
                    System.out.println("Открыт файл: " + selectedFile.getAbsolutePath());
                    habitat.loadAllObjects(selectedFile.getAbsolutePath());
                } else {
                    System.out.println("Загрузка отменена.");
                }

                habitat.resume();
                Main.frame.requestFocusInWindow();
            }
        };

        consoleAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Console console = new Console(habitat);
                console.openConsoleDialog();
                Main.frame.requestFocusInWindow();
            }
        };

        viewConnectedUsersAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UsersManager abc = new UsersManager(habitat);
                abc.openUsersManagerDialog();
                Main.frame.requestFocusInWindow();
            }
        };



    }


}
