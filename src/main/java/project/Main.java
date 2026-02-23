package project;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Line2D;
import javax.swing.*;


public class Main extends JPanel implements KeyListener {


    int center_h = 1000/2;
    int center_v = 1000/2;

    long last_time = System.currentTimeMillis();
    double game_time = 0;

    // Options
    int max_level = 1;
    int arm_length = 100;
    float init_stroke = 1F;
    int split_factor = 0;

    double stroke_factor = 0;
    double length_factor = 0;
    double fade_factor = 0;
    double time_factor = 1;
    double separation_factor = 1;

    boolean init_arrow = true;
    boolean aa = true;
    boolean invert_colors = false;
    boolean stop = false;

    public void drawCircle(Graphics g, int x, int y, int width, int level) {
        if (level >= 1) {
            g.drawOval(x, y, width, width);
            drawCircle(g, x-width/4, y, width/2,  level-1);
        }
    }

    public void draw_clock(Graphics2D g2d, double x1, double y1, double x2, double y2, int level){
        if (level <= max_level) {
            if (level == 0 && init_arrow){
                double[] pos_2 = get_clock_arrow_delta();
                x2 = x2 + pos_2[0];
                y2 = y2 + pos_2[1];
            }
            float stroke_num = (float) (init_stroke + (level*stroke_factor));
            Stroke stroke;
            if (stroke_num > 0){
                stroke = new BasicStroke(stroke_num);
            } else {
                stroke = new BasicStroke(0.1F);
            }

            int fade = (int) (255 - (level * fade_factor));
            if (fade < 0 ) {fade = 0;}

            if(invert_colors){
                g2d.setColor(new Color(255, 255, 255, fade));
            } else {
                g2d.setColor(new Color(36, 36, 36, fade));
            }

            g2d.setStroke(stroke);
//            g2d.drawLine(x1, y1, x2, y2);
            g2d.draw(new Line2D.Double(x1, y1, x2, y2));

            for (int i = 0; i < split_factor; i++) {
                double[] pos_2 = get_clock_arrow_delta(level + i + 1);
                draw_clock(g2d, x2, y2, x2+pos_2[0], y2+pos_2[1],level + 1);
            }
        }
    }

    public void draw_clock(Graphics2D g2d){
        draw_clock(g2d, center_h, center_v, center_h, center_v, 0);
    }

    public void paintComponent(Graphics g) {
//        System.out.println(game_time);
        long real_time = System.currentTimeMillis();
        long delta_time = real_time - last_time;
        last_time = real_time;
        if (!stop){
            game_time += (double) (delta_time * time_factor);
        }

        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        Dimension window_size = this.getSize();

        center_v = window_size.height/2;
        center_h = window_size.width/2;

        if (aa){
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        } else {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        }

        if(invert_colors){
            g2d.setColor(Color.DARK_GRAY);
            g2d.fillRect(0, 0, window_size.width, window_size.height);
        }

        draw_clock(g2d);
    }

    public double[] get_clock_arrow_delta(int shift){

        double secs = ( game_time / 1000 ) % 60;
        secs = secs * ((shift*separation_factor) + 1);

        double angle = (Math.PI * 2 * secs / 60) - (Math.PI / 2);

        double x2 = Math.round(Math.cos(angle) * arm_length);
        double y2 = Math.round(Math.sin(angle) * arm_length);

        return new double[]{x2, y2};
    }

    public double[] get_clock_arrow_delta(){
        return get_clock_arrow_delta(0);
    }

    public static void main(String[] args) {

        Main panel = new Main();
        JFrame window = new JFrame();
        panel.setBackground(Color.WHITE);
        window.addKeyListener(panel);

        window.setTitle("Recursion");
        window.setSize(1000, 1000);
        window.setVisible(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.add(panel);

        new Timer(16, e -> {
            panel.repaint();
        }).start();




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

                case KeyEvent.VK_UP -> separation_factor += 0.01;
                case KeyEvent.VK_DOWN -> separation_factor -= 0.01;
            }
        } else if (key_mods == InputEvent.SHIFT_DOWN_MASK){
            switch (key_code){
                case KeyEvent.VK_RIGHT -> game_time += 10;
                case KeyEvent.VK_LEFT -> game_time -= 10;

                case KeyEvent.VK_UP -> time_factor += 0.05;
                case KeyEvent.VK_DOWN -> time_factor -= 0.05;
            }
        } else {
            switch (key_code){
                case KeyEvent.VK_UP -> max_level+=1;
                case KeyEvent.VK_DOWN -> max_level-=1;

                case KeyEvent.VK_RIGHT -> split_factor += 1;
                case KeyEvent.VK_LEFT -> split_factor -= 1;

                case KeyEvent.VK_EQUALS -> arm_length += 10;
                case KeyEvent.VK_MINUS -> arm_length -= 10;

                case KeyEvent.VK_CLOSE_BRACKET -> init_stroke += 1;
                case KeyEvent.VK_OPEN_BRACKET -> init_stroke -= 1;

                case KeyEvent.VK_QUOTE -> stroke_factor += 0.1;
                case KeyEvent.VK_SEMICOLON -> stroke_factor -= 0.1;

                case KeyEvent.VK_COMMA -> fade_factor -= 1;
                case KeyEvent.VK_PERIOD -> fade_factor += 1;

                case KeyEvent.VK_I -> init_arrow = !init_arrow;
                case KeyEvent.VK_F -> invert_colors = !invert_colors;
                case KeyEvent.VK_A -> aa = !aa;
                case KeyEvent.VK_SPACE -> stop = !stop;
                case KeyEvent.VK_Q -> System.exit(0);

                case KeyEvent.VK_1 -> {
                    arm_length = 100;
                    init_stroke = 5;
                    split_factor = 6;
                    fade_factor = 48;
                    init_arrow = false;
                    max_level = 4;
                    stroke_factor = -0.9;
                }

                case KeyEvent.VK_2 -> {
                    max_level = 2;
                    arm_length = 150;
                    split_factor = 15;
                    init_arrow = false;
                }

                case KeyEvent.VK_3 -> {
                    max_level = 6;
                    arm_length = 300;
                    split_factor = 4;
                    init_arrow = false;
                    aa = false;
                }

                case KeyEvent.VK_0 -> {
                    max_level = 1;
                    arm_length = 100;
                    init_stroke = 1F;
                    split_factor = 0;

                    stroke_factor = 0;
                    length_factor = 0;
                    fade_factor = 0;

                    init_arrow = true;
                    aa = true;
                    invert_colors = false;
                }
            }
        }


    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}