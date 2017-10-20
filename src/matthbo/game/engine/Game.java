package matthbo.game.engine;

import matthbo.game.engine.graphics.Screen;
import matthbo.game.engine.input.InputHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public abstract class Game extends Canvas implements Runnable, GameInterface {

    private static final int DEFAULT_WIDTH = 1280;
    private static final int DEFAULT_HEIGHT = 720;
    private static final int DEFAULT_SCALE = 2;
    private static int width;
    private static int height;

    private Thread thread;
    private JFrame frame;
    private boolean running = false;
    private boolean fullscreen = false;

    private Screen screen;
    private Dimension size;
    protected InputHandler input;

    private BufferedImage image;
    private int[] pixels;

    public Game(int width, int height, int scale) {
        Game.width = width / scale;
        Game.height = height / scale;

        image = new BufferedImage(Game.width, Game.height, BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();

        size = new Dimension(width, height);
        setPreferredSize(size);

        screen =  new Screen(Game.width, Game.height);
        frame = new JFrame();
        input = new InputHandler();

        addKeyListener(input);
    }

    public Game() {
        this(DEFAULT_WIDTH , DEFAULT_HEIGHT, DEFAULT_SCALE);
    }

    public synchronized void start(){
        running = true;
        thread = new Thread(this, "Display");
        thread.start();
    }

    public synchronized void stop(){
        running = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        long timer = System.currentTimeMillis();
        final double ns = 1000000000.0 / 60;
        double delta = 0;
        int frames = 0;
        int updates = 0;
        requestFocusInWindow();
        while(running){
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while(delta >= 1){
                update();
                updates++;
                delta--;
                postUpdate();
            }
            render();
            frames++;

            if(System.currentTimeMillis() - timer > 1000){
                timer += 1000;
                System.out.println(updates + "ups, " + frames + "fps");
                updates = 0;
                frames = 0;
            }
        }
        stop();
    }

    private void postUpdate() {
        if(input != null) input.clear();
    }

    protected void fullscreen(){
        frame.dispose();
        if(!fullscreen) {
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setUndecorated(true);

            fullscreen = true;
        } else {
            frame.setExtendedState(JFrame.NORMAL);
            frame.setUndecorated(false);
            frame.pack();
            frame.setLocationRelativeTo(null);

            fullscreen = false;
        }
        frame.setVisible(true);
        requestFocusInWindow();
    }

    protected void clearScreen(){
        screen.clear();
    }

    protected void renderScreen(){
        screen.render();
        System.arraycopy(screen.pixels, 0, pixels, 0, pixels.length);
    }

    public JFrame getFrame(){
        return frame;
    }

    protected BufferedImage getImage() {
        return image;
    }

    protected void removeDefaultKeyHandler(){
        removeKeyListener(input);
        input = null;
    }
}
