package project;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class Menu extends JPanel{

    static HashMap<String, JLabel> option_labels = new HashMap<>();

    public static void create_menu_window(){

        Menu panel = new Menu();
        JFrame window = new JFrame();
        panel.setBackground(Color.WHITE);

        window.setTitle("Menu");
        window.setSize(400, 500);

        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        int i = 0;

        for (Map.Entry<String, Main.Option> entry : Main.options.entrySet()) {

            Main.Option value = entry.getValue();
            String key = entry.getKey();
            gbc.gridy = i;

            gbc.gridx = 0;
            panel.add(new JLabel(key), gbc);

            gbc.gridx = 1;
            JLabel option_label = new JLabel("0");
            panel.add(option_label, gbc);
            option_labels.put(key, option_label);

            if(value.get() instanceof  Boolean){
                gbc.gridx = 2;
                JButton button_minus = new JButton("!");
                panel.add(button_minus, gbc);

                button_minus.addActionListener(e -> {
                    value.step_up();
                    update();
                });

                gbc.gridx = 5;
                panel.add(new JLabel(KeyEvent.getKeyText(value.key_up)), gbc);
            } else {
                gbc.gridx = 2;
                JButton button_minus = new JButton("-");
                panel.add(button_minus, gbc);

                button_minus.addActionListener(e -> {
                    value.step_down();
                    update();
                });

                gbc.gridx = 3;
                JButton button_plus = new JButton("+");
                panel.add(button_plus, gbc);

                button_plus.addActionListener(e -> {
                    value.step_up();
                    update();
                });
                gbc.gridx = 5;
                if (key.equals("time_factor")){
                    panel.add(new JLabel("Shift/Ctrl arrows"), gbc);
                } else {
                    panel.add(new JLabel(KeyEvent.getKeyText(value.key_up) + " | " + KeyEvent.getKeyText(value.key_down) ), gbc);
                }
            }
            i++;
        }

        window.setVisible(true);
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        window.add(panel);

        update();
    }

    public static void update(){
        for (Map.Entry<String, JLabel> entry : option_labels.entrySet()) {

            JLabel value = entry.getValue();
            String key = entry.getKey();
            Object main_val = Main.options.get(key).get();

            if (main_val instanceof Double){
                value.setText(String.format("%.2f", (double) Main.options.get(key).get()));
            } else {
                value.setText(Main.options.get(key).get().toString());
            }
        }
    }
}
