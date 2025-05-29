
import java.util.ArrayList;

// Classe que representa um Jogador
public class Jogador {
    
    // Atributos: nome do jogador e a mão de cartas
    private String nome;
    private ArrayList<Carta> mao;

    // Construtor
    public Jogador(String nome) {
        this.nome = nome;
        mao = new ArrayList<>();
    }

    // Método para adicionar carta à mão
    public void adicionarCarta(Carta carta) {
        mao.add(carta);
    }

    // Método para remover carta da mão
    public void removerCarta(Carta carta) {
        mao.remove(carta);
    }

    // Getter: retorna todas as cartas da mão
    public ArrayList<Carta> getMao() {
        return mao;
    }

    // Getter: retorna o nome do jogador
    public String getNome() {
        return nome;
    }

    // Método para verificar se o jogador venceu
    public boolean venceu() {
        return mao.isEmpty();
    }
}