public class Carta {
    private String cor; //Vermelho, Azul, Amarelo, Verde
    private String valor; //0-9, +2, +4, reverse, bloqueio, coringa
    
    //Cria uma carta nova com cor e valor
    public Carta (String cor, String valor) {
        this.cor = cor;
        this.valor = valor;
    }

    //Cor da carta
    public String getCor() {
        return cor;
    }
    
    //Valor da carta
    public String getValor() {
        return valor;
    }

    //Método toString como a carta será exibida quando imprimir
    @Override
    public String toString() {
        return cor + " " + valor;
    }
}
