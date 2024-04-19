import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Properties;
import java.util.Scanner;


public class ConfigManager {
    static String filePath = "cfg.txt";
    final private Habitat habitat;
    public ConfigManager(Habitat habitat) {
        this.habitat = habitat;
    }

    public void save() {
        try {
            FileWriter writer = new FileWriter(filePath);
            // запись в файл
            writer.write("version=" + Main.version + '\n');
            writer.write("N_car=" + habitat.getN_car() + '\n');
            writer.write("N_motorcycle=" + habitat.getN_motorcycle() + '\n');
            writer.write("Speed=" + habitat.getSpeed() + '\n');
            writer.write("CarProbability=" + Main.carProbabilityComboBox.getSelectedIndex() + '\n');
            writer.write("MotorcycleProbability=" + Main.motorcycleProbabilityComboBox.getSelectedIndex() + '\n');
            writer.write("CarLiveTime=" + habitat.getLiveTime(0) + '\n');
            writer.write("MotorcycleLiveTime=" + habitat.getLiveTime(1) + '\n');
            writer.write("Is_text=" + habitat.getIs_text() + '\n');
            writer.write("Is_dialog_check=" + habitat.getIs_dialog_check() + '\n');
            writer.write("CarRunPriority=" + habitat.carsAi.priority + '\n');
            writer.write("MotorcycleRunPriority=" + habitat.motorcyclesAi.priority + '\n');
            writer.write("IsCarRun=" + Habitat.objectsRunning[0] + '\n');
            writer.write("IsMotorcycleRun=" + Habitat.objectsRunning[1]); // в конце перенос строки не нужен

            writer.close();
        } catch (IOException e) {
            System.out.println("Ошибка при записи в файл");
            e.printStackTrace();
        }
    }

    public void open() {
        File file = new File(filePath);
        if (file.exists()) {
            if (file.length() == 0) {
                System.out.println("Empty file");
            }
            else {
                try(FileInputStream fis = new FileInputStream(filePath)) {
                    Properties properties = new Properties();
                    properties.load(fis);

                    String value = properties.getProperty("version");
                    if (!value.equals(Main.version)) {
                        System.out.println("Версии не совпадают");
                        return;
                    }

                    value = properties.getProperty("IsCarRun");
                    habitat.setRunning(0, value.equals("true"));

                    value = properties.getProperty("IsMotorcycleRun");
                    habitat.setRunning(1, value.equals("true"));


                    value = properties.getProperty("CarRunPriority");
                    if (!value.equals("Normal")) habitat.carsAi.setThreadPriority(value);
                    switch (value) {
                        case "Min" -> Main.carPrioritiesComboBox.setSelectedIndex(0);
                        case "Normal" -> Main.carPrioritiesComboBox.setSelectedIndex(1);
                        case "Max" -> Main.carPrioritiesComboBox.setSelectedIndex(2);
                    }
                    value = properties.getProperty("MotorcycleRunPriority");
                    if (!value.equals("Normal")) habitat.motorcyclesAi.setThreadPriority(value);
                    switch (value) {
                        case "Min" -> Main.motorcyclePrioritiesComboBox.setSelectedIndex(0);
                        case "Normal" -> Main.motorcyclePrioritiesComboBox.setSelectedIndex(1);
                        case "Max" -> Main.motorcyclePrioritiesComboBox.setSelectedIndex(2);
                    }

                    value = properties.getProperty("CarProbability");
                    int number = Integer.parseInt(value);
                    Main.carProbabilityComboBox.setSelectedIndex(number);

                    value = properties.getProperty("MotorcycleProbability");
                    number = Integer.parseInt(value);
                    Main.motorcycleProbabilityComboBox.setSelectedIndex(number);

                    value = properties.getProperty("N_car");
                    number = Integer.parseInt(value);
                    Main.carBirthPeriodField.setValue(habitat.cheng_N_car(number));

                    value = properties.getProperty("N_motorcycle");
                    number = Integer.parseInt(value);
                    Main.motorcycleBirthPeriodField.setValue(habitat.cheng_N_motorcycle(number));

                    value = properties.getProperty("Speed");
                    number = Integer.parseInt(value);
                    habitat.chengGameObjectSpeed(number);
                    Main.sliderV.setValue(number);

                    value = properties.getProperty("CarLiveTime");
                    number = Integer.parseInt(value);
                    habitat.setLiveTime(number, 0);

                    value = properties.getProperty("MotorcycleLiveTime");
                    number = Integer.parseInt(value);
                    habitat.setLiveTime(number, 1);


                    value = properties.getProperty("Is_text");
                    if (value.equals("false")) {
                        habitat.show_hide_text();
                        Main.showTextButton.setEnabled(!Main.showTextButton.isEnabled());
                        Main.hideTextButton.setEnabled(!Main.hideTextButton.isEnabled());
                    }

                    value = properties.getProperty("Is_dialog_check");
                    if (value.equals("false")) {
                        habitat.show_hide_dialog();
                        Main.is_dialog_check.setSelected(!Main.is_dialog_check.isSelected());
                    }

                }
                catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
