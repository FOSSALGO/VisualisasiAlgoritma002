package fosalgo;

import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import static java.util.Arrays.asList;

public class WindowScale {
    
    public static double get() {
        java.awt.Window w = new java.awt.Window(new Frame());
        double s = getWindowScale(w);
        return s;
    }
    
    private static double getWindowScale(java.awt.Window window) {
        GraphicsDevice device = getWindowDevice(window);
        return device.getDisplayMode().getWidth() / (double) device.getDefaultConfiguration().getBounds().width;
    }
    
    private static GraphicsDevice getWindowDevice(java.awt.Window window) {
        Rectangle bounds = window.getBounds();
        return asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()).stream()
                // pick devices where window located
                .filter(d -> d.getDefaultConfiguration().getBounds().intersects(bounds))
                // sort by biggest intersection square
                .sorted((f, s) -> Long.compare(//
                square(f.getDefaultConfiguration().getBounds().intersection(bounds)),
                square(s.getDefaultConfiguration().getBounds().intersection(bounds))))
                // use one with the biggest part of the window
                .reduce((f, s) -> s) //

                // fallback to default device
                .orElse(window.getGraphicsConfiguration().getDevice());
    }
    
    private static long square(Rectangle rec) {
        return Math.abs(rec.width * rec.height);
    }
}
