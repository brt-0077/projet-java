import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;

public class Menu extends JFrame {
    private final Image backgroundImage;

    public Menu() {
        setTitle("Jeu de Dames - Menu Principal");
        setSize(980, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        backgroundImage = chargerImageTheme();

        // Panneau principal avec image de theme + voile de contraste.
        JPanel panelPrincipal = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (backgroundImage != null) {
                    g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    Color c1 = new Color(26, 33, 64);
                    Color c2 = new Color(12, 16, 35);
                    GradientPaint grad = new GradientPaint(0, 0, c1, 0, getHeight(), c2);
                    g2.setPaint(grad);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }

                g2.setColor(new Color(7, 11, 24, 165));
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.setColor(new Color(255, 255, 255, 14));
                for (int i = -200; i < getWidth() + 200; i += 90) {
                    g2.fillRect(i, 0, 38, getHeight());
                }
            }
        };
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(38, 70, 54, 70));

        // Carte de titre.
        JPanel panelTitre = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color gradCouleur1 = new Color(255, 185, 90, 70);
                Color gradCouleur2 = new Color(193, 84, 36, 90);
                GradientPaint grad = new GradientPaint(0, 0, gradCouleur1, getWidth(), getHeight(), gradCouleur2);
                g2.setPaint(grad);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 26, 26);

                g2.setColor(new Color(255, 224, 181, 140));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 26, 26);
            }
        };
        panelTitre.setLayout(new BoxLayout(panelTitre, BoxLayout.Y_AXIS));
        panelTitre.setOpaque(false);
        panelTitre.setMaximumSize(new Dimension(660, 160));
        panelTitre.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelTitre.setBorder(BorderFactory.createEmptyBorder(24, 34, 24, 34));

        JLabel logo = new JLabel("DAMES");
        logo.setFont(new Font("Serif", Font.BOLD, 22));
        logo.setForeground(new Color(255, 235, 205));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelTitre.add(logo);

        JLabel titre = new JLabel("JEU DE DAMES");
        titre.setFont(new Font("Serif", Font.BOLD, 58));
        titre.setForeground(new Color(255, 247, 235));
        titre.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelTitre.add(titre);

        JLabel sousTitre = new JLabel("Mode Classique et Duel contre IA");
        sousTitre.setFont(new Font("Serif", Font.ITALIC, 18));
        sousTitre.setForeground(new Color(240, 228, 214));
        sousTitre.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelTitre.add(sousTitre);

        panelPrincipal.add(panelTitre);
        panelPrincipal.add(Box.createVerticalStrut(34));

        JPanel panelBoutons = new JPanel();
        panelBoutons.setOpaque(false);
        panelBoutons.setLayout(new BoxLayout(panelBoutons, BoxLayout.Y_AXIS));
        panelBoutons.setAlignmentX(Component.CENTER_ALIGNMENT);

        BoutonStyleise btn1vs1 = new BoutonStyleise("1 vs 1  -  Joueur contre Joueur",
                new Color(46, 138, 102),
                new Color(35, 119, 88));
        btn1vs1.addActionListener(e -> lancerJeu(false, demanderNomsJoueurs(false)));
        panelBoutons.add(btn1vs1);

        panelBoutons.add(Box.createVerticalStrut(20));

        BoutonStyleise btnVsIA = new BoutonStyleise("🤖  Contre l'IA",
                new Color(179, 84, 45),
                new Color(158, 68, 36));
        btnVsIA.addActionListener(e -> lancerJeu(true, demanderNomsJoueurs(true)));
        panelBoutons.add(btnVsIA);

        panelBoutons.add(Box.createVerticalStrut(20));

        BoutonStyleise btnQuitter = new BoutonStyleise("Quitter",
                new Color(77, 84, 108),
                new Color(62, 68, 91));
        btnQuitter.addActionListener(e -> System.exit(0));
        panelBoutons.add(btnQuitter);

        panelPrincipal.add(panelBoutons);
        panelPrincipal.add(Box.createVerticalGlue());

        JLabel credit = new JLabel("Theme image: projet red 2.avif");
        credit.setFont(new Font("Serif", Font.PLAIN, 14));
        credit.setForeground(new Color(233, 220, 205));
        credit.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelPrincipal.add(credit);

        add(panelPrincipal);
        setVisible(true);
    }

    private Image chargerImageTheme() {
        File imageFile = new File("..", "projet red 2.avif");
        if (!imageFile.exists()) {
            imageFile = new File("projet red 2.avif");
        }
        if (!imageFile.exists()) return null;

        ImageIcon icon = new ImageIcon(imageFile.getAbsolutePath());
        if (icon.getIconWidth() <= 0 || icon.getIconHeight() <= 0) {
            return null;
        }
        return icon.getImage();
    }

    private class BoutonStyleise extends JButton {
        private final Color couleurBase;
        private final Color couleurHover;
        private boolean estSurvolee = false;

        public BoutonStyleise(String texte, Color couleurBase, Color couleurHover) {
            super(texte);
            this.couleurBase = couleurBase;
            this.couleurHover = couleurHover;
            
            setFont(new Font("Serif", Font.BOLD, 24));
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setPreferredSize(new Dimension(520, 74));
            setMaximumSize(new Dimension(520, 74));
            setAlignmentX(Component.CENTER_ALIGNMENT);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseInputAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    estSurvolee = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    estSurvolee = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(0, 0, 0, 120));
            g2.fillRoundRect(5, 6, getWidth() - 8, getHeight() - 10, 24, 24);

            Color couleur = estSurvolee ? couleurHover : couleurBase;
            GradientPaint grad = new GradientPaint(0, 0, couleur.brighter(), 0, getHeight(), couleur.darker());
            g2.setPaint(grad);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);

            g2.setColor(new Color(255, 236, 218, estSurvolee ? 230 : 180));
            g2.setStroke(new BasicStroke(estSurvolee ? 3.0f : 1.5f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 24, 24);

            if (estSurvolee) {
                g2.setColor(new Color(255, 255, 255, 45));
                g2.fillRoundRect(6, 4, getWidth() - 12, 20, 18, 18);
            }

            super.paintComponent(g);
        }
    }

    private String[] demanderNomsJoueurs(boolean avecIA) {
        final String[] resultat = new String[2];

        JDialog dialogue = new JDialog(this, "Configuration des joueurs", true);
        dialogue.setLocationRelativeTo(this);
        dialogue.setResizable(false);

        JPanel fond = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color c1 = new Color(32, 42, 78);
                Color c2 = new Color(14, 18, 36);
                GradientPaint grad = new GradientPaint(0, 0, c1, 0, getHeight(), c2);
                g2.setPaint(grad);
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.setColor(new Color(255, 255, 255, 12));
                for (int i = -120; i < getWidth() + 120; i += 80) {
                    g2.fillRect(i, 0, 24, getHeight());
                }
            }
        };
        fond.setLayout(new GridBagLayout());

        JPanel carte = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(0, 0, 0, 90));
                g2.fillRoundRect(6, 8, getWidth() - 10, getHeight() - 12, 28, 28);

                GradientPaint grad = new GradientPaint(
                        0, 0, new Color(255, 228, 196, 48),
                        0, getHeight(), new Color(255, 213, 168, 24)
                );
                g2.setPaint(grad);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 28, 28);

                g2.setColor(new Color(255, 239, 222, 150));
                g2.setStroke(new BasicStroke(1.8f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 28, 28);
            }
        };
        carte.setOpaque(false);
        carte.setLayout(new BoxLayout(carte, BoxLayout.Y_AXIS));
        carte.setBorder(BorderFactory.createEmptyBorder(20, 26, 18, 26));
        carte.setPreferredSize(new Dimension(500, 320));

        JLabel titreDialogue = new JLabel("Choix des joueurs");
        titreDialogue.setFont(new Font("Serif", Font.BOLD, 30));
        titreDialogue.setForeground(new Color(255, 241, 226));
        titreDialogue.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sousTitre = new JLabel(avecIA ? "Entre ton nom avant d'affronter l'IA" : "Personnalise les noms de la partie");
        sousTitre.setFont(new Font("Serif", Font.ITALIC, 14));
        sousTitre.setForeground(new Color(232, 220, 205));
        sousTitre.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSeparator separateur = new JSeparator();
        separateur.setForeground(new Color(255, 236, 220, 120));
        separateur.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        JTextField joueur1Field = creerChampNom("Joueur 1", true);
        JTextField joueur2Field = creerChampNom(avecIA ? "IA" : "Joueur 2", !avecIA);

        JLabel label1 = new JLabel("Joueur blanc");
        label1.setFont(new Font("Serif", Font.BOLD, 15));
        label1.setForeground(new Color(248, 236, 220));

        JLabel label2 = new JLabel(avecIA ? "Joueur noir (IA)" : "Joueur noir");
        label2.setFont(new Font("Serif", Font.BOLD, 15));
        label2.setForeground(new Color(248, 236, 220));

        JPanel boutons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        boutons.setOpaque(false);

        JButton annuler = creerBoutonDialogue("Annuler", new Color(78, 84, 110), new Color(64, 70, 95));
        JButton lancer = creerBoutonDialogue("Lancer", new Color(52, 146, 108), new Color(40, 126, 92));

        annuler.addActionListener(e -> dialogue.dispose());
        lancer.addActionListener(e -> {
            String nom1 = joueur1Field.getText().trim();
            String nom2 = joueur2Field.getText().trim();
            if (nom1.isEmpty()) nom1 = "Joueur 1";
            if (nom2.isEmpty()) nom2 = avecIA ? "IA" : "Joueur 2";

            resultat[0] = nom1;
            resultat[1] = nom2;
            dialogue.dispose();
        });

        boutons.add(annuler);
        boutons.add(lancer);

        carte.add(titreDialogue);
        carte.add(Box.createVerticalStrut(6));
        carte.add(sousTitre);
        carte.add(Box.createVerticalStrut(10));
        carte.add(separateur);
        carte.add(Box.createVerticalStrut(16));
        carte.add(label1);
        carte.add(Box.createVerticalStrut(6));
        carte.add(joueur1Field);
        carte.add(Box.createVerticalStrut(12));
        carte.add(label2);
        carte.add(Box.createVerticalStrut(6));
        carte.add(joueur2Field);
        carte.add(Box.createVerticalStrut(16));
        carte.add(boutons);

        fond.add(carte);
        dialogue.setContentPane(fond);
        dialogue.pack();
        dialogue.setSize(560, 400);
        dialogue.setMinimumSize(new Dimension(560, 400));
        dialogue.setVisible(true);

        if (resultat[0] == null) {
            return null;
        }
        return new String[]{resultat[0], resultat[1]};
    }

    private JTextField creerChampNom(String texteParDefaut, boolean editable) {
        ChampNomStyle champ = new ChampNomStyle(texteParDefaut, editable);
        champ.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        return champ;
    }

    private class ChampNomStyle extends JTextField {
        private final boolean editableCustom;

        public ChampNomStyle(String texteParDefaut, boolean editable) {
            super(texteParDefaut);
            this.editableCustom = editable;
            setEditable(editable);
            setOpaque(false);
            setFont(new Font("Serif", Font.PLAIN, 18));
            setForeground(editable ? new Color(34, 34, 34) : new Color(95, 95, 95));
            setCaretColor(new Color(34, 34, 34));
            setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
            setSelectionColor(new Color(255, 217, 170));
            setSelectedTextColor(new Color(40, 40, 40));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color fond = editableCustom ? new Color(255, 248, 240) : new Color(236, 233, 229);
            Color bordure = isFocusOwner() && editableCustom ? new Color(214, 159, 97) : new Color(203, 177, 147);

            g2.setColor(new Color(0, 0, 0, 60));
            g2.fillRoundRect(2, 3, getWidth() - 4, getHeight() - 4, 18, 18);

            g2.setColor(fond);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);

            g2.setColor(bordure);
            g2.setStroke(new BasicStroke(isFocusOwner() && editableCustom ? 2.4f : 1.6f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);

            super.paintComponent(g);
        }
    }

    private JButton creerBoutonDialogue(String texte, Color base, Color hover) {
        JButton bouton = new JButton(texte) {
            private boolean survol = false;

            {
                addMouseListener(new MouseInputAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        survol = true;
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        survol = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(0, 0, 0, 85));
                g2.fillRoundRect(3, 4, getWidth() - 6, getHeight() - 6, 20, 20);

                Color couleur = survol ? hover : base;
                GradientPaint grad = new GradientPaint(0, 0, couleur.brighter(), 0, getHeight(), couleur.darker());
                g2.setPaint(grad);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);

                g2.setColor(new Color(255, 240, 220, survol ? 240 : 170));
                g2.setStroke(new BasicStroke(survol ? 1.8f : 1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);

                if (survol) {
                    g2.setColor(new Color(255, 255, 255, 45));
                    g2.fillRoundRect(5, 4, getWidth() - 10, 14, 12, 12);
                }

                super.paintComponent(g);
            }
        };

        bouton.setFont(new Font("Serif", Font.BOLD, 17));
        bouton.setForeground(Color.WHITE);
        bouton.setBorderPainted(false);
        bouton.setFocusPainted(false);
        bouton.setContentAreaFilled(false);
        bouton.setPreferredSize(new Dimension(126, 40));
        return bouton;
    }

    private void lancerJeu(boolean avecIA, String[] nomsJoueurs) {
        if (nomsJoueurs == null) {
            return;
        }

        dispose();
        String nomJoueur1 = nomsJoueurs[0];
        String nomJoueur2 = nomsJoueurs[1];
        SwingUtilities.invokeLater(() -> new InterfaceGraphique(avecIA, nomJoueur1, nomJoueur2));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Menu());
    }
}
