import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class Painel extends JPanel {

    private Image fundo;
    
    public Painel() {
        ImageIcon referencia = new ImageIcon("res\\Colorground.png");
        fundo = referencia.getImage(); 
    }

    public void paint(Graphics g) {
        Graphics2D graficos = (Graphics2D) g;
        graficos.drawImage(fundo, 0, 0, null);
        g.dispose(); 
    }
}
