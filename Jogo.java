import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

// Classe principal do jogo com interface gráfica
public class Jogo {
    private Baralho baralho;
    private Jogador jogador;
    private Jogador bot;
    private Carta cartaAtual;
    private JFrame frame;            // Janela principal
    private JPanel maoJogadorPanel; // Painel para a mão do jogador
    private JPanel maoBotPanel;     // Painel para a mão do bot
    private JPanel mesaPanel;       // Painel para a carta atual
    private JLabel mensagemLabel;   // Rótulo para mensagens
    private JButton comprarButton;  // Botão para comprar carta
    private boolean bloquearProximo; // Indica se o próximo turno será bloqueado

    // Construtor
    public Jogo() {
        baralho = new Baralho();
        jogador = new Jogador("Você");
        bot = new Jogador("Bot");
        bloquearProximo = false;

        // Cada jogador compra 7 cartas no começo
        for (int i = 0; i < 7; i++) {
            jogador.adicionarCarta(baralho.comprarCarta());
            bot.adicionarCarta(baralho.comprarCarta());
        }

        // Define a primeira carta da mesa, evitando cartas especiais
        cartaAtual = baralho.comprarCarta();
        while (cartaAtual != null && (cartaAtual.getValor().equals("Coringa") ||
                cartaAtual.getValor().equals("+4") ||
                cartaAtual.getValor().equals("+2") ||
                cartaAtual.getValor().equals("Reverse") ||
                cartaAtual.getValor().equals("Bloqueio"))) {
            cartaAtual = baralho.comprarCarta();
        }
        if (cartaAtual == null) {
            System.err.println("Erro: Baralho vazio ao iniciar a mesa!");
            System.exit(1);
        }

        criarInterface(); // Cria a interface gráfica
    }

    // Cria a interface gráfica
    private void criarInterface() {
        frame = new JFrame("Jogo de UNO");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setMinimumSize(new Dimension(800, 600)); // Define tamanho mínimo
        frame.setLayout(new BorderLayout());

        // Painel principal com background
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                File bgFile = new File("imagens/background.png");
                if (bgFile.exists()) {
                    ImageIcon bgIcon = new ImageIcon(bgFile.getPath());
                    g.drawImage(bgIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(new Color(0, 100, 0)); // Fundo verde padrão
                    g.fillRect(0, 0, getWidth(), getHeight());
                    System.err.println("Imagem de background não encontrada: " + bgFile.getAbsolutePath());
                }
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        frame.setContentPane(backgroundPanel);

        // Painel da mesa (centro)
        mesaPanel = new JPanel();
        mesaPanel.setOpaque(false);
        mesaPanel.setLayout(new BoxLayout(mesaPanel, BoxLayout.Y_AXIS));

        // Botão de compra
        File versoFile = new File("imagens/verso.png");
        if (versoFile.exists()) {
            comprarButton = new JButton(new ImageIcon(versoFile.getPath()));
        } else {
            comprarButton = new JButton("Comprar");
            comprarButton.setBackground(Color.DARK_GRAY);
            comprarButton.setForeground(Color.WHITE);
            System.err.println("Imagem verso.png não encontrada: " + versoFile.getAbsolutePath());
        }
        comprarButton.setPreferredSize(new Dimension(80, 120));
        comprarButton.setMaximumSize(new Dimension(80, 120));
        comprarButton.addActionListener(e -> comprarCarta());

        // Atualiza a mesa
        atualizarMesa();

        // Painel da mão do jogador (inferior) com FlowLayout e JScrollPane
        maoJogadorPanel = new JPanel();
        maoJogadorPanel.setOpaque(false);
        maoJogadorPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 2, 5)); // Centralizado, espaçamento horizontal de 2, vertical de 5
        atualizarMaoJogador();
        JScrollPane scrollPane = new JScrollPane(maoJogadorPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER); // Desativa a rolagem vertical

        // Painel da mão do bot (superior)
        maoBotPanel = new JPanel();
        maoBotPanel.setOpaque(false);
        maoBotPanel.setLayout(new FlowLayout());
        atualizarMaoBot();

        // Rótulo para mensagens (menor)
        mensagemLabel = new JLabel("Bem-vindo ao UNO!");
        mensagemLabel.setFont(new Font("Arial", Font.BOLD, 14)); // Fonte menor
        mensagemLabel.setForeground(Color.WHITE);
        mensagemLabel.setBackground(new Color(0, 0, 0, 150));
        mensagemLabel.setOpaque(true);
        mensagemLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Menos preenchimento
        mensagemLabel.setPreferredSize(new Dimension(200, 40)); // Largura fixa

        // Adiciona os painéis à janela
        backgroundPanel.add(maoBotPanel, BorderLayout.NORTH);
        backgroundPanel.add(mesaPanel, BorderLayout.CENTER);
        backgroundPanel.add(scrollPane, BorderLayout.SOUTH); // Adiciona o JScrollPane no lugar do maoJogadorPanel
        backgroundPanel.add(mensagemLabel, BorderLayout.WEST);

        frame.setVisible(true);
    }

    // Cria um componente visual para uma carta
    private JComponent criarComponenteCarta(Carta carta, boolean verso) {
        JLabel cartaLabel = new JLabel();
        cartaLabel.setPreferredSize(new Dimension(80, 120));
        cartaLabel.setMaximumSize(new Dimension(80, 120));
        if (verso) {
            File versoFile = new File("imagens/verso.png");
            if (versoFile.exists()) {
                cartaLabel.setIcon(new ImageIcon(versoFile.getPath()));
            } else {
                cartaLabel.setText("Verso");
                cartaLabel.setBackground(Color.DARK_GRAY);
                cartaLabel.setForeground(Color.WHITE);
                cartaLabel.setOpaque(true);
                cartaLabel.setHorizontalAlignment(SwingConstants.CENTER);
                System.err.println("Imagem verso.png não encontrada: " + versoFile.getAbsolutePath());
            }
        } else if (carta != null) {
            File imagemFile = new File(carta.getImagem());
            if (imagemFile.exists()) {
                cartaLabel.setIcon(new ImageIcon(imagemFile.getPath()));
            } else {
                Color corFundo;
                switch (carta.getCor()) {
                    case "Vermelho": corFundo = Color.RED; break;
                    case "Verde": corFundo = Color.GREEN; break;
                    case "Azul": corFundo = Color.BLUE; break;
                    case "Amarelo": corFundo = Color.YELLOW; break;
                    case "Preto": corFundo = Color.BLACK; break;
                    default: corFundo = Color.GRAY;
                }
                cartaLabel.setText("<html>" + carta.getValor() + "<br>" + carta.getCor() + "</html>");
                cartaLabel.setBackground(corFundo);
                cartaLabel.setForeground(corFundo == Color.YELLOW ? Color.BLACK : Color.WHITE);
                cartaLabel.setOpaque(true);
                cartaLabel.setHorizontalAlignment(SwingConstants.CENTER);
                System.err.println("Imagem não encontrada: " + imagemFile.getAbsolutePath());
            }
        }
        return cartaLabel;
    }

    // Atualiza a mesa com a carta atual
    private void atualizarMesa() {
        mesaPanel.removeAll();
        if (cartaAtual == null) {
            exibirMensagem("Erro: Nenhuma carta na mesa!");
            return;
        }

        // Painel para a carta da mesa (fixa o tamanho)
        JPanel cartaMesaPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        cartaMesaPanel.setOpaque(false);
        JComponent cartaMesa = criarComponenteCarta(cartaAtual, false);
        cartaMesaPanel.add(cartaMesa);

        JPanel controlesPanel = new JPanel();
        controlesPanel.setOpaque(false);
        controlesPanel.add(comprarButton);

        mesaPanel.add(Box.createVerticalGlue());
        mesaPanel.add(cartaMesaPanel);
        mesaPanel.add(Box.createVerticalStrut(10));
        mesaPanel.add(controlesPanel);
        mesaPanel.add(Box.createVerticalGlue());

        mesaPanel.revalidate();
        mesaPanel.repaint();
    }

    // Atualiza a mão do jogador
    private void atualizarMaoJogador() {
        maoJogadorPanel.removeAll();
        for (Carta carta : jogador.getMao()) {
            JComponent cartaComponent = criarComponenteCarta(carta, false);
            cartaComponent.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    jogarCartaJogador(carta);
                }
            });
            maoJogadorPanel.add(cartaComponent);
        }
        maoJogadorPanel.revalidate();
        maoJogadorPanel.repaint();
    }

    // Atualiza a mão do bot (com verso)
    private void atualizarMaoBot() {
        maoBotPanel.removeAll();
        for (int i = 0; i < bot.getMao().size(); i++) {
            JComponent cartaVerso = criarComponenteCarta(null, true);
            maoBotPanel.add(cartaVerso);
        }
        maoBotPanel.revalidate();
        maoBotPanel.repaint();
    }

    // Ação de comprar uma carta
    private void comprarCarta() {
        if (bloquearProximo) {
            exibirMensagem("Você foi bloqueado e não pode comprar!");
            bloquearProximo = false;
            atualizarInterface();
            return;
        }
        Carta nova = baralho.comprarCarta();
        if (nova != null) {
            jogador.adicionarCarta(nova);
            exibirMensagem("Você comprou: " + formatarCarta(nova));
            atualizarInterface();
            turnoBot();
        } else {
            exibirMensagem("Baralho vazio! Jogo encerrado!");
            frame.dispose();
        }
    }

    // Ação de jogar uma carta pelo jogador
    private void jogarCartaJogador(Carta carta) {
        if (bloquearProximo) {
            exibirMensagem("Você foi bloqueado e não pode jogar!");
            bloquearProximo = false;
            atualizarInterface();
            return;
        }
        if (podeJogar(carta)) {
            jogarCarta(jogador, carta);
            if (jogador.venceu()) {
                exibirMensagem("Você venceu!");
                JOptionPane.showMessageDialog(frame, "Você venceu!");
                frame.dispose();
                return;
            }
            atualizarInterface();
            turnoBot();
        } else {
            exibirMensagem("Jogada inválida!");
        }
    }

    // Executa o turno do bot
    private void turnoBot() {
        if (bloquearProximo) {
            exibirMensagem("Bot foi bloqueado e perdeu a vez!");
            bloquearProximo = false;
            atualizarInterface();
            return;
        }
        if (bot.venceu()) {
            exibirMensagem("O bot venceu!");
            JOptionPane.showMessageDialog(frame, "O bot venceu!");
            frame.dispose();
            return;
        }
        exibirMensagem("Turno do bot...");
        for (Carta c : bot.getMao()) {
            if (podeJogar(c)) {
                jogarCarta(bot, c);
                atualizarInterface();
                return;
            }
        }
        Carta nova = baralho.comprarCarta();
        if (nova != null) {
            bot.adicionarCarta(nova);
            exibirMensagem("Bot comprou uma carta.");
        } else {
            exibirMensagem("Baralho vazio! Bot não comprou.");
            frame.dispose();
        }
        atualizarInterface();
    }

    // Atualiza a interface gráfica
    private void atualizarInterface() {
        atualizarMesa();
        atualizarMaoJogador();
        atualizarMaoBot();
    }

    // Verifica se uma carta pode ser jogada
    private boolean podeJogar(Carta carta) {
        return carta.getCor().equals(cartaAtual.getCor()) ||
               carta.getValor().equals(cartaAtual.getValor()) ||
               carta.getCor().equals("Preto");
    }

    // Joga uma carta e aplica seus efeitos
    private void jogarCarta(Jogador jogadorAtual, Carta carta) {
        exibirMensagem(jogadorAtual.getNome() + " jogou: " + formatarCarta(carta));
        jogadorAtual.removerCarta(carta);
        cartaAtual = carta;
        aplicarEfeito(carta, jogadorAtual);
    }

    // Aplica os efeitos de cartas especiais
    private void aplicarEfeito(Carta carta, Jogador jogadorAtual) {
        Jogador adversario = (jogadorAtual == jogador) ? bot : jogador;
        if (carta.getValor().equals("+2")) {
            for (int i = 0; i < 2; i++) {
                Carta nova = baralho.comprarCarta();
                if (nova != null) {
                    adversario.adicionarCarta(nova);
                    exibirMensagem(adversario.getNome() + " comprou: " + formatarCarta(nova));
                }
            }
            exibirMensagem(adversario.getNome() + " comprou 2 cartas!");
        } else if (carta.getValor().equals("+4")) {
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
            escolherCor(jogadorAtual);
        } else if (carta.getValor().equals("Reverse")) {
            exibirMensagem("Reverse jogado, mas como são só dois jogadores, é como pular a vez.");
        } else if (carta.getValor().equals("Bloqueio")) {
            exibirMensagem("Bloqueio jogado, o próximo perde a vez!");
            bloquearProximo = true;
        }
    }

    // Permite escolher a cor após Coringa ou +4
    private void escolherCor(Jogador jogadorAtual) {
        if (jogadorAtual == jogador) {
            JPanel corPanel = new JPanel();
            corPanel.setOpaque(false);
            JButton vermelho = new JButton("Vermelho");
            JButton verde = new JButton("Verde");
            JButton azul = new JButton("Azul");
            JButton amarelo = new JButton("Amarelo");
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
            String[] cores = {"Vermelho", "Verde", "Azul", "Amarelo"};
            String corEscolhida = cores[(int)(Math.random() * 4)];
            exibirMensagem("Bot escolheu a cor: " + corEscolhida);
            cartaAtual = new Carta(corEscolhida, cartaAtual.getValor());
            atualizarInterface();
        }
    }

    // Define a nova cor
    private void setCor(String cor) {
        cartaAtual = new Carta(cor, cartaAtual.getValor());
        exibirMensagem("Nova cor: " + formatarCarta(cartaAtual));
        atualizarInterface();
    }

    // Formata a carta para exibição
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

    // Exibe uma mensagem
    private void exibirMensagem(String mensagem) {
        mensagemLabel.setText(mensagem);
    }

    // Inicia o jogo
    public void iniciar() {
        // O jogo já é iniciado no construtor com a interface gráfica
    }

    // Método principal para executar o jogo
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Jogo();
        });
    }
}