import javax.swing.*;

// Classe principal que inicia o jogo
public class Main {
    public static void main(String[] args) {
        // Executa a criação da interface na thread de despacho de eventos do Swing
        SwingUtilities.invokeLater(() -> new Jogo());
    }
}