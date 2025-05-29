import java.util.ArrayList;

// Representa um jogador (humano ou bot) no jogo de Uno
public class Jogador {
    // Nome do jogador (ex.: "Você" ou "Bot")
    private String nome;
    // Lista de cartas na mão do jogador
    private ArrayList<Carta> mao;

    // Construtor: inicializa o jogador com um nome e uma mão vazia
    public Jogador(String nome) {
        this.nome = nome;
        mao = new ArrayList<>();
    }

    // Adiciona uma carta à mão do jogador
    public void adicionarCarta(Carta carta) {
        mao.add(carta);
    }

    // Remove uma carta da mão do jogador
    public void removerCarta(Carta carta) {
        mao.remove(carta);
    }

    // Retorna a lista de cartas na mão
    public ArrayList<Carta> getMao() {
        return mao;
    }

    // Retorna o nome do jogador
    public String getNome() {
        return nome;
    }

    // Verifica se o jogador venceu (mão vazia)
    public boolean venceu() {
        return mao.isEmpty();
    }
}