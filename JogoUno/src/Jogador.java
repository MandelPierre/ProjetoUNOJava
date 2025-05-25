import java.util.ArrayList;

public class Jogador {
    private String nome;
    private ArrayList<Carta> mao;

    //Construtor cria o jogador com nome e mão vazia
    public Jogador(String nome) {
        this.nome = nome;
        this.mao = new ArrayList<>();
    }
    
}
