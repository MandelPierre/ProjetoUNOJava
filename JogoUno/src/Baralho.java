import java.util.ArrayList;
import java.util.Collections;

public class Baralho {
    private ArrayList <Carta> cartas;

    //Construtor Cria e embaralha o baralho
    public Baralho() {
        cartas = new ArrayList<>();
        inicializarBaralho();
        embaralhar();
    }

    //Método que cria todas as cartas do baralho
    private void inicializarBaralho() {
        String[] cores = {"Vermelho", "Verde", "Amarelo", "Azul"};
        String[] valores = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "+2", "Reverso", "Bloqueio"};

        //Para cada cor, cria todas cartas possíveis
        for(String cor : cores) {
            for (String valor : valores) {
                cartas.add(new Carta(cor, valor));
                //No UNO, quase todas as cartas são duplicadas, menos o 0
                if (!valor.equals("0")) {
                    cartas.add(new Carta(cor, valor));
                }
            }
        }

        //Coringas e +4 (sem cor): 4 "Coringa" e 4 "+4"
        for (int i = 0; i < 4; i++) {
            cartas.add(new Carta("Preto", "Coringa"));
            cartas.add(new Carta("Preto", "+4"));
        }
    }

    //Método para embaralhar as cartas
    public void embaralhar() {
        Collections.shuffle(cartas);
    }

    //Método para comprar uma carta do baralho
    public Carta comprarCarta(){
        if(!cartas.isEmpty()) {
            return cartas.remove(0);
        }
        return null; //Se não tiver cartas
    }
}
