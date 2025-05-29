// Representa uma carta do jogo de Uno com cor, valor e caminho da imagem
public class Carta {
    private String cor;   // Cor da carta (Vermelho, Azul, Amarelo, Verde, Preto)
    private String valor; // Valor da carta (0-9, +2, +4, Reverse, Bloqueio, Coringa)
    private String imagem; // Caminho da imagem da carta (ex.: imagens/Vermelho_7.png)

    // Construtor: inicializa a carta com cor, valor e define o caminho da imagem
    public Carta(String cor, String valor) {
        this.cor = cor;
        this.valor = valor;
        // Define o caminho da imagem baseado na cor e valor (ex.: imagens/Vermelho_7.png)
        this.imagem = "imagens/" + cor + "_" + valor + ".png";
    }

    // Retorna a cor da carta
    public String getCor() {
        return cor;
    }

    // Retorna o valor da carta
    public String getValor() {
        return valor;
    }

    // Retorna o caminho da imagem da carta
    public String getImagem() {
        return imagem;
    }

    // Representação textual da carta para mensagens (ex.: "Vermelho 7")
    @Override
    public String toString() {
        return cor + " " + valor;
    }
}