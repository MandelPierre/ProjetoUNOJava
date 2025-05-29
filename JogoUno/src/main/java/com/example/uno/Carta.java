
public class Carta {
    private String cor; //Vermelho, Azul, Amarelo, Verde
    private String valor; //0-9, +2, +4, reverse, bloqueio, coringa
    //private String imagem; //Imagem das cartas

    //Cria uma carta nova com cor e valor
    public Carta (String cor, String valor) {
        this.cor = cor;
        this.valor = valor;
        //this.imagem = "imagens/" + cor + "_" + valor + ".png";
    }

    //Cor da carta
    public String getCor() {
        return cor;
    }
    
    //Valor da carta
    public String getValor() {
        return valor;
    }

    //Imagem da carta
    //public String getImagem(){
        //return imagem;
    //}

    //Método toString como a carta será exibida quando imprimir
    @Override
    public String toString() {
        return cor + " " + valor;
    }
}
