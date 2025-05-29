import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

// Classe principal que gerencia o jogo de Uno com interface gráfica em Swing
public class Jogo {
    // Atributos do jogo
    private Baralho baralho;         // Baralho de cartas
    private Jogador jogador;         // Jogador humano
    private Jogador bot;             // Jogador bot
    private Carta cartaAtual;        // Carta atual na mesa
    private boolean jogadorDisseUno; // Indica se o jogador gritou "UNO!"
    private boolean botDisseUno;     // Indica se o bot gritou "UNO!"
    private boolean bloquearProximo; // Indica se o próximo turno será bloqueado
    private JFrame frame;            // Janela principal
    private JPanel maoJogadorPanel;  // Painel para a mão do jogador
    private JPanel maoBotPanel;      // Painel para a mão do bot
    private JLabel mensagemLabel;    // Rótulo para mensagens do jogo
    private JPanel mesaPanel;        // Painel para a mesa (carta atual)
    private JButton comprarButton;   // Botão para comprar carta
    private JButton unoButton;       // Botão para gritar "UNO!"

    // Construtor: inicializa o jogo e a interface gráfica
    public Jogo() {
        inicializarJogo(); // Configura o jogo
        criarInterface();  // Cria a interface gráfica
    }

    // Inicializa o estado inicial do jogo
    private void inicializarJogo() {
        baralho = new Baralho(); // Cria um novo baralho
        jogador = new Jogador("Você"); // Cria o jogador humano
        bot = new Jogador("Bot");      // Cria o bot
        jogadorDisseUno = false;       // Inicializa flag de UNO do jogador
        botDisseUno = false;           // Inicializa flag de UNO do bot
        bloquearProximo = false;       // Inicializa flag de bloqueio

        // Distribui 7 cartas para cada jogador
        for (int i = 0; i < 7; i++) {
            Carta cartaJogador = baralho.comprarCarta();
            Carta cartaBot = baralho.comprarCarta();
            if (cartaJogador != null && cartaBot != null) {
                jogador.adicionarCarta(cartaJogador);
                bot.adicionarCarta(cartaBot);
            } else {
                // Exibe erro se o baralho não tiver cartas suficientes
                JOptionPane.showMessageDialog(null, "Erro: Baralho insuficiente para iniciar o jogo!");
                System.exit(1);
            }
        }

        // Escolhe a primeira carta da mesa, garantindo que não seja especial
        cartaAtual = baralho.comprarCarta();
        while (cartaAtual != null && (cartaAtual.getValor().equals("Coringa") ||
                cartaAtual.getValor().equals("+4") ||
                cartaAtual.getValor().equals("+2") ||
                cartaAtual.getValor().equals("Reverse") ||
                cartaAtual.getValor().equals("Bloqueio"))) {
            cartaAtual = baralho.comprarCarta();
        }
        if (cartaAtual == null) {
            // Exibe erro se não houver cartas válidas para iniciar
            JOptionPane.showMessageDialog(null, "Erro: Baralho vazio ao iniciar a mesa!");
            System.exit(1);
        }
    }

    // Cria a interface gráfica com Swing
    private void criarInterface() {
        // Cria a janela principal
        frame = new JFrame("Jogo de UNO");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600); // Tamanho da janela: 800x600 pixels
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(new Color(0, 100, 0)); // Fundo verde (mesa de jogo)

        // Painel da mesa (centro): exibe a carta atual
        mesaPanel = new JPanel();
        mesaPanel.setOpaque(false); // Fundo transparente
        mesaPanel.setLayout(new BoxLayout(mesaPanel, BoxLayout.Y_AXIS)); // Alinhamento vertical
        atualizarMesa(); // Atualiza a carta na mesa

        // Botão de compra: usa a imagem verso.png
        comprarButton = new JButton(new ImageIcon("imagens/verso.png"));
        comprarButton.setPreferredSize(new Dimension(80, 120)); // Tamanho da carta
        comprarButton.addActionListener(e -> comprarCartaJogador()); // Ação ao clicar

        // Botão UNO: aparece quando o jogador tem uma carta
        unoButton = new JButton("Gritar UNO!");
        unoButton.setFont(new Font("Arial", Font.BOLD, 16));
        unoButton.setBackground(new Color(255, 69, 0)); // Fundo laranja
        unoButton.setForeground(Color.WHITE);
        unoButton.setVisible(false); // Invisível até ser necessário
        unoButton.addActionListener(e -> gritarUno()); // Ação ao clicar

        // Painel para os botões de controle (comprar e UNO)
        JPanel controlesPanel = new JPanel();
        controlesPanel.setOpaque(false);
        controlesPanel.add(comprarButton);
        controlesPanel.add(unoButton);
        mesaPanel.add(controlesPanel);

        // Painel da mão do jogador (inferior)
        maoJogadorPanel = new JPanel(); // Inicializa o painel corretamente
        maoJogadorPanel.setOpaque(false); // Fundo transparente
        maoJogadorPanel.setLayout(new FlowLayout()); // Layout para alinhar cartas horizontalmente
        atualizarMaoJogador(); // Atualiza as cartas do jogador

        // Painel da mão do bot (superior)
        maoBotPanel = new JPanel();
        maoBotPanel.setOpaque(false); // Fundo transparente
        maoBotPanel.setLayout(new FlowLayout()); // Layout para alinhar cartas
        atualizarMaoBot(); // Atualiza as cartas do bot

        // Rótulo para mensagens do jogo
        mensagemLabel = new JLabel("Bem-vindo ao UNO!");
        mensagemLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mensagemLabel.setForeground(Color.WHITE);
        mensagemLabel.setBackground(new Color(0, 0, 0, 100)); // Fundo semi-transparente
        mensagemLabel.setOpaque(true);
        mensagemLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Adiciona os painéis à janela
        frame.add(maoBotPanel, BorderLayout.NORTH); // Mão do bot no topo
        frame.add(mesaPanel, BorderLayout.CENTER); // Mesa no centro
        frame.add(maoJogadorPanel, BorderLayout.SOUTH); // Mão do jogador na base
        frame.add(mensagemLabel, BorderLayout.WEST); // Mensagens à esquerda

        // Exibe a janela
        frame.setVisible(true);
        atualizarInterface(); // Atualiza a interface inicial
        verificarUnoJogador(); // Verifica se o jogador precisa gritar UNO
        verificarTurnoBot(); // Executa o turno inicial do bot, se necessário
    }

    // Atualiza toda a interface gráfica
    private void atualizarInterface() {
        atualizarMesa(); // Atualiza a carta na mesa
        atualizarMaoJogador(); // Atualiza a mão do jogador
        atualizarMaoBot(); // Atualiza a mão do bot
        comprarButton.setEnabled(!baralho.isVazio() && !bloquearProximo); // Habilita/desabilita botão de compra
        unoButton.setVisible(jogador.getMao().size() == 1 && !jogadorDisseUno); // Mostra botão UNO se necessário
    }

    // Atualiza o painel da mesa com a carta atual
    private void atualizarMesa() {
        mesaPanel.removeAll(); // Remove todos os componentes do painel
        JLabel cartaMesa = new JLabel(new ImageIcon(cartaAtual.getImagem())); // Cria a imagem da carta atual
        cartaMesa.setAlignmentX(Component.CENTER_ALIGNMENT); // Centraliza a carta
        mesaPanel.add(Box.createVerticalGlue()); // Espaço acima
        mesaPanel.add(cartaMesa); // Adiciona a carta
        mesaPanel.add(Box.createVerticalStrut(10)); // Espaço entre carta e botões
        JPanel controlesPanel = new JPanel(); // Painel para os botões
        controlesPanel.setOpaque(false);
        controlesPanel.add(comprarButton);
        controlesPanel.add(unoButton);
        mesaPanel.add(controlesPanel); // Adiciona os botões
        mesaPanel.add(Box.createVerticalGlue()); // Espaço abaixo
        mesaPanel.revalidate(); // Atualiza o layout
        mesaPanel.repaint(); // Redesenha o painel
    }

    // Atualiza o painel da mão do jogador com cartas clicáveis
    private void atualizarMaoJogador() {
        maoJogadorPanel.removeAll(); // Remove todas as cartas
        for (Carta carta : jogador.getMao()) {
            JLabel cartaLabel = new JLabel(new ImageIcon(carta.getImagem())); // Cria imagem da carta
            cartaLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    jogarCartaJogador(carta); // Joga a carta ao clicar
                }
            });
            maoJogadorPanel.add(cartaLabel); // Adiciona a carta ao painel
        }
        maoJogadorPanel.revalidate();
        maoJogadorPanel.repaint();
    }

    // Atualiza o painel da mão do bot com imagens de verso
    private void atualizarMaoBot() {
        maoBotPanel.removeAll(); // Remove todas as cartas
        for (int i = 0; i < bot.getMao().size(); i++) {
            JLabel cartaVerso = new JLabel(new ImageIcon("imagens/verso.png")); // Imagem de verso
            maoBotPanel.add(cartaVerso); // Adiciona ao painel
        }
        maoBotPanel.revalidate();
        maoBotPanel.repaint();
    }

    // Ação de comprar uma carta para o jogador
    private void comprarCartaJogador() {
        if (bloquearProximo) {
            exibirMensagem("🚫 Você foi bloqueado e não pode comprar!");
            return;
        }
        Carta nova = baralho.comprarCarta(); // Tenta comprar uma carta
        if (nova != null) {
            jogador.adicionarCarta(nova); // Adiciona à mão
            exibirMensagem("Você comprou: " + formatarCarta(nova));
            atualizarInterface();
            verificarUnoJogador(); // Verifica se precisa gritar UNO
            verificarTurnoBot(); // Executa o turno do bot
        } else {
            exibirMensagem("🚫 Baralho vazio! Jogo encerrado!");
            frame.dispose(); // Fecha a janela
        }
    }

    // Ação de gritar "UNO!"
    private void gritarUno() {
        if (jogador.getMao().size() == 1) {
            jogadorDisseUno = true; // Marca que o jogador gritou UNO
            exibirMensagem("🗣️ Você gritou: UNO!");
            atualizarInterface();
            verificarTurnoBot(); // Prossegue para o turno do bot
        } else {
            exibirMensagem("🚫 Você só pode gritar UNO com uma carta na mão!");
        }
    }

    // Ação de jogar uma carta pelo jogador
    private void jogarCartaJogador(Carta carta) {
        if (bloquearProximo) {
            exibirMensagem("🚫 Você foi bloqueado e não pode jogar!");
            return;
        }
        if (podeJogar(carta)) { // Verifica se a carta é válida
            jogarCarta(jogador, carta); // Joga a carta
            if (jogador.venceu()) { // Verifica se venceu
                exibirMensagem("🎉 Você venceu! Parabéns! 🎉");
                JOptionPane.showMessageDialog(frame, "Você venceu!");
                frame.dispose(); // Fecha a janela
                return;
            }
            verificarUnoJogador(); // Verifica se precisa gritar UNO
            atualizarInterface();
            verificarTurnoBot(); // Executa o turno do bot
        } else {
            exibirMensagem("🚫 Jogada inválida! A carta deve combinar com a cor ou valor da mesa.");
        }
    }

    // Verifica se o jogador precisa gritar UNO e aplica penalidade
    private void verificarUnoJogador() {
        if (jogador.getMao().size() == 1 && !jogadorDisseUno) {
            exibirMensagem("⚠️ Você tem uma carta! Clique em 'Gritar UNO!' ou será penalizado!");
            // Cria uma thread para esperar 5 segundos antes de aplicar penalidade
            new Thread(() -> {
                try {
                    Thread.sleep(5000); // Aguarda 5 segundos
                    SwingUtilities.invokeLater(() -> {
                        if (jogador.getMao().size() == 1 && !jogadorDisseUno) {
                            exibirMensagem("⚠️ Você não disse UNO! Penalidade: compre 2 cartas!");
                            for (int i = 0; i < 2; i++) {
                                Carta nova = baralho.comprarCarta();
                                if (nova != null) {
                                    jogador.adicionarCarta(nova);
                                    exibirMensagem("Você comprou: " + formatarCarta(nova));
                                }
                            }
                            atualizarInterface();
                            verificarTurnoBot();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    // Executa o turno do bot
    private void verificarTurnoBot() {
        if (bloquearProximo) {
            exibirMensagem("🚫 Bot foi bloqueado e perdeu a vez!");
            bloquearProximo = false;
            atualizarInterface();
            return;
        }
        if (bot.venceu()) {
            exibirMensagem("😢 O bot venceu! Melhor sorte na próxima!");
            JOptionPane.showMessageDialog(frame, "O bot venceu!");
            frame.dispose();
            return;
        }
        // Tenta jogar uma carta válida
        for (Carta c : bot.getMao()) {
            if (podeJogar(c)) {
                jogarCarta(bot, c);
                if (bot.getMao().size() == 1 && !botDisseUno) {
                    botDisseUno = true;
                    exibirMensagem("🤖 Bot gritou: UNO!");
                }
                atualizarInterface();
                return;
            }
        }
        // Se não puder jogar, compra uma carta
        Carta nova = baralho.comprarCarta();
        if (nova != null) {
            bot.adicionarCarta(nova);
            exibirMensagem("🤖 Bot comprou uma carta.");
        } else {
            exibirMensagem("🤖 Baralho vazio! Bot não comprou.");
            frame.dispose();
        }
        atualizarInterface();
    }

    // Verifica se uma carta pode ser jogada (mesma cor, valor ou Coringa/+4)
    private boolean podeJogar(Carta carta) {
        return carta.getCor().equals(cartaAtual.getCor()) ||
               carta.getValor().equals(cartaAtual.getValor()) ||
               carta.getCor().equals("Preto");
    }

    // Joga uma carta e aplica seus efeitos
    private void jogarCarta(Jogador jogadorAtual, Carta carta) {
        exibirMensagem(jogadorAtual.getNome() + " jogou: " + formatarCarta(carta));
        jogadorAtual.removerCarta(carta); // Remove a carta da mão
        cartaAtual = carta; // Atualiza a carta da mesa
        aplicarEfeito(carta, jogadorAtual); // Aplica efeitos especiais
    }

    // Aplica os efeitos de cartas especiais (+2, +4, Coringa, Reverse, Bloqueio)
    private void aplicarEfeito(Carta carta, Jogador jogadorAtual) {
        Jogador adversario = (jogadorAtual == jogador) ? bot : jogador;
        if (carta.getValor().equals("+2")) {
            // Adversário compra 2 cartas
            for (int i = 0; i < 2; i++) {
                Carta nova = baralho.comprarCarta();
                if (nova != null) {
                    adversario.adicionarCarta(nova);
                    exibirMensagem(adversario.getNome() + " comprou: " + formatarCarta(nova));
                }
            }
            exibirMensagem(adversario.getNome() + " comprou 2 cartas!");
        } else if (carta.getValor().equals("+4")) {
            // Adversário compra 4 cartas e jogador escolhe cor
            for (int i = 0; i < 4; i++) {
                Carta nova = baralho.comprarCarta();
                if (nova != null) {
                    adversario.adicionarCarta(nova);
                    exibirMensagem(adversario.getNome() + " comprou: " + formatarCarta(nova));
                }
            }
            exibirMensagem(adversario.getNome() + " comprou 4 cartas!");
            escolherCor(jogadorAtual);
        } else if (carta.getValor().equals("Coringa")) {
            // Jogador escolhe a nova cor
            escolherCor(jogadorAtual);
        } else if (carta.getValor().equals("Reverse")) {
            // Como são dois jogadores, Reverse é como pular a vez
            exibirMensagem("🔄 Reverse jogado! Como são dois jogadores, é como pular a vez.");
        } else if (carta.getValor().equals("Bloqueio")) {
            // Bloqueia o próximo turno
            exibirMensagem("⛔ Bloqueio jogado! O próximo jogador perde a vez.");
            bloquearProximo = true;
        }
    }

    // Permite escolher a cor após Coringa ou +4
    private void escolherCor(Jogador jogadorAtual) {
        if (jogadorAtual == jogador) {
            // Cria um painel com botões para escolher a cor
            JPanel corPanel = new JPanel();
            corPanel.setOpaque(false);
            JButton vermelho = new JButton("❤️ Vermelho");
            JButton verde = new JButton("💚 Verde");
            JButton azul = new JButton("💙 Azul");
            JButton amarelo = new JButton("💛 Amarelo");
            vermelho.setBackground(Color.RED);
            verde.setBackground(Color.GREEN);
            azul.setBackground(Color.BLUE);
            amarelo.setBackground(Color.YELLOW);
            vermelho.setForeground(Color.WHITE);
            verde.setForeground(Color.WHITE);
            azul.setForeground(Color.WHITE);
            amarelo.setForeground(Color.BLACK);
            vermelho.addActionListener(e -> setCor("Vermelho"));
            verde.addActionListener(e -> setCor("Verde"));
            azul.addActionListener(e -> setCor("Azul"));
            amarelo.addActionListener(e -> setCor("Amarelo"));
            corPanel.add(vermelho);
            corPanel.add(verde);
            corPanel.add(azul);
            corPanel.add(amarelo);
            mesaPanel.removeAll();
            mesaPanel.add(corPanel);
            mesaPanel.revalidate();
            mesaPanel.repaint();
        } else {
            // Bot escolhe uma cor aleatoriamente
            String[] cores = {"Vermelho", "Verde", "Azul", "Amarelo"};
            String corEscolhida = cores[(int)(Math.random() * 4)];
            exibirMensagem("🤖 Bot escolheu a cor: " + corEscolhida);
            cartaAtual = new Carta(corEscolhida, cartaAtual.getValor());
            atualizarInterface();
        }
    }

    // Define a nova cor após escolha
    private void setCor(String cor) {
        cartaAtual = new Carta(cor, cartaAtual.getValor());
        exibirMensagem("Nova cor: " + formatarCarta(cartaAtual));
        atualizarInterface();
        if (bloquearProximo) {
            verificarTurnoBot(); // Prossegue para o bot se houver bloqueio
        }
    }

    // Formata a carta para exibição em mensagens
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

    // Exibe uma mensagem no rótulo de mensagens
    private void exibirMensagem(String mensagem) {
        mensagemLabel.setText(mensagem);
    }
}