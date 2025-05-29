import java.util.ArrayList;
import java.util.Collections;

// Classe que representa o Baralho
public class Baralho {
    
    // Lista de cartas
    private final ArrayList<Carta> cartas;

    // Construtor: cria o baralho e já embaralha
    public Baralho() {
        cartas = new ArrayList<>();
        inicializarBaralho(); // Cria todas as cartas
        embaralhar();        // Embaralha o baralho
    }

    // Método que cria todas as cartas
    private void inicializarBaralho() {
        // Cores possíveis
        String[] cores = {"Vermelho", "Verde", "Azul", "Amarelo"};
        // Valores possíveis
        String[] valores = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "+2", "Reverse", "Bloqueio"};

        // Para cada cor e cada valor, cria as cartas
        for (String cor : cores) {
            for (String valor : valores) {
                cartas.add(new Carta(cor, valor)); // 1 carta
                if (!valor.equals("0")) {
                    cartas.add(new Carta(cor, valor)); // +1 carta (total 2)
                }
            }
        }

        // Adiciona os coringas e +4 (sem cor)
        for (int i = 0; i < 4; i++) {
            cartas.add(new Carta("Preto", "Coringa"));
            cartas.add(new Carta("Preto", "+4"));
        }
    }

    // Método que embaralha o baralho
    private void embaralhar() {
        Collections.shuffle(cartas);
    }

    // Método para comprar a carta do topo do baralho
    public Carta comprarCarta() {
        if (cartas.isEmpty()) {
        System.out.println("O baralho está vazio! Não há mais cartas para comprar.");
        return null;
        }
    return cartas.remove(0);
    }

    public boolean isVazio() {
        return cartas.isEmpty();
    }
}