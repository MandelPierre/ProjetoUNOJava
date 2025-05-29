// Classe que representa uma Carta no jogo
public class Carta {
    
    // Atributos: cor e valor da carta
    private String cor;
    private String valor;

    // Construtor: cria uma nova carta com cor e valor
    public Carta(String cor, String valor) {
        this.cor = cor;
        this.valor = valor;
    }

    // Getter para obter a cor da carta
    public String getCor() {
        return cor;
    }

    // Getter para obter o valor da carta
    public String getValor() {
        return valor;
    }

    // Método toString sobrescrito: como a carta será exibida quando imprimir
    @Override
    public String toString() {
        return cor + " " + valor;
    }
}