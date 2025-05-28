/*
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class Painel extends JPanel {

    private Image fundo; //Imagem de fundo o Background
    
    public Painel() {
        ImageIcon referencia = new ImageIcon("res\\Colorground.png");
        fundo = referencia.getImage(); 
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graficos = (Graphics2D) g;
        graficos.drawImage(fundo, 0, 0, null);

    }
}
*/