package fosalgo;
import java.awt.EventQueue;
import javax.swing.JFrame;
public class Window extends JFrame {
    
    public Window(){
        initUI();
    }
    
    private void initUI(){
        setContentPane(new Surface());
        setTitle("JENDELA JIWA");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Window jendela = new Window();
                jendela.setVisible(true);
            }
        });
    }
}
