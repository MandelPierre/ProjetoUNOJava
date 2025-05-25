public class Carta {
    private String cor; //Vermelho, Azul, Amarelo, Verde
    private String valor; //0-9, +2, +4, reverse, bloqueio, coringa
    
    public Carta (String cor, String valor) {
        this.cor = cor;
        this.valor = valor;
    }

    public String getCor() {
        return cor;
    }
    
    public String getValor() {
        return valor;
    }

    public String toString() {
        return cor + " " + valor;
    }
}
