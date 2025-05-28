import java.util.Scanner;

// Classe principal do jogo
public class Jogo {
    private Baralho baralho;
    private Jogador jogador;
    private Jogador bot;
    private Carta cartaAtual;
    private Scanner scanner;

    // Construtor
    public Jogo() {
        baralho = new Baralho();
        jogador = new Jogador("Você");
        bot = new Jogador("Bot");
        scanner = new Scanner(System.in);

        // Cada jogador compra 7 cartas no começo
        for (int i = 0; i < 7; i++) {
            jogador.adicionarCarta(baralho.comprarCarta());
            bot.adicionarCarta(baralho.comprarCarta());
        }

        // Define a primeira carta da mesa
        cartaAtual = baralho.comprarCarta();
    }

    //Mostra a carta atual e a mão. Deixa escolher jogar carta ou comprar
    public void iniciar() {
        System.out.println("Bem-vindo ao UNO!");

    while (true) {
        System.out.println("\nCarta atual: " + cartaAtual);
        System.out.println("Sua mão:");
        for (int i = 0; i < jogador.getMao().size(); i++) {
            System.out.println((i + 1) + " - " + jogador.getMao().get(i));
        }

        System.out.println("Escolha uma carta para jogar ou 0 para comprar:");
        int escolha = scanner.nextInt();

        if (escolha == 0) {
            Carta nova = baralho.comprarCarta();
            jogador.adicionarCarta(nova);
            System.out.println("Você comprou: " + nova);
        } else if (escolha > 0 && escolha <= jogador.getMao().size()) {
            Carta cartaEscolhida = jogador.getMao().get(escolha - 1);
            if (podeJogar(cartaEscolhida)) {
                jogarCarta(jogador, cartaEscolhida);
                if (jogador.venceu()) {
                    System.out.println("Você venceu!"); //Verifica vitória
                    break;
                }
                    turnoBot();
                    if (bot.venceu()) {
                        System.out.println("O bot venceu!");
                        break;
                    }
                } else {
                    System.out.println("Jogada inválida! Tente novamente.");
                }
            } else {
                System.out.println("Escolha inválida!");
            }
        }
    }

    //Método para saber se pode jogar
    private boolean podeJogar(Carta carta) {
    return carta.getCor().equals(cartaAtual.getCor()) ||
            carta.getValor().equals(cartaAtual.getValor()) ||
            carta.getCor().equals("Preto"); // Coringa sempre pode
    }

    //Remove da mão e atualiza a carta da mesa. Aplica efeito (se tiver).
    private void jogarCarta(Jogador jogadorAtual, Carta carta) {
        System.out.println(jogadorAtual.getNome() + " jogou: " + carta);
        jogadorAtual.removerCarta(carta);
        cartaAtual = carta;
        aplicarEfeito(carta, jogadorAtual);
    }

    //Aplica efeitos de cartas especiais. Como +4, reverse, etc...
    private void aplicarEfeito(Carta carta, Jogador jogadorAtual) {
        if (carta.getValor().equals("+2")) {
            Jogador adversario = (jogadorAtual == jogador) ? bot : jogador;
            for (int i = 0; i < 2; i++) {
                adversario.adicionarCarta(baralho.comprarCarta());
            }
            System.out.println(adversario.getNome() + " comprou 2 cartas!");
        } else if (carta.getValor().equals("+4")) {
            Jogador adversario = (jogadorAtual == jogador) ? bot : jogador;
            for (int i = 0; i < 4; i++) {
                adversario.adicionarCarta(baralho.comprarCarta());
            }
            System.out.println(adversario.getNome() + " comprou 4 cartas!");
            escolherCor(jogadorAtual);
        } else if (carta.getValor().equals("Coringa")) {
            escolherCor(jogadorAtual);
        } else if (carta.getValor().equals("Reverse")) {
            System.out.println("Reverse jogado, mas como são só dois jogadores, é como pular a vez.");
        } else if (carta.getValor().equals("Bloqueio")) {
            System.out.println("Bloqueio jogado, o próximo perde a vez.");
            if (jogadorAtual == jogador) {
                System.out.println("O bot perdeu a vez!");
            } else {
                System.out.println("Você perdeu a vez!");
            }
        }
    }

    //Jogador escolhe a cor quando jogado coringa ou +4 e o bot escolhe aleatoriamente.
    private void escolherCor(Jogador jogadorAtual) {
        if (jogadorAtual == jogador) {
            System.out.println("Escolha a nova cor: 1-Vermelho, 2-Verde, 3-Azul, 4-Amarelo");
            int corEscolha = scanner.nextInt();
            switch (corEscolha) {
                case 1: cartaAtual = new Carta("Vermelho", cartaAtual.getValor()); break;
                case 2: cartaAtual = new Carta("Verde", cartaAtual.getValor()); break;
                case 3: cartaAtual = new Carta("Azul", cartaAtual.getValor()); break;
                case 4: cartaAtual = new Carta("Amarelo", cartaAtual.getValor()); break;
                default: System.out.println("Escolha inválida, cor padrão Vermelho!");
                         cartaAtual = new Carta("Vermelho", cartaAtual.getValor());
            }
        } else {
            String[] cores = {"Vermelho", "Verde", "Azul", "Amarelo"};
            String corEscolhida = cores[(int)(Math.random() * 4)];
            System.out.println("Bot escolheu a cor: " + corEscolhida);
            cartaAtual = new Carta(corEscolhida, cartaAtual.getValor());
        }
    }

    //Bot joga se puder, senão compra.
    private void turnoBot() {
        System.out.println("\nTurno do bot...");
        for (Carta c : bot.getMao()) {
            if (podeJogar(c)) {
                jogarCarta(bot, c);
                return;
            }
        }
        Carta nova = baralho.comprarCarta();
        bot.adicionarCarta(nova);
        System.out.println("Bot comprou uma carta.");
    }
}