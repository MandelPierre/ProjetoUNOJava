import java.util.ArrayList;
import java.util.Collections;

// Representa o baralho de cartas do jogo de Uno
public class Baralho {
    // Lista que armazena todas as cartas do baralho
    private final ArrayList<Carta> cartas;

    // Construtor: inicializa o baralho, cria as cartas e embaralha
    public Baralho() {
        cartas = new ArrayList<>();
        inicializarBaralho(); // Cria todas as cartas
        embaralhar();        // Embaralha as cartas
    }

    // Cria as cartas do baralho seguindo as regras do Uno
    private void inicializarBaralho() {
        // Define as cores normais (exclui Preto, que é para cartas especiais)
        String[] cores = {"Vermelho", "Verde", "Azul", "Amarelo"};
        // Define os valores das cartas normais e especiais
        String[] valores = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "+2", "Reverse", "Bloqueio"};

        // Para cada cor e valor, cria as cartas
        for (String cor : cores) {
            for (String valor : valores) {
                cartas.add(new Carta(cor, valor)); // Adiciona uma carta
                if (!valor.equals("0")) { // Exceto o 0, todas têm duplicata
                    cartas.add(new Carta(cor, valor));
                }
            }
        }

        // Adiciona 4 Coringas e 4 +4 (cartas especiais de cor Preto)
        for (int i = 0; i < 4; i++) {
            cartas.add(new Carta("Preto", "Coringa"));
            cartas.add(new Carta("Preto", "+4"));
        }
    }

    // Embaralha as cartas usando a função shuffle do Collections
    private void embaralhar() {
        Collections.shuffle(cartas);
    }

    // Remove e retorna a carta do topo do baralho, ou null se vazio
    public Carta comprarCarta() {
        if (cartas.isEmpty()) {
            return null; // Retorna null se não há cartas
        }
        return cartas.remove(0); // Remove a primeira carta
    }

    // Verifica se o baralho está vazio
    public boolean isVazio() {
        return cartas.isEmpty();
    }
}