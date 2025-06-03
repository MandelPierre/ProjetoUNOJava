import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import javax.swing.*;

public class Jogo {
    private Baralho baralho;
    private Jogador jogador;
    private Jogador bot;
    private Carta cartaAtual;
    private JFrame frame;            //Janela principal
    private JPanel maoJogadorPanel; //Mão do jogador
    private JPanel maoBotPanel;     //Mão do bot
    private JPanel mesaPanel;       //Painel para a carta atual
    private JPanel mensagemPanel;   //Painel para a mensagem
    private JLabel mensagemLabel;   //Rótulo dentro do painel de mensagem
    private JButton comprarButton;  //Botão para comprar carta
    private boolean bloquearProximo; //Indica se o próximo turno será bloqueado
    private JButton gritarButton;    //Botão para gritar gol
    private boolean jogadorGritouGol;
    private boolean botGritouGol;

    public Jogo() {
        baralho = new Baralho();
        jogador = new Jogador("Você");
        bot = new Jogador("Bot");
        bloquearProximo = false;
        jogadorGritouGol = false;
        botGritouGol = false;

        // Cada jogador compra 7 cartas no começo
        for (int i = 0; i < 7; i++) {
            jogador.adicionarCarta(baralho.comprarCarta());
            bot.adicionarCarta(baralho.comprarCarta());
        }

        //Define a primeira carta da mesa, evitando cartas especiais (incluindo FazoL)
        cartaAtual = baralho.comprarCarta();
        while (cartaAtual != null && (cartaAtual.getValor().equals("Coringa") ||
                cartaAtual.getValor().equals("+4") ||
                cartaAtual.getValor().equals("+2") ||
                cartaAtual.getValor().equals("Reverse") ||
                cartaAtual.getValor().equals("Bloqueio") ||
                cartaAtual.getValor().equals("FazoL"))) {
            if (baralho.tamanho() > 0) {
                baralho.adicionarCarta(cartaAtual); //Devolve a carta ao baralho
                cartaAtual = baralho.comprarCarta();
            } else {
                break; //Sai do loop se o baralho ficar vazio
            }
        }
        if (cartaAtual == null) {
            System.exit(1);
        }

        criarInterface(); //Chama método que cria a interface gráfica
    }

    //Cria a interface gráfica
    private void criarInterface() {
        frame = new JFrame("Jogo GOL");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setMinimumSize(new Dimension(800, 600)); //Define tamanho mínimo
        frame.setLayout(new BorderLayout());

        //Painel principal com background
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                File bgFile = new File("imagens/background.png");
                if (bgFile.exists()) {
                    ImageIcon bgIcon = new ImageIcon(bgFile.getPath());
                    g.drawImage(bgIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(new Color(0, 100, 0)); //Fundo verde padrão
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        frame.setContentPane(backgroundPanel);

        //Painel da mesa (centro)
        mesaPanel = new JPanel();
        mesaPanel.setOpaque(false);
        mesaPanel.setLayout(new BoxLayout(mesaPanel, BoxLayout.Y_AXIS));

        //Botão de Gritar GOL
        File botaoFile = new File("imagens/gritarGol.png");
        if (botaoFile.exists()) {
            gritarButton = new JButton(new ImageIcon(botaoFile.getPath()));
        } else {
            gritarButton = new JButton("Gritar GOL");
            gritarButton.setBackground(Color.BLACK);
            gritarButton.setForeground(Color.WHITE);
        }
        gritarButton.setPreferredSize(new Dimension(120, 60));
        gritarButton.setMaximumSize(new Dimension(120, 60));
        gritarButton.setEnabled(false);
        gritarButton.addActionListener(e -> {
            jogadorGritouGol = true;
            exibirMensagem("Você gritou GOL!");
            gritarButton.setEnabled(false);
        });

        //Botão de compra
        File versoFile = new File("imagens/comprar.png");
        if (versoFile.exists()) {
            comprarButton = new JButton(new ImageIcon(versoFile.getPath()));
        } else {
            comprarButton = new JButton("Comprar");
            comprarButton.setBackground(Color.DARK_GRAY);
            comprarButton.setForeground(Color.WHITE);
        }
        comprarButton.setPreferredSize(new Dimension(98, 139));
        comprarButton.setMaximumSize(new Dimension(98, 139));
        comprarButton.addActionListener(e -> comprarCarta());

        atualizarMesa();

        //Painel da mão do jogador (inferior) com FlowLayout e JScrollPane
        maoJogadorPanel = new JPanel();
        maoJogadorPanel.setOpaque(false);
        maoJogadorPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 2, 5));
        atualizarMaoJogador();
        JScrollPane scrollPane = new JScrollPane(maoJogadorPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER); // Desativa a rolagem vertical

        //Painel da mão do bot
        maoBotPanel = new JPanel();
        maoBotPanel.setOpaque(false);
        maoBotPanel.setLayout(new FlowLayout());
        atualizarMaoBot();

        //Painel para mensagens (substitui JLabel direto)
        mensagemPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0)); //Fundo preto
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 50, 50); //Cantos arredondados (raio 10)
            }
        };
        mensagemPanel.setOpaque(false); //Para permitir o desenho personalizado
        mensagemPanel.setLayout(new BorderLayout());
        mensagemPanel.setPreferredSize(new Dimension(270, 200));
        mensagemLabel = new JLabel("Bem-vindo ao GOL!");
        mensagemLabel.setFont(new Font("Dialog", Font.BOLD, 18)); //Fonte reduzida
        mensagemLabel.setForeground(Color.WHITE); //Texto branco
        mensagemLabel.setHorizontalAlignment(SwingConstants.CENTER); //Centraliza o texto
        mensagemPanel.add(mensagemLabel, BorderLayout.CENTER);

        //Adiciona os painéis à janela
        backgroundPanel.add(maoBotPanel, BorderLayout.NORTH);
        backgroundPanel.add(mesaPanel, BorderLayout.CENTER);
        backgroundPanel.add(scrollPane, BorderLayout.SOUTH); //Adiciona o JScrollPane
        backgroundPanel.add(mensagemPanel, BorderLayout.WEST);

        // Painel auxiliar para conter o mensagemPanel e evitar que o BorderLayout estique
        JPanel mensagemWrapper = new JPanel(new GridBagLayout());
        mensagemWrapper.setOpaque(false); // Fundo transparente
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER; // Centraliza horizontalmente
        gbc.fill = GridBagConstraints.NONE; // Não estica o componente
        mensagemWrapper.add(mensagemPanel, gbc);
        mensagemWrapper.add(mensagemPanel);

        // Adiciona os painéis à janela
        backgroundPanel.add(mensagemWrapper, BorderLayout.WEST); // Adiciona o wrapper ao invés do mensagemPanel diretamente

        frame.setVisible(true);
    }

    //Cria um componente visual para uma carta
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
            }
        }
        return cartaLabel;
    }

    //Atualiza a mesa com a carta atual
    private void atualizarMesa() {
        mesaPanel.removeAll();
        if (cartaAtual == null) {
            exibirMensagem("Erro: Nenhuma carta na mesa!");
            return;
        }

        //Painel para a carta da mesa (fixa o tamanho)
        JPanel cartaMesaPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        cartaMesaPanel.setOpaque(false);
        JComponent cartaMesa = criarComponenteCarta(cartaAtual, false);
        cartaMesaPanel.add(cartaMesa);

        JPanel controlesPanel = new JPanel();
        controlesPanel.setOpaque(false);
        controlesPanel.add(comprarButton);
        controlesPanel.add(gritarButton);

        mesaPanel.add(Box.createVerticalGlue());
        mesaPanel.add(cartaMesaPanel);
        mesaPanel.add(Box.createVerticalStrut(10));
        mesaPanel.add(controlesPanel);
        mesaPanel.add(Box.createVerticalGlue());

        mesaPanel.revalidate();
        mesaPanel.repaint();
    }

    //Atualiza a mão do jogador
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

    //Atualiza a mão do bot (com verso)
    private void atualizarMaoBot() {
        maoBotPanel.removeAll();
        for (int i = 0; i < bot.getMao().size(); i++) {
            JComponent cartaVerso = criarComponenteCarta(null, true);
            maoBotPanel.add(cartaVerso);
        }
        maoBotPanel.revalidate();
        maoBotPanel.repaint();
    }

    //Ação de comprar uma carta
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

    //Ação de jogar uma carta pelo jogador
    private void jogarCartaJogador(Carta carta) {
        if (bloquearProximo) {
            exibirMensagem("Você foi bloqueado e não pode jogar!");
            bloquearProximo = false;
            atualizarInterface();
            return;
        }
        if (podeJogar(carta)) {
            boolean tinhaDuasCartas = jogador.getMao().size() == 2;
            jogarCarta(jogador, carta);

            //Verifica se o jogador ficou com 1 carta e não gritou GOL
            if(tinhaDuasCartas && !jogadorGritouGol) {
                exibirMensagem("Você não gritou GOL! Penalidade +2 cartas!");
                for (int i = 0; i < 2; i++) {
                    Carta nova = baralho.comprarCarta();
                    if(nova != null) {
                        jogador.adicionarCarta(nova);
                        exibirMensagem("Você Comprou: " + formatarCarta(nova));
                    }
                }
            }
            jogadorGritouGol = false; //Para resetar

            if (jogador.venceu()) {
                exibirMensagem("Você venceu!");
                JOptionPane.showMessageDialog(frame, "Você venceu!");
                frame.dispose();
                return;
            }
            atualizarInterface();
            //O jogador humano joga novamente se a carta for Bloqueio ou Reverse
            if (carta.getValor().equals("Bloqueio") || carta.getValor().equals("Reverse")) {
                exibirMensagem("Você joga novamente!");
                //Não chama turnoBot(), permitindo que o jogador continue
            } else {
                //Apenas passa o turno para o bot se não for Bloqueio ou Reverse
                Timer timer = new Timer(800, e -> {
                    turnoBot();
                });
                timer.setRepeats(false); //Executa apenas uma vez
                timer.start();
            }
        } else {
            exibirMensagem("Jogada inválida!");
        }
    }

    //Executa o turno do bot
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

        //Verifica se o bot tem 2 cartas e decide se grita GOL
        if(bot.getMao().size() == 2 && !botGritouGol) {
            if(Math.random() < 0.6) { //60% de chance do bot gritar GOL
                botGritouGol = true;
                exibirMensagem("Bot Gritou GOL!");
            }
        }
        for (Carta c : bot.getMao()) {
            if (podeJogar(c)) {
                boolean tinhaDuasCartas = bot.getMao().size() == 2;
                jogarCarta(bot, c);

                //Verifica se o bot ficou com 1 carta e não gritou GOL
                if(tinhaDuasCartas && !botGritouGol) {
                    exibirMensagem("Bot não gritou GOL! Penalidade +2 cartas!");
                    for (int i = 0; i < 2; i++) {
                        Carta nova = baralho.comprarCarta();
                        if(nova != null) {
                            bot.adicionarCarta(nova);
                            exibirMensagem("Bot comprou: " + formatarCarta(nova));
                        } 
                    }
                }
                botGritouGol = false; //Para resetar
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

    //Atualiza a interface gráfica
    private void atualizarInterface() {
        atualizarMesa();
        atualizarMaoJogador();
        atualizarMaoBot();
        //Habilita o botão gritar GOL se o jogador tiver exatamente 2 cartas
        gritarButton.setEnabled(jogador.getMao().size() == 2 && !jogadorGritouGol);
    }

    //Verifica se uma carta pode ser jogada
    private boolean podeJogar(Carta carta) {
        boolean podeJogar = carta.getCor().equals(cartaAtual.getCor()) ||
                            carta.getValor().equals(cartaAtual.getValor()) ||
                            carta.getCor().equals("Preto");
        return podeJogar;
    }

    //Joga uma carta e aplica seus efeitos
    private void jogarCarta(Jogador jogadorAtual, Carta carta) {
        exibirMensagem(jogadorAtual.getNome() + " jogou: " + formatarCarta(carta));
        jogadorAtual.removerCarta(carta);
        cartaAtual = carta;
        aplicarEfeito(carta, jogadorAtual);
    }

    //Aplica os efeitos de cartas especiais
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
            //Reverse funciona como Bloqueio para 2 jogadores
            exibirMensagem("Reverse jogado! O próximo perde a vez!");
            //Se quem jogou foi o bot, ele joga novamente
            if (jogadorAtual == bot) {
                exibirMensagem("Bot joga novamente!");
                Timer timer = new Timer(800, e -> {
                    turnoBot();
                });
                timer.setRepeats(false);
                timer.start();
            }
            //Se quem jogou foi o jogador humano, ele pode jogar novamente (lógica no jogarCartaJogador)
        } else if (carta.getValor().equals("Bloqueio")) {
            exibirMensagem("Bloqueio jogado, o próximo perde a vez!");
            //Se quem jogou foi o bot, ele joga novamente
            if (jogadorAtual == bot) {
                exibirMensagem("Bot joga novamente!");
                Timer timer = new Timer(800, e -> {
                    turnoBot();
                });
                timer.setRepeats(false);
                timer.start();
            }
            //Se quem jogou foi o jogador humano, ele pode jogar novamente (lógica no jogarCartaJogador)
        } else if (carta.getValor().equals("FazoL")) {
            //Efeito do FazoL: rouba todas as cartas com efeito bom do adversário
            ArrayList<Carta> cartasRoubadas = new ArrayList<>();
            ArrayList<Carta> maoAdversario = new ArrayList<>(adversario.getMao()); // Cópia para evitar ConcurrentModificationException
            for (Carta c : maoAdversario) {
                String valor = c.getValor();
                if (valor.equals("Coringa") || valor.equals("+4") || valor.equals("+2") ||
                    valor.equals("Bloqueio") || valor.equals("Reverse")) {
                    cartasRoubadas.add(c);
                    adversario.removerCarta(c);
                    jogadorAtual.adicionarCarta(c);
                }
            }
            if (cartasRoubadas.isEmpty()) {
                exibirMensagem(adversario.getNome() + " não tinha cartas com efeitos para roubar!");
            } else {
                StringBuilder mensagem = new StringBuilder(jogadorAtual.getNome() + " roubou do " + adversario.getNome() + ": ");
                for (Carta c : cartasRoubadas) {
                    mensagem.append(formatarCarta(c)).append(", ");
                }
                //Remove a última vírgula e espaço
                mensagem.setLength(mensagem.length() - 2);
                exibirMensagem(mensagem.toString());
            }
            //Adiciona escolha de cor para o FazoL
            escolherCor(jogadorAtual);
        }
    }

    //Permite escolher a cor após Coringa, +4 ou FazoL
    private void escolherCor(Jogador jogadorAtual) {
        if (jogadorAtual == jogador) {
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
                setCor("Vermelho");
                dialog.dispose();
            });
            verde.addActionListener(e -> {
                setCor("Verde");
                dialog.dispose();
            });
            azul.addActionListener(e -> {
                setCor("Azul");
                dialog.dispose();
            });
            amarelo.addActionListener(e -> {
                setCor("Amarelo");
                dialog.dispose();
            });

            dialog.add(vermelho);
            dialog.add(verde);
            dialog.add(azul);
            dialog.add(amarelo);
            dialog.setVisible(true);
        } else {
            String[] cores = {"Vermelho", "Verde", "Azul", "Amarelo"};
            String corEscolhida = cores[(int)(Math.random() * 4)];
            exibirMensagem("Bot escolheu a cor: " + corEscolhida);
            setCor(corEscolhida);
        }
    }

    //Define a nova cor e atualiza a imagem da carta
    private void setCor(String cor) {
        String valor = cartaAtual.getValor(); //Mantém o valor ("Coringa", "+4" ou "FazoL")
        cartaAtual = new Carta(cor, valor); //Cria uma nova carta com a cor escolhida
        //Atualiza a imagem com base na nova cor
        String novaImagem = "imagens/" + cor + "_" + valor + ".png";
        File imagemFile = new File(novaImagem);
        if (imagemFile.exists()) {
            cartaAtual.setImagem(novaImagem);
        } else {
        }
        exibirMensagem("Nova cor: " + formatarCarta(cartaAtual));
        atualizarInterface();
    }

    //Formata a carta para exibição
    private String formatarCarta(Carta carta) {
        String cor = carta.getCor();
        String valor = carta.getValor();
        return cor + " " + valor;
    }

    //Exibe uma mensagem
    private void exibirMensagem(String mensagem) {
        mensagemLabel.setText(mensagem);
    }

    //Inicia o jogo
    public void iniciar() {
        //O jogo já é iniciado no construtor com a interface gráfica
    }

    //Método principal para executar o jogo
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Jogo();
        });
    }
}