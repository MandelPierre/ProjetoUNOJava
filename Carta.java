// Classe que representa uma Carta no jogo
public class Carta {
    
    private String cor;
    private String valor;
    private String imagem;

    // Construtor: cria uma nova carta com cor e valor
    public Carta(String cor, String valor) {
        this.cor = cor;
        this.valor = valor;
        this.imagem = "imagens/" + cor + "_" + valor + ".png";
    }

    // Getter para obter a cor da carta
    public String getCor() {
        return cor;
    }

    // Getter para obter o valor da carta
    public String getValor() {
        return valor;
    }

    // Retorna o caminho da imagem da carta
    public String getImagem() {
        return imagem;
    }

    // Método toString: como a carta será exibida quando imprimir
    @Override
    public String toString() {
        return cor + " " + valor;
    }

    public void setImagem(String novaImagem) {
        this.imagem = novaImagem;
    }
}