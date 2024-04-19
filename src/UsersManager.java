import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class UsersManager {
    private Habitat habitat;
    private JDialog dialog;

    public UsersManager(Habitat habitat) {
        this.habitat = habitat;
    }

    public void openUsersManagerDialog() {
        habitat.pause();
        // Создаем не модальное диалоговое окно
        dialog = new JDialog(Main.frame, "Подключеные пользователи", false);
        JPanel panel = getPanel();


        dialog.getContentPane().add(panel);
        dialog.setSize(420, 200);
        dialog.setLocationRelativeTo((JFrame) SwingUtilities.getWindowAncestor(habitat));
        dialog.setVisible(true);

        // Добавляем слушатель событий окна для перехвата события закрытия окна
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                habitat.resume(); // Продолжаем работу программы при закрытии окна
                dialog.dispose(); // Закрываем диалоговое окно
            }
        });
    }

    private JPanel getPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel grid = new JPanel(new GridLayout(6, 1));

        JLabel label = new JLabel("Выбирете кокому пользователю передать все \"Живые объекты\":");
        grid.add(label);
        grid.add(new JLabel());

        String[] options = new String[Main.client.listUsers.size() - 1];
        int i = 0;
        for (Integer id : Main.client.listUsers) {
            if (id != Main.client.ClientID) {options[i] = id.toString(); i += 1;};
        }

        JComboBox<String> comboBox = new JComboBox<>(options);
        grid.add(comboBox);
        grid.add(new JLabel());
        grid.add(new JLabel());

        JButton closeButton = new JButton("Отправить");
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String idString = (String) comboBox.getSelectedItem();
                if (idString != null) {
                    int id = Integer.parseInt(idString);
                    try {
                        Main.client.sendObjects(id);
                        System.out.println("Живые объекты успешно отправлены");
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                habitat.resume();
                dialog.dispose();
            }
        });
        grid.add(closeButton, BorderLayout.CENTER);
        panel.add(grid);
        return panel;
    }


}
