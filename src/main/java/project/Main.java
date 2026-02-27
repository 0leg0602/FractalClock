package project;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Line2D;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Oleg
 * <br>
 * time_start: ???
 * time_finish: Fri Feb 27 00:53:01 EST 2026
 * <br>
 * Take insparation from "<a href="https://www.egui.rs/#clock">egui Fractal Clock</a>"
 * <br>
 * I wanted to try to make my own Fractal Clock
 * <br>
 * I made the clock fully customiziable, all the customizable valuases as well as thier shortcuts are visiable in the Menu
 * <br>
 * The menu can be open with key "M"
 * <br>
 * There are some shortcutst which are not listed in the menu
 * First presents keys from 1 to 4 change the clock to my custom premade presets
 * <br>
 * Second is mod keys, shift arrow up speeds up the clock, shift arrow down slows down
 * <br>
 * Shift arrow left moves clock by 10 frames backwards, right arrow forward
 * <br>
 * Shift arrow left moves clock by 1 frame backwards, right arrow forward
 *
 */


public class Main extends JPanel implements KeyListener {

    /**
     *  Custom class for every option for the fractal clock
     */
    static class Option {

        Object value;
        Object step;
        int key_up;
        int key_down;

        /**
         * I have not used class Object much before, so I decided to give it a try.
         * It is defensively not the most organized or efficient way of storing options,
         * but I am just trying new thing out.
         * @param value The actual value for the option, my logic expects it to be Integer, Double, or Boolean.
         * @param step When step_{up, down} is called how much should the value move by.
         * @param key_up Key code to trigger step_up.
         * @param key_down Key code to trigger step_down.
         */
        public Option(Object value, Object step, int key_up, int key_down){
            this.value = value;
            this.step = step;
            this.key_up = key_up;
            this.key_down = key_down;
        }

        /**
         * Constructor, but for boolean, for simplicity key_down is set to -1,
         * because changing a boolean value requires only 1 key.
         * @param value The actual value for the option, for this constructor value should be a boolean
         * @param key_up Key code to flip the boolean value
         */
        public Option(Object value, int key_up){
            this(value, 0, key_up, -1);
        }

        /**
         * Increase the value by the step
         */
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

        /**
         * Decrease the value by the step
         */
        public void step_down(){
            if (value instanceof Double) {
                value = (double) value - (double) step;
            } else if (value instanceof Integer) {
                value = (int) value - (int) step;
            }
        }

        /**
         * Change value by a specific amount
         * (Only works for double)
         */
        public void step_by(double step_amount){
            if (value instanceof Double) {
                value = (double) value + step_amount;
            }
        }

        /**
         * Set value to a specific value
         * @param set_value the value to set it to
         */
        public void set(Object set_value){
            value = set_value;
        }

        /**
         * Getter
         * @return the value
         */
        public Object get(){
            return value;
        }

    }

    /**
     * All the options are stored in a hash map
     * however when drawing the menu I simply iterate through the hash map
     * if I use regular hash map instead of the linked one the order is going to be random,
     * but I want the buttons on the menu to have a certain order.
     */
    static LinkedHashMap<String, Option> options = new LinkedHashMap<>();

    int center_h = 1000/2;
    int center_v = 1000/2;

    long last_time = System.currentTimeMillis();
    double game_time = 0;

    /**
     * Second hash map this time the order does not matter
     * It is a sort of wrapper for the options hash map
     * which allows me to conviniently call it like this:
     * options.get(options_keys.get(key_code));
     */
    static HashMap<Integer, String> options_keys = new HashMap<>();

    /**
     * Initialize the options and options_keys hash maps
     */
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

    /**
     * recursively drawing the clock
     * @param g2d Graphics
     * @param x1 initial x
     * @param y1 initial y
     * @param x2 final x
     * @param y2 final y
     * @param level what level of recursion this is zero being the first
     */
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

    /**
     * Simple wrapper to make paintComponent less cluttered
     * @param g2d Graphics
     */
    public void draw_clock(Graphics2D g2d){
        draw_clock(g2d, center_h, center_v, center_h, center_v, 0);
    }

    @Override
    public void paintComponent(Graphics g) {
        if ( (double) options.get("fade_factor").get() < 0.0) {
            options.get("fade_factor").set(0.0);
            Menu.update();
        }
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

    /**
     * How much the arrow point need to be shifted on x and y
     * based on what is the current time is
     * @param shift how much to rotate the arrow from the original position
     *              if none of the arrows were shifted the recursion would draw one long straight line.
     * @return int array first element being x cord and second y cord
     */
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
        Menu.update();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }


}