import java.util.Scanner;

public class Jogo {
    private Baralho baralho;
    private Jogador jogador;
    private Jogador bot;
    private Carta cartaAtual;
    private Scanner scanner;
    private boolean jogadorDisseUno;
    private boolean botDisseUno;
    private boolean bloquearProximo;

    public Jogo() {
        baralho = new Baralho();
        jogador = new Jogador("Você");
        bot = new Jogador("Bot");
        scanner = new Scanner(System.in);
        jogadorDisseUno = false;
        botDisseUno = false;
        bloquearProximo = false;

        for (int i = 0; i < 7; i++) {
            Carta cartaJogador = baralho.comprarCarta();
            Carta cartaBot = baralho.comprarCarta();
            if (cartaJogador != null && cartaBot != null) {
                jogador.adicionarCarta(cartaJogador);
                bot.adicionarCarta(cartaBot);
            } else {
                System.out.println("🚫 Erro: Baralho insuficiente para iniciar o jogo!");
                System.exit(1);
            }
        }

        cartaAtual = baralho.comprarCarta();
        while (cartaAtual != null && (cartaAtual.getValor().equals("Coringa") || 
                cartaAtual.getValor().equals("+4") || 
                cartaAtual.getValor().equals("+2") || 
                cartaAtual.getValor().equals("Reverse") || 
                cartaAtual.getValor().equals("Bloqueio"))) {
            cartaAtual = baralho.comprarCarta();
        }
        if (cartaAtual == null) {
            System.out.println("🚫 Erro: Baralho vazio ao iniciar a mesa!");
            System.exit(1);
        }
    }

    public void iniciar() {
        System.out.println("=================================");
        System.out.println("        Bem-vindo ao UNO!        ");
        System.out.println("=================================");

        while (true) {
            if (!bloquearProximo) {
                exibirMesa();
                exibirMaoJogador();
                exibirOpcoes();

                try {
                    int escolha = scanner.nextInt();
                    if (escolha == 0) {
                        comprarCartaJogador();
                    } else if (escolha == -1) {
                        gritarUno();
                    } else if (escolha > 0 && escolha <= jogador.getMao().size()) {
                        Carta cartaEscolhida = jogador.getMao().get(escolha - 1);
                        if (podeJogar(cartaEscolhida)) {
                            jogarCarta(jogador, cartaEscolhida);
                            if (jogador.venceu()) {
                                System.out.println("=================================");
                                System.out.println("🎉 Você venceu! Parabéns! 🎉");
                                System.out.println("=================================");
                                break;
                            }
                            if (jogador.getMao().size() == 1 && !jogadorDisseUno) {
                                System.out.println("⚠️ Você tem uma carta! Diga 'UNO' (digite -1) ou será penalizado!");
                            }
                        } else {
                            System.out.println("🚫 Jogada inválida! A carta deve combinar com a cor ou valor da mesa.");
                        }
                    } else {
                        System.out.println("🚫 Escolha inválida! Tente novamente.");
                    }
                } catch (java.util.InputMismatchException e) {
                    System.out.println("🚫 Entrada inválida! Digite um número.");
                    scanner.nextLine();
                }
            } else {
                System.out.println("🚫 Você foi bloqueado e perdeu a vez!");
                bloquearProximo = false;
            }

            if (bot.venceu()) {
                System.out.println("=================================");
                System.out.println("😢 O bot venceu! Melhor sorte na próxima!");
                System.out.println("=================================");
                break;
            }

            if (!bloquearProximo) {
                turnoBot();
                if (bot.getMao().size() == 1 && !botDisseUno) {
                    botDisseUno = true;
                    System.out.println("🤖 Bot gritou: UNO!");
                }
            } else {
                System.out.println("🚫 Bot foi bloqueado e perdeu a vez!");
                bloquearProximo = false;
            }

            if (jogador.getMao().size() == 1 && !jogadorDisseUno && !bloquearProximo) {
                System.out.println("⚠️ Você não disse UNO! Penalidade: compre 2 cartas!");
                for (int i = 0; i < 2; i++) {
                    comprarCartaJogador();
                }
            }
        }
        scanner.close();
    }

    private void exibirMesa() {
        System.out.println("\n=================================");
        System.out.println("          MESA DO UNO           ");
        System.out.println("=================================");
        System.out.println("📜 Baralho: [" + (baralho.isVazio() ? "Vazio" : "🃏") + "]");
        System.out.println("🃏 Carta na mesa: [" + formatarCarta(cartaAtual) + "]");
        System.out.println("=================================");
    }

    private void exibirMaoJogador() {
        System.out.println("\nSua mão (" + jogador.getMao().size() + " cartas):");
        for (int i = 0; i < jogador.getMao().size(); i++) {
            System.out.println("[" + (i + 1) + "] " + formatarCarta(jogador.getMao().get(i)));
        }
    }

    private void exibirOpcoes() {
        System.out.println("\nOpções:");
        System.out.println("[0] Comprar carta");
        if (jogador.getMao().size() == 1 && !jogadorDisseUno) {
            System.out.println("[-1] Gritar UNO!");
        }
        System.out.println("[1-" + jogador.getMao().size() + "] Jogar carta");
        System.out.print("Digite sua escolha: ");
    }

    private String formatarCarta(Carta carta) {
        String cor = carta.getCor();
        String valor = carta.getValor();
        String simboloCor;
        switch (cor) {
            case "Vermelho": simboloCor = "❤️"; break;
            case "Verde": simboloCor = "💚"; break;
            case "Azul": simboloCor = "💙"; break;
            case "Amarelo": simboloCor = "💛"; break;
            case "Preto": simboloCor = "🖤"; break;
            default: simboloCor = "❓";
        }
        return simboloCor + " " + cor + " " + valor;
    }

    private void comprarCartaJogador() {
        Carta nova = baralho.comprarCarta();
        if (nova != null) {
            jogador.adicionarCarta(nova);
            System.out.println("Você comprou: " + formatarCarta(nova));
        } else {
            System.out.println("🚫 Baralho vazio! Não há mais cartas para comprar.");
            System.out.println("=================================");
            System.out.println("🚫 Jogo encerrado: baralho vazio!");
            System.exit(0);
        }
    }

    private void gritarUno() {
        if (jogador.getMao().size() == 1) {
            jogadorDisseUno = true;
            System.out.println("🗣️ Você gritou: UNO!");
        } else {
            System.out.println("🚫 Você só pode gritar UNO com uma carta na mão!");
        }
    }

    private boolean podeJogar(Carta carta) {
        return carta.getCor().equals(cartaAtual.getCor()) ||
               carta.getValor().equals(cartaAtual.getValor()) ||
               carta.getCor().equals("Preto");
    }

    private void jogarCarta(Jogador jogadorAtual, Carta carta) {
        System.out.println(jogadorAtual.getNome() + " jogou: " + formatarCarta(carta));
        jogadorAtual.removerCarta(carta);
        cartaAtual = carta;
        aplicarEfeito(carta, jogadorAtual);
    }

    private void aplicarEfeito(Carta carta, Jogador jogadorAtual) {
        Jogador adversario = (jogadorAtual == jogador) ? bot : jogador;
        if (carta.getValor().equals("+2")) {
            for (int i = 0; i < 2; i++) {
                Carta nova = baralho.comprarCarta();
                if (nova != null) {
                    adversario.adicionarCarta(nova);
                    System.out.println(adversario.getNome() + " comprou: " + formatarCarta(nova));
                }
            }
            System.out.println(adversario.getNome() + " comprou 2 cartas!");
        } else if (carta.getValor().equals("+4")) {
            for (int i = 0; i < 4; i++) {
                Carta nova = baralho.comprarCarta();
                if (nova != null) {
                    adversario.adicionarCarta(nova);
                    System.out.println(adversario.getNome() + " comprou: " + formatarCarta(nova));
                }
            }
            System.out.println(adversario.getNome() + " comprou 4 cartas!");
            escolherCor(jogadorAtual);
        } else if (carta.getValor().equals("Coringa")) {
            escolherCor(jogadorAtual);
        } else if (carta.getValor().equals("Reverse")) {
            System.out.println("🔄 Reverse jogado! Como são dois jogadores, é como pular a vez.");
        } else if (carta.getValor().equals("Bloqueio")) {
            System.out.println("⛔ Bloqueio jogado! O próximo jogador perde a vez.");
            bloquearProximo = true;
        }
    }

    private void escolherCor(Jogador jogadorAtual) {
        if (jogadorAtual == jogador) {
            System.out.println("Escolha a nova cor:");
            System.out.println("[1] ❤️ Vermelho");
            System.out.println("[2] 💚 Verde");
            System.out.println("[3] 💙 Azul");
            System.out.println("[4] 💛 Amarelo");
            System.out.print("Digite sua escolha: ");
            try {
                int corEscolha = scanner.nextInt();
                switch (corEscolha) {
                    case 1: cartaAtual = new Carta("Vermelho", cartaAtual.getValor()); break;
                    case 2: cartaAtual = new Carta("Verde", cartaAtual.getValor()); break;
                    case 3: cartaAtual = new Carta("Azul", cartaAtual.getValor()); break;
                    case 4: cartaAtual = new Carta("Amarelo", cartaAtual.getValor()); break;
                    default:
                        System.out.println("🚫 Escolha inválida! Cor padrão: Vermelho");
                        cartaAtual = new Carta("Vermelho", cartaAtual.getValor());
                }
                System.out.println("Nova cor: " + formatarCarta(cartaAtual));
            } catch (java.util.InputMismatchException e) {
                System.out.println("🚫 Entrada inválida! Cor padrão: Vermelho");
                cartaAtual = new Carta("Vermelho", cartaAtual.getValor());
                scanner.nextLine();
            }
        } else {
            String[] cores = {"Vermelho", "Verde", "Azul", "Amarelo"};
            String corEscolhida = cores[(int)(Math.random() * 4)];
            System.out.println("🤖 Bot escolheu a cor: " + corEscolhida);
            cartaAtual = new Carta(corEscolhida, cartaAtual.getValor());
        }
    }

    private void turnoBot() {
        System.out.println("\n=================================");
        System.out.println("          Turno do Bot          ");
        System.out.println("=================================");
        for (Carta c : bot.getMao()) {
            if (podeJogar(c)) {
                jogarCarta(bot, c);
                return;
            }
        }
        Carta nova = baralho.comprarCarta();
        if (nova != null) {
            bot.adicionarCarta(nova);
            System.out.println("🤖 Bot comprou uma carta.");
        } else {
            System.out.println("🤖 Baralho vazio! Bot não comprou.");
            System.out.println("=================================");
            System.out.println("🚫 Jogo encerrado: baralho vazio!");
            System.exit(0);
        }
    }
}