package fosalgo;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.util.HashSet;
import javax.swing.JPanel;

public class Surface extends JPanel implements Runnable, KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

    //--------------------------------------------------------------------------
    //Loop Parameters
    private final static int MAX_FPS = 60;
    private final static int MAX_FRAME_SKIPS = 10;
    private final static int FRAME_PERIOD = 1000 / MAX_FPS;
    volatile boolean running = true;
    Thread thread;

    //KEYBOARD
    HashSet<Integer> keyboard = new HashSet<>();

    //TRANSLATE AND SCALE
    double translateX;
    double translateY;
    double scale;

    //MOUSE LISTENER
    private int lastOffsetX;
    private int lastOffsetY;

    //VARIABLES
    int cellSize = 40;
    int xo, yo;
    int vx = 3;
    int vy = 3;

    public Surface() {
        translateX = 0;
        translateY = 0;
        scale = WindowScale.get();
        setOpaque(false);
        setDoubleBuffered(true);
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        setFocusable(true);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        thread = new Thread(this);
        thread.start();
    }

    private void update() {
        //xo++;
        //yo++;
        //deteksi keyboard
        if (keyboard.contains(KeyEvent.VK_RIGHT)) {
            xo += vx;
        }
        if (keyboard.contains(KeyEvent.VK_LEFT)) {
            xo -= vx;
        }
        if (keyboard.contains(KeyEvent.VK_DOWN)) {
            yo += vy;
        }
        if (keyboard.contains(KeyEvent.VK_UP)) {
            yo -= vy;
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g.create();
        draw(g2d);
    }

    private void draw(Graphics2D g2d) {

        //SET TRANSFORM AND SCALE
        AffineTransform tx = new AffineTransform();
        tx.translate(translateX, translateY);
        tx.scale(scale, scale);
        g2d.setTransform(tx);

        //SET RENDERING HINTS
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHints(rh);

        //FILL BACKGROUND
        g2d.setColor(Color.decode("#2c3e50"));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        //GAMBAR GRID
        g2d.setColor(Color.decode("#e67e22"));
        int min_x = 0;
        int max_x = getWidth();
        int min_y = 0;
        int max_y = getHeight();

        int k = min_x;
        //draw line vertical
        while (k < max_x) {
            g2d.drawLine(k, min_y, k, max_y);
            k += cellSize;
        }

        k = min_y;
        //draw line horizontal
        while (k < max_y) {
            g2d.drawLine(min_x, k, max_x, k);
            k += cellSize;
        }

        g2d.setColor(Color.RED);
        g2d.drawOval(xo - 20, yo - 20, 40, 40);

        g2d.dispose();
    }

    private void delay(long sleepTime) {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        long beginTime;
        long timeDiff;
        int sleepTime = 0;
        int framesSkipped;

        //LOOP WHILE running = true; 
        while (running) {
            try {
                synchronized (this) {
                    beginTime = System.currentTimeMillis();
                    framesSkipped = 0;
                    update();
                    repaint();
                }
                timeDiff = System.currentTimeMillis() - beginTime;
                sleepTime = (int) (FRAME_PERIOD - timeDiff);
                if (sleepTime > 0) {
                    delay(sleepTime);
                }
                while (sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS) {
                    update();
                    sleepTime += FRAME_PERIOD;
                    framesSkipped++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //
    }

    @Override
    public void keyPressed(KeyEvent e) {
        Integer key = e.getKeyCode();
        keyboard.add(key);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        Integer key = e.getKeyCode();
        keyboard.remove(key);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        //
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // capture starting point
        lastOffsetX = e.getX();
        lastOffsetY = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        //
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        //
    }

    @Override
    public void mouseExited(MouseEvent e) {
        //
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // new x and y are defined by current mouse location subtracted
        // by previously processed mouse location
        int newX = e.getX() - lastOffsetX;
        int newY = e.getY() - lastOffsetY;

        // increment last offset to last processed by drag event.
        lastOffsetX += newX;
        lastOffsetY += newY;

        // update the canvas locations
        translateX += newX;
        translateY += newY;

        // schedule a repaint.
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        //
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
            scale += (.1 * e.getWheelRotation());
            scale = Math.max(0.00001, scale);
            repaint();
        }
    }

}
