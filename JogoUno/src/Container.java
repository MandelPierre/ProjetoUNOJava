import javax.swing.JFrame;

public class Container extends JFrame {
    public Container() {
        add(new Fase());
        setTitle("UNO");
        setSize(1024,728);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }
    
    public static void main(String[] args) {
        new Container();
    }
}
