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
    private JPanel mensagemPanel;   // Painel para a mensagem (substitui JLabel)
    private JLabel mensagemLabel;   // Rótulo dentro do painel de mensagem
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

        // Define a primeira carta da mesa, evitando cartas especiais (incluindo FazoL)
        cartaAtual = baralho.comprarCarta();
        while (cartaAtual != null && (cartaAtual.getValor().equals("Coringa") ||
                cartaAtual.getValor().equals("+4") ||
                cartaAtual.getValor().equals("+2") ||
                cartaAtual.getValor().equals("Reverse") ||
                cartaAtual.getValor().equals("Bloqueio") ||
                cartaAtual.getValor().equals("FazoL"))) {
            if (baralho.tamanho() > 0) {
                baralho.adicionarCarta(cartaAtual); // Devolve a carta ao baralho
                cartaAtual = baralho.comprarCarta();
            } else {
                System.err.println("Erro: Baralho vazio ao tentar evitar carta especial!");
                break; // Sai do loop se o baralho ficar vazio
            }
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
        File versoFile = new File("imagens/comprar.png");
        if (versoFile.exists()) {
            comprarButton = new JButton(new ImageIcon(versoFile.getPath()));
        } else {
            comprarButton = new JButton("Comprar");
            comprarButton.setBackground(Color.DARK_GRAY);
            comprarButton.setForeground(Color.WHITE);
            System.err.println("Imagem comprar.png não encontrada: " + versoFile.getAbsolutePath());
        }
        comprarButton.setPreferredSize(new Dimension(98, 139));
        comprarButton.setMaximumSize(new Dimension(98, 139));
        comprarButton.addActionListener(e -> comprarCarta());

        atualizarMesa();

        // Painel da mão do jogador (inferior) com FlowLayout e JScrollPane
        maoJogadorPanel = new JPanel();
        maoJogadorPanel.setOpaque(false);
        maoJogadorPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 2, 5));
        atualizarMaoJogador();
        JScrollPane scrollPane = new JScrollPane(maoJogadorPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER); // Desativa a rolagem vertical

        // Painel da mão do bot
        maoBotPanel = new JPanel();
        maoBotPanel.setOpaque(false);
        maoBotPanel.setLayout(new FlowLayout());
        atualizarMaoBot();

        // Painel para mensagens (substitui JLabel direto)
        mensagemPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0)); // Fundo preto
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10); // Cantos arredondados (raio 10)
            }
        };
        mensagemPanel.setOpaque(false); // Para permitir o desenho personalizado
        mensagemPanel.setLayout(new BorderLayout());
        mensagemPanel.setPreferredSize(new Dimension(270, 25)); // Altura reduzida para 25 pixels

        mensagemLabel = new JLabel("Bem-vindo ao UNO!");
        mensagemLabel.setFont(new Font("Arial", Font.BOLD, 16)); // Fonte reduzida
        mensagemLabel.setForeground(Color.WHITE); // Texto branco
        mensagemLabel.setHorizontalAlignment(SwingConstants.CENTER); // Centraliza o texto
        mensagemPanel.add(mensagemLabel, BorderLayout.CENTER);

        // Adiciona os painéis à janela
        backgroundPanel.add(maoBotPanel, BorderLayout.NORTH);
        backgroundPanel.add(mesaPanel, BorderLayout.CENTER);
        backgroundPanel.add(scrollPane, BorderLayout.SOUTH); // Adiciona o JScrollPane
        backgroundPanel.add(mensagemPanel, BorderLayout.WEST);

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
        System.out.println("DEBUG: Tentando jogar carta: " + formatarCarta(carta));
        if (bloquearProximo) {
            exibirMensagem("Você foi bloqueado e não pode jogar!");
            bloquearProximo = false;
            atualizarInterface();
            return;
        }
        if (podeJogar(carta)) {
            System.out.println("DEBUG: Carta válida, chamando jogarCarta");
            jogarCarta(jogador, carta);
            if (jogador.venceu()) {
                exibirMensagem("Você venceu!");
                JOptionPane.showMessageDialog(frame, "Você venceu!");
                frame.dispose();
                return;
            }
            atualizarInterface();
            // O jogador humano joga novamente se a carta for Bloqueio ou Reverse
            if (carta.getValor().equals("Bloqueio") || carta.getValor().equals("Reverse")) {
                exibirMensagem("Você joga novamente!");
                // Não chama turnoBot(), permitindo que o jogador continue
            } else {
                // Apenas passa o turno para o bot se não for Bloqueio ou Reverse
                Timer timer = new Timer(800, e -> {
                    turnoBot();
                });
                timer.setRepeats(false); // Executa apenas uma vez
                timer.start();
            }
        } else {
            exibirMensagem("Jogada inválida!");
            System.out.println("DEBUG: Jogada inválida para: " + formatarCarta(carta));
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
            exibirMensagem("O Bot venceu!");
            JOptionPane.showMessageDialog(frame, "O Bot venceu!");
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
        boolean podeJogar = carta.getCor().equals(cartaAtual.getCor()) ||
                            carta.getValor().equals(cartaAtual.getValor()) ||
                            carta.getCor().equals("Preto");
        System.out.println("DEBUG: podeJogar para " + formatarCarta(carta) + " contra " + formatarCarta(cartaAtual) + ": " + podeJogar);
        return podeJogar;
    }

    // Joga uma carta e aplica seus efeitos
    private void jogarCarta(Jogador jogadorAtual, Carta carta) {
        exibirMensagem(jogadorAtual.getNome() + " jogou: " + formatarCarta(carta));
        System.out.println("DEBUG: " + jogadorAtual.getNome() + " jogou: " + formatarCarta(carta));
        jogadorAtual.removerCarta(carta);
        cartaAtual = carta;
        aplicarEfeito(carta, jogadorAtual);
    }

    // Aplica os efeitos de cartas especiais
    private void aplicarEfeito(Carta carta, Jogador jogadorAtual) {
        System.out.println("DEBUG: Aplicando efeito para: " + formatarCarta(carta));
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
            System.out.println("DEBUG: Chamando escolherCor para +4");
            escolherCor(jogadorAtual);
        } else if (carta.getValor().equals("Coringa")) {
            System.out.println("DEBUG: Chamando escolherCor para Coringa");
            escolherCor(jogadorAtual);
        } else if (carta.getValor().equals("Reverse")) {
            // Reverse funciona como Bloqueio para 2 jogadores
            exibirMensagem("Reverse jogado! O próximo perde a vez!");
            // Se quem jogou foi o bot, ele joga novamente
            if (jogadorAtual == bot) {
                exibirMensagem("Bot joga novamente!");
                Timer timer = new Timer(800, e -> {
                    System.out.println("DEBUG: Iniciando novo turno do bot após Reverse");
                    turnoBot();
                });
                timer.setRepeats(false);
                timer.start();
            }
            // Se quem jogou foi o jogador humano, ele pode jogar novamente (lógica no jogarCartaJogador)
        } else if (carta.getValor().equals("Bloqueio")) {
            exibirMensagem("Bloqueio jogado, o próximo perde a vez!");
            // Se quem jogou foi o bot, ele joga novamente
            if (jogadorAtual == bot) {
                exibirMensagem("Bot joga novamente!");
                Timer timer = new Timer(800, e -> {
                    System.out.println("DEBUG: Iniciando novo turno do bot após Bloqueio");
                    turnoBot();
                });
                timer.setRepeats(false);
                timer.start();
            }
            // Se quem jogou foi o jogador humano, ele pode jogar novamente (lógica no jogarCartaJogador)
        } else if (carta.getValor().equals("FazoL")) {
            System.out.println("DEBUG: Iniciando efeito FazoL para " + jogadorAtual.getNome());
            // Efeito do FazoL: rouba todas as cartas com efeito bom do adversário
            ArrayList<Carta> cartasRoubadas = new ArrayList<>();
            ArrayList<Carta> maoAdversario = new ArrayList<>(adversario.getMao()); // Cópia para evitar ConcurrentModificationException
            System.out.println("DEBUG: Mão do adversário (" + adversario.getNome() + ") tem " + maoAdversario.size() + " cartas");
            for (Carta c : maoAdversario) {
                String valor = c.getValor();
                System.out.println("DEBUG: Verificando carta do adversário: " + formatarCarta(c));
                if (valor.equals("Coringa") || valor.equals("+4") || valor.equals("+2") ||
                    valor.equals("Bloqueio") || valor.equals("Reverse")) {
                    System.out.println("DEBUG: Carta com efeito encontrada: " + formatarCarta(c));
                    cartasRoubadas.add(c);
                    adversario.removerCarta(c);
                    jogadorAtual.adicionarCarta(c);
                }
            }
            if (cartasRoubadas.isEmpty()) {
                exibirMensagem(adversario.getNome() + " não tinha cartas com efeitos para roubar!");
                System.out.println("DEBUG: Nenhuma carta com efeito encontrada para roubar");
            } else {
                StringBuilder mensagem = new StringBuilder(jogadorAtual.getNome() + " roubou do " + adversario.getNome() + ": ");
                for (Carta c : cartasRoubadas) {
                    mensagem.append(formatarCarta(c)).append(", ");
                }
                // Remove a última vírgula e espaço
                mensagem.setLength(mensagem.length() - 2);
                exibirMensagem(mensagem.toString());
                System.out.println("DEBUG: Cartas roubadas: " + mensagem.toString());
            }
            // Adiciona escolha de cor para o FazoL
            System.out.println("DEBUG: Chamando escolherCor para FazoL");
            escolherCor(jogadorAtual);
        }
    }

    // Permite escolher a cor após Coringa, +4 ou FazoL
    private void escolherCor(Jogador jogadorAtual) {
        System.out.println("DEBUG: escolherCor chamado para jogador: " + jogadorAtual.getNome());
        if (jogadorAtual == jogador) {
            System.out.println("DEBUG: Exibindo botões de escolha de cor para o jogador humano");
            JDialog dialog = new JDialog(frame, "Escolha uma cor", true);
            dialog.setLayout(new FlowLayout());
            dialog.setSize(230, 130);
            dialog.setLocationRelativeTo(frame);

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

            vermelho.addActionListener(e -> {
                System.out.println("DEBUG: Cor escolhida: Vermelho");
                setCor("Vermelho");
                dialog.dispose();
            });
            verde.addActionListener(e -> {
                System.out.println("DEBUG: Cor escolhida: Verde");
                setCor("Verde");
                dialog.dispose();
            });
            azul.addActionListener(e -> {
                System.out.println("DEBUG: Cor escolhida: Azul");
                setCor("Azul");
                dialog.dispose();
            });
            amarelo.addActionListener(e -> {
                System.out.println("DEBUG: Cor escolhida: Amarelo");
                setCor("Amarelo");
                dialog.dispose();
            });

            dialog.add(vermelho);
            dialog.add(verde);
            dialog.add(azul);
            dialog.add(amarelo);
            dialog.setVisible(true);
            System.out.println("DEBUG: Botões exibidos no dialog");
        } else {
            String[] cores = {"Vermelho", "Verde", "Azul", "Amarelo"};
            String corEscolhida = cores[(int)(Math.random() * 4)];
            exibirMensagem("Bot escolheu a cor: " + corEscolhida);
            System.out.println("DEBUG: Bot escolheu a cor: " + corEscolhida);
            setCor(corEscolhida);
        }
    }

    // Define a nova cor e atualiza a imagem da carta
    private void setCor(String cor) {
        System.out.println("DEBUG: Definindo nova cor: " + cor);
        String valor = cartaAtual.getValor(); // Mantém o valor ("Coringa", "+4" ou "FazoL")
        cartaAtual = new Carta(cor, valor); // Cria uma nova carta com a cor escolhida
        // Atualiza a imagem com base na nova cor
        String novaImagem = "imagens/" + cor + "_" + valor + ".png";
        File imagemFile = new File(novaImagem);
        if (imagemFile.exists()) {
            cartaAtual.setImagem(novaImagem);
            System.out.println("DEBUG: Imagem atualizada para: " + novaImagem);
        } else {
            System.err.println("Imagem não encontrada: " + novaImagem);
        }
        exibirMensagem("Nova cor: " + formatarCarta(cartaAtual));
        atualizarInterface();
    }

    // Formata a carta para exibição
    private String formatarCarta(Carta carta) {
        String cor = carta.getCor();
        String valor = carta.getValor();
        return cor + " " + valor;
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