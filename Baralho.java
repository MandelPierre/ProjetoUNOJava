import java.util.ArrayList;
import java.util.Collections;

public class Baralho {
    private ArrayList<Carta> cartas;

    public Baralho() {
        cartas = new ArrayList<>(); // Garante que a lista seja inicializada
        criarBaralho();
        embaralhar();
    }

    // Cria o baralho com todas as cartas
    private void criarBaralho() {
        String[] cores = {"Vermelho", "Verde", "Azul", "Amarelo"};
        String[] valoresNumericos = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        String[] valoresEspeciais = {"+2", "Reverse", "Bloqueio"};

        // Cartas numéricas (0-9)
        for (String cor : cores) {
            Carta zero = new Carta(cor, "0");
            cartas.add(zero);
            for (int i = 1; i < valoresNumericos.length; i++) {
                Carta carta1 = new Carta(cor, valoresNumericos[i]);
                Carta carta2 = new Carta(cor, valoresNumericos[i]);
                cartas.add(carta1);
                cartas.add(carta2);
            }
            for (String valor : valoresEspeciais) {
                Carta especial1 = new Carta(cor, valor);
                Carta especial2 = new Carta(cor, valor);
                cartas.add(especial1);
                cartas.add(especial2);
            }
        }

        // Cartas especiais pretas (Coringa e +4) - 4 de cada
        for (int i = 0; i < 4; i++) {
            Carta coringa = new Carta("Preto", "Coringa");
            Carta maisQuatro = new Carta("Preto", "+4");
            cartas.add(coringa);
            cartas.add(maisQuatro);
        }

        // Adiciona 4 cartas FazoL
        for (int i = 0; i < 4; i++) {
            Carta fazoL = new Carta("Preto", "FazoL");
            fazoL.setImagem("imagens/FazoL.png"); // Define a imagem
            cartas.add(fazoL);
        }
    }

    // Embaralha o baralho
    public void embaralhar() {
        Collections.shuffle(cartas);
    }

    // Compra uma carta do topo do baralho
    public Carta comprarCarta() {
        if (cartas.isEmpty()) {
            return null;
        }
        return cartas.remove(0);
    }

    // Adiciona uma carta de volta ao baralho
    public void adicionarCarta(Carta carta) {
        if (carta != null) {
            cartas.add(carta); // Adiciona a carta de volta
            embaralhar(); // Embaralha para manter aleatoriedade
        }
    }

    // Retorna o número de cartas no baralho
    public int tamanho() {
        return cartas.size();
    }
}