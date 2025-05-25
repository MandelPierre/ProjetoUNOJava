import java.util.ArrayList;

public class Jogador {
    private String nome;
    private ArrayList<Carta> mao;

    //Construtor cria o jogador com nome e mão vazia
    public Jogador(String nome) {
        this.nome = nome;
        this.mao = new ArrayList<>();
    }
    
    //Método para comprar uma carta
    public void comprarCarta(Baralho baralho) {
        Carta carta = baralho.comprarCarta();
        if (carta != null) {
            mao.add(carta);
            System.out.println(nome + " comprou: " + carta);
        } else {
            System.out.println("O baralho esta vazio");
        }
    }

    //Método para jogar uma carta da mão
    public Carta jogarCarta(int indice) {
        if(indice >= 0 && indice < mao.size()) {
            return mao.remove(indice);
        }
        return null;
    }
    
    //Mostrar a mão do jogador
    
    // Verifica se o jogador ficou sem cartas (venceu)
    
    // Getter do nome

    // Getter da mão (para lógicas futuras, se quiser)
}
