package project;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Line2D;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.*;


public class Main extends JPanel implements KeyListener {

    static class Option {
        Object value;
        Object step;
        int key_up;
        int key_down;

        public Option(Object value, Object step, int key_up, int key_down){
            this.value = value;
            this.step = step;
            this.key_up = key_up;
            this.key_down = key_down;
        }

        public Option(Object value, int key_up){
            this(value, 0, key_up, -1);
        }

        public void step_up(){
            if (value instanceof Double) {
                value = (double) value + (double) step;
            } else if (value instanceof Integer) {
                value = (int) value + (int) step;
            } else if (value instanceof Boolean) {
                value = !(boolean) value;
            } else {
                System.out.println("else");
            }
        }

        public void step_down(){
            if (value instanceof Double) {
                value = (double) value - (double) step;
            } else if (value instanceof Integer) {
                value = (int) value - (int) step;
            }
        }

        public void step_by(double step_amount){
            if (value instanceof Double) {
                value = (double) value + step_amount;
            }
        }

        public void set(Object set_value){
            value = set_value;
        }

        public Object get(){
            return value;
        }

    }


    static LinkedHashMap<String, Option> options = new LinkedHashMap<>();

    int center_h = 1000/2;
    int center_v = 1000/2;

    long last_time = System.currentTimeMillis();
    double game_time = 0;


    static HashMap<Integer, String> options_keys = new HashMap<>();

    public static void init(){
        options.put("arm_length", new Option(100.0, 10.0, KeyEvent.VK_EQUALS, KeyEvent.VK_MINUS));
        options.put("max_level", new Option(0, 1, KeyEvent.VK_UP, KeyEvent.VK_DOWN));
        options.put("split_factor", new Option(0, 1, KeyEvent.VK_RIGHT, KeyEvent.VK_LEFT));

        options.put("init_stroke", new Option(1.0, 1.0, KeyEvent.VK_CLOSE_BRACKET, KeyEvent.VK_OPEN_BRACKET));
        options.put("stroke_factor", new Option(0.0, 0.1, KeyEvent.VK_QUOTE, KeyEvent.VK_SEMICOLON));
        options.put("fade_factor", new Option(0.0, 1.0, KeyEvent.VK_COMMA, KeyEvent.VK_PERIOD));
        options.put("time_factor", new Option(1.0, 1.0, -1, -1));

        options.put("invert_colors", new Option(false, KeyEvent.VK_F));
        options.put("init_arrow", new Option(true, KeyEvent.VK_I));
        options.put("aa", new Option(true, KeyEvent.VK_A));
        options.put("stop", new Option(false, KeyEvent.VK_SPACE));

        for (Map.Entry<String, Option> entry : options.entrySet()) {
            Option value = entry.getValue();
            String key = entry.getKey();
            options_keys.put(value.key_up, key + " up");
            options_keys.put(value.key_down, key+ " down");
        }
    }

    public void draw_clock(Graphics2D g2d, double x1, double y1, double x2, double y2, int level){
        if (level <= (int) options.get("max_level").get()) {
            if (level == 0 && (boolean) options.get("init_arrow").get()){
                double[] pos_2 = get_clock_arrow_delta();
                x2 = x2 + pos_2[0];
                y2 = y2 + pos_2[1];
            }
            float stroke_num = (float) ( (double) options.get("init_stroke").get() + (level * (double) options.get("stroke_factor").get()));
            Stroke stroke;
            if (stroke_num > 0){
                stroke = new BasicStroke(stroke_num);
            } else {
                stroke = new BasicStroke(0.1F);
            }

            int fade = (int) (255 - (level * (double) options.get("fade_factor").get()));
            if (fade < 0 ) {fade = 0;}

            if((boolean) options.get("invert_colors").get()){
                g2d.setColor(new Color(255, 255, 255, fade));
            } else {
                g2d.setColor(new Color(36, 36, 36, fade));
            }

            g2d.setStroke(stroke);
            g2d.draw(new Line2D.Double(x1, y1, x2, y2));

            for (int i = 0; i < (int) options.get("split_factor").get(); i++) {
                double[] pos_2 = get_clock_arrow_delta(level + i + 1);
                draw_clock(g2d, x2, y2, x2+pos_2[0], y2+pos_2[1],level + 1);
            }
        }
    }

    public void draw_clock(Graphics2D g2d){
        draw_clock(g2d, center_h, center_v, center_h, center_v, 0);
    }

    public void paintComponent(Graphics g) {
        long real_time = System.currentTimeMillis();
        long delta_time = real_time - last_time;
        last_time = real_time;
        if (! (boolean) options.get("stop").get()){
            game_time += (delta_time * (double) options.get("time_factor").get());
        }

        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        Dimension window_size = this.getSize();

        center_v = window_size.height/2;
        center_h = window_size.width/2;

        if ((boolean) options.get("aa").get()){
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        } else {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        }

        if((boolean) options.get("invert_colors").get()){
            g2d.setColor(Color.DARK_GRAY);
            g2d.fillRect(0, 0, window_size.width, window_size.height);
        }

        draw_clock(g2d);
    }

    public double[] get_clock_arrow_delta(int shift){

        double secs = ( game_time / 1000 ) % 60;
        secs = secs * ((shift) + 1);

        double angle = (Math.PI * 2 * secs / 60) - (Math.PI / 2);

        double x2 = Math.round(Math.cos(angle) * (double) options.get("arm_length").get());
        double y2 = Math.round(Math.sin(angle) * (double) options.get("arm_length").get());


        return new double[]{x2, y2};
    }

    public double[] get_clock_arrow_delta(){
        return get_clock_arrow_delta(0);
    }

    public static void main(String[] args) {

        init();

        Main panel = new Main();
        JFrame window = new JFrame();
        panel.setBackground(Color.WHITE);
        window.addKeyListener(panel);

        window.setTitle("Fractal Clock");
        window.setSize(1000, 1000);
        window.setVisible(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.add(panel);

        new Timer(16, e -> panel.repaint()).start();




    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        Menu.update();
        int key_code = e.getKeyCode();
        int key_mods = e.getModifiersEx();

        if (key_mods == InputEvent.CTRL_DOWN_MASK){
            switch (key_code){
                case KeyEvent.VK_RIGHT -> game_time += 1;
                case KeyEvent.VK_LEFT -> game_time -= 1;
            }
        } else if (key_mods == InputEvent.SHIFT_DOWN_MASK){
            switch (key_code){
                case KeyEvent.VK_RIGHT -> game_time += 10;
                case KeyEvent.VK_LEFT -> game_time -= 10;

                case KeyEvent.VK_UP -> options.get("time_factor").step_by(0.05);
                case KeyEvent.VK_DOWN -> options.get("time_factor").step_by(-0.05);
            }
        } else if (options_keys.containsKey(key_code)){

            String key_dir = options_keys.get(key_code);
            String[] key_dir_split = key_dir.split(" ");

            String key = key_dir_split[0];
            String dir = key_dir_split[1];
            if (dir.equals("up")){
                options.get(key).step_up();
            } else {
                options.get(key).step_down();
            }
        } else {
            switch (key_code){
                case KeyEvent.VK_Q -> System.exit(0);
                case KeyEvent.VK_M -> Menu.create_menu_window();

                case KeyEvent.VK_1 -> {
                    options.get("arm_length").set(100.0);
                    options.get("init_stroke").set(5.0);
                    options.get("split_factor").set(6);
                    options.get("fade_factor").set(48.0);
                    options.get("init_arrow").set(false);
                    options.get("max_level").set(4);
                    options.get("stroke_factor").set(-0.9);
                }

                case KeyEvent.VK_2 -> {
                    options.get("max_level").set(2);
                    options.get("arm_length").set(150.0);
                    options.get("split_factor").set(15);
                    options.get("init_arrow").set(false);
                }

                case KeyEvent.VK_3 -> {
                    options.get("max_level").set(6);
                    options.get("arm_length").set(300.0);
                    options.get("split_factor").set(4);
                    options.get("init_arrow").set(false);
                    options.get("aa").set(false);
                }

                case KeyEvent.VK_4 -> {
                    options.get("max_level").set(3);
                    options.get("arm_length").set(160.0);
                    options.get("init_stroke").set(0.0);
                    options.get("split_factor").set(15);

                    options.get("stroke_factor").set(2.6);
                    options.get("fade_factor").set(78.0);

                    options.get("init_arrow").set(false);
                    options.get("aa").set(false);
                    options.get("invert_colors").set(false);
                }

                case KeyEvent.VK_0 -> {
                    options.get("max_level").set(1);
                    options.get("arm_length").set(100.0);
                    options.get("init_stroke").set(1.0);
                    options.get("split_factor").set(0);

                    options.get("stroke_factor").set(0.0);
                    options.get("fade_factor").set(0.0);

                    options.get("init_arrow").set(true);
                    options.get("aa").set(true);
                    options.get("invert_colors").set(false);
                }
            }
        }



    }

    @Override
    public void keyReleased(KeyEvent e) {

    }


}