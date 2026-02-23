package project;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

public class Menu extends JPanel{

    public static void main(String[] args) {
       return;
    }

    public static void create_menu_window(){
//        options.add()

//        Option test2 = new Option("test", Main.arm_length, 10);

        Menu panel = new Menu();
        JFrame window = new JFrame();
        panel.setBackground(Color.WHITE);

        window.setTitle("Menu");
        window.setSize(200, 500);

//        JLabel label = new JLabel("Hello, Swing Label!");
//        panel.add(label);

        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Components will fill horizontally and have some padding
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5); // Add 5px padding on all sides

        // Button 1: top-left (0,0)
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("arm length"), gbc);

        // Button 2: top-right (1,0)
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(new JLabel("0"), gbc);

        // Button 3: middle row (0,1), spans 2 columns
        gbc.gridx = 2;
        gbc.gridy = 0;
//        gbc.gridwidth = 2; // Span two columns
        JButton button = new JButton("-");
        panel.add(button, gbc);
//        button.addActionListener(e -> {
//            System.out.println("Button was clicked!");
////            Main.arm_length += 10;
//            test2.value = test2.value + 10;
//            test2
//        });

        // Button 4: bottom-left (0,2)
        gbc.gridx = 3;
        gbc.gridy = 0;
        panel.add(new JButton("+"), gbc);

        // Button 5: bottom-right (1,2), uses extra vertical space
//        gbc.gridx = 1;
//        gbc.gridy = 2;
//        gbc.weighty = 1.0; // Request extra vertical space
//        gbc.fill = GridBagConstraints.BOTH; // Fill both horizontally and vertically
//        panel.add(new JButton("Button 5"), gbc);

//        JTextField textField = new JTextField(20);
//        JButton button = new JButton("Submit");
//        panel.add(textField);
//        panel.add(button);

        window.setVisible(true);
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        window.add(panel);

//        new Timer(16, e -> {
//            panel.repaint();
//        }).start();
    }

    public static void update(){

    }
}
