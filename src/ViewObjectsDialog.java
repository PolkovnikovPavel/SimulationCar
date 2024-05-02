import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public class ViewObjectsDialog extends JDialog {
    public ViewObjectsDialog(JFrame parent, TreeMap<Integer, Integer> data, Habitat habitat) {
        super(parent, "Живые объекты", true);

        // Создание таблицы с тремя колонками

        String[] columnNames = {"№", "Время рождения", "ID", "Добавить в БД"};

        JPanel baseGrid = new JPanel(new GridLayout(habitat.objects.size() + 1, 4));
        Border border = BorderFactory.createLineBorder(Color.BLACK);  // линия
        for (int i = 0; i < 4; i++) {
            JLabel label = new JLabel(columnNames[i]);
            label.setBorder(border);
            baseGrid.add(label);
        }
        int i = 0;
        for (GameObject object : habitat.objects) {
            i += 1;
            JLabel label0 = new JLabel(String.valueOf(i));
            JLabel label1 = new JLabel(String.valueOf(object.birthday));
            JLabel label2 = new JLabel(String.valueOf(object.ID));
            JButton button = new JButton("Добавить");

            label0.setBorder(border);
            label1.setBorder(border);
            label2.setBorder(border);
            button.setBorder(border);

            button.setBackground(new Color(255, 227, 108));
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        ConnDB.writeObject(object, habitat);
                        button.setBackground(new Color(136, 232, 74));
                        button.setEnabled(false);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
            baseGrid.add(label0);
            baseGrid.add(label1);
            baseGrid.add(label2);
            baseGrid.add(button);
        }


        // Добавление таблицы на панель
        JScrollPane scrollPane = new JScrollPane(baseGrid);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);

        // Событие закрытия окна
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                habitat.resume();
            }
        });

        // Добавление панели на диалоговое окно
        panel.setSize(400, 600);
        getContentPane().add(panel);
        pack();
        setLocationRelativeTo(parent);
    }
}
