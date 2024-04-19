import javax.swing.*;
import java.awt.*;
import java.util.*;

public class ViewObjectsDialog extends JDialog {
    public ViewObjectsDialog(JFrame parent, TreeMap<Integer, Integer> data) {
        super(parent, "Живые объекты", true);

        // Создание таблицы с двумя колонками
        Integer[][] rowData = new Integer[data.size()][2];
        int i = 0;
        for (Map.Entry<Integer, Integer> entry : data.entrySet()) {
            rowData[i][0] = entry.getValue();
            rowData[i++][1] = entry.getKey();
        }

        String[] columnNames = {"Время рождения", "ID"};
        JTable table = new JTable(rowData, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);

        // Добавление таблицы на панель
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);

        // Добавление панели на диалоговое окно
        getContentPane().add(panel);
        pack();
        setLocationRelativeTo(parent);
    }
}
