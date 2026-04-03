import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class InterfaceGraphique extends JFrame {

    JButton[][] boutons = new JButton[8][8];
    Jeu jeu;
    boolean avecIA;
    JLabel statutLabel;
    JLabel timerLabel;
    JLabel alerteLabel;
    Timer chronoTour;
    Timer alerteTimer;
    int secondesRestantes = 30;
    boolean partieTerminee = false;

    int xSelection = -1, ySelection = -1;

    public InterfaceGraphique(boolean avecIA) {
        this(avecIA, "Joueur", avecIA ? "IA" : "Joueur 2");
    }

    public InterfaceGraphique(boolean avecIA, String nomJoueur1, String nomJoueur2) {
        this.avecIA = avecIA;
        this.jeu = new Jeu(avecIA, nomJoueur1, nomJoueur2);
        
        setTitle("Jeu de Dames");
        setSize(700, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panneau principal avec titre et plateau
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBackground(new Color(45, 45, 90));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Titre et statut
        JPanel panelHaut = new JPanel(new BorderLayout());
        panelHaut.setBackground(new Color(45, 45, 90));

        JLabel titre = new JLabel(avecIA ? "Jeu de Dames - 1vs IA" : "Jeu de Dames - 1vs1");
        titre.setFont(new Font("Arial", Font.BOLD, 24));
        titre.setForeground(new Color(255, 215, 0));
        panelHaut.add(titre, BorderLayout.WEST);

        alerteLabel = new JLabel(" ");
        alerteLabel.setFont(new Font("Arial", Font.BOLD, 16));
        alerteLabel.setForeground(new Color(255, 170, 120));
        alerteLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panelHaut.add(alerteLabel, BorderLayout.CENTER);

        JPanel panelInfos = new JPanel();
        panelInfos.setOpaque(false);
        panelInfos.setLayout(new BoxLayout(panelInfos, BoxLayout.Y_AXIS));

        statutLabel = new JLabel("Tour: Joueur (Blanc)");
        statutLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statutLabel.setForeground(Color.WHITE);
        statutLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        timerLabel = new JLabel("Temps: 30s");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        timerLabel.setForeground(new Color(255, 235, 140));
        timerLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        panelInfos.add(statutLabel);
        panelInfos.add(Box.createVerticalStrut(4));
        panelInfos.add(timerLabel);
        panelHaut.add(panelInfos, BorderLayout.EAST);

        panelPrincipal.add(panelHaut, BorderLayout.NORTH);

        // Plateau de jeu
        JPanel panelPlateau = new JPanel(new GridLayout(8, 8, 2, 2));
        panelPlateau.setBackground(Color.BLACK);

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {

                JButton btn = new JButton();
                boutons[i][j] = btn;
                btn.setFont(new Font("Arial", Font.BOLD, 56));
                btn.setFocusPainted(false);

                Color couleurFond;
                if ((i + j) % 2 == 0)
                    couleurFond = new Color(240, 217, 181); // Beige
                else
                    couleurFond = new Color(139, 69, 19); // Marron

                btn.setBackground(couleurFond);
                btn.setOpaque(true);

                int x = i;
                int y = j;

                btn.addActionListener(e -> gererClic(x, y));

                panelPlateau.add(btn);
            }
        }
        panelPrincipal.add(panelPlateau, BorderLayout.CENTER);

        // Bouton Retour au Menu
        JPanel panelBas = new JPanel();
        panelBas.setBackground(new Color(45, 45, 90));

        JButton btnMenu = new JButton("Retour au Menu");
        btnMenu.setFont(new Font("Arial", Font.BOLD, 14));
        btnMenu.setBackground(new Color(66, 133, 244));
        btnMenu.setForeground(Color.WHITE);
        btnMenu.setFocusPainted(false);
        btnMenu.addActionListener(e -> {
            if (chronoTour != null) {
                chronoTour.stop();
            }
            dispose();
            SwingUtilities.invokeLater(() -> new Menu());
        });
        panelBas.add(btnMenu);

        panelPrincipal.add(panelBas, BorderLayout.SOUTH);

        add(panelPrincipal);
        setVisible(true);
        rafraichir();
        mettreAJourStatut();
        demarrerChronoTour();
    }

    private void gererClic(int x, int y) {
        if (partieTerminee) return;
        if (jeu.estTourIA()) return;

        if (xSelection == -1) {
            Case c = jeu.plateau.cases[x][y];
            if (c.estVide()) return;
            if (!c.piece.getCouleur().equals(jeu.joueurCourant.couleur)) return;

            effacerSurlignages();
            xSelection = x;
            ySelection = y;
            boutons[x][y].setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(255, 215, 0), 4),
                    BorderFactory.createEmptyBorder(1, 1, 1, 1)
            ));
            afficherCoupsPossibles(x, y);
        } else {
            boolean coupValide = jeu.jouerCoup(xSelection, ySelection, x, y);
            effacerSurlignages();

            if (coupValide) {
                finaliserChangementTour();
            }
            xSelection = -1;
            ySelection = -1;
        }
    }

    private void afficherCoupsPossibles(int xSelection, int ySelection) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (i == xSelection && j == ySelection) continue;

                if (jeu.estCoupValide(xSelection, ySelection, i, j)) {
                    boutons[i][j].setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(80, 220, 140), 4),
                            BorderFactory.createEmptyBorder(1, 1, 1, 1)
                    ));
                }
            }
        }
    }

    private void effacerSurlignages() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                boutons[i][j].setBorder(null);
            }
        }
    }

    private void mettreAJourStatut() {
        String joueur = jeu.joueurCourant == jeu.joueur1
                ? jeu.joueur1.getNom() + " (Blanc)"
                : jeu.joueur2.getNom() + " (Noir)";
        statutLabel.setText("Tour: " + joueur);
    }

    private void demarrerChronoTour() {
        if (partieTerminee) return;

        if (chronoTour != null) {
            chronoTour.stop();
        }

        secondesRestantes = 30;
        timerLabel.setText("Temps: " + secondesRestantes + "s");
        timerLabel.setForeground(new Color(255, 235, 140));

        chronoTour = new Timer(1000, e -> {
            secondesRestantes--;
            timerLabel.setText("Temps: " + secondesRestantes + "s");

            if (secondesRestantes <= 10) {
                timerLabel.setForeground(new Color(255, 120, 120));
            }

            if (secondesRestantes <= 0) {
                chronoTour.stop();
                gererFinTemps();
            }
        });
        chronoTour.start();
    }

    private void gererFinTemps() {
        if (partieTerminee) return;

        String joueurDepasse = jeu.joueurCourant == jeu.joueur1
            ? jeu.joueur1.getNom() + " (Blanc)"
            : jeu.joueur2.getNom() + " (Noir)";
        afficherAlerteTempsEcoule(joueurDepasse);

        if (xSelection != -1) {
            boutons[xSelection][ySelection].setBorder(null);
            xSelection = -1;
            ySelection = -1;
        }

        jeu.passerTour();
        mettreAJourStatut();
        demarrerChronoTour();

        if (jeu.estTourIA()) {
            lancerTourIA();
        }
    }

    private void afficherAlerteTempsEcoule(String joueurDepasse) {
        alerteLabel.setText("Temps ecoule pour " + joueurDepasse + " - tour passe");

        if (alerteTimer != null) {
            alerteTimer.stop();
        }

        alerteTimer = new Timer(1000, e -> alerteLabel.setText(" "));
        alerteTimer.setRepeats(false);
        alerteTimer.start();
    }

    private void finaliserChangementTour() {
        rafraichir();

        Joueur vainqueur = jeu.verifierVainqueur();
        if (vainqueur != null) {
            annoncerVictoire(vainqueur);
            return;
        }

        mettreAJourStatut();
        demarrerChronoTour();

        if (jeu.estTourIA()) {
            lancerTourIA();
        }
    }

    private void lancerTourIA() {
        Timer delaiIA = new Timer(500, e -> {
            if (partieTerminee) return;

            int[] coup = jeu.obtenirCoupIA();
            if (coup != null) {
                jeu.jouerCoup(coup[0], coup[1], coup[2], coup[3]);
            } else {
                jeu.passerTour();
            }
            finaliserChangementTour();
        });
        delaiIA.setRepeats(false);
        delaiIA.start();
    }

    private void annoncerVictoire(Joueur vainqueur) {
        partieTerminee = true;

        if (chronoTour != null) {
            chronoTour.stop();
        }
        if (alerteTimer != null) {
            alerteTimer.stop();
        }

        statutLabel.setText("Partie terminee");
        timerLabel.setText("Temps: --");
        alerteLabel.setText("Victoire de " + vainqueur.getNom() + " !");

        JOptionPane.showMessageDialog(
                this,
                "Victoire de " + vainqueur.getNom() + " !",
                "Victoire",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void rafraichir() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece p = jeu.plateau.cases[i][j].piece;

                if (p == null) {
                    boutons[i][j].setText("");
                    boutons[i][j].setIcon(null);
                } else {
                    boutons[i][j].setText("");
                    boutons[i][j].setIcon(creerIconePiece(p.getCouleur().equals("blanc"), p instanceof Dame));
                }
            }
        }
    }

    private Icon creerIconePiece(boolean estBlanc, boolean estDame) {
        int taille = 58;
        BufferedImage image = new BufferedImage(taille, taille, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color couleurPiece = estBlanc ? new Color(245, 245, 245) : new Color(25, 25, 25);
        Color contour = estBlanc ? new Color(180, 180, 180) : new Color(70, 70, 70);

        // Ombre douce pour donner du volume.
        g2.setColor(new Color(0, 0, 0, 55));
        g2.fillOval(5, 7, 50, 50);

        // Disque principal du pion.
        g2.setColor(couleurPiece);
        g2.fillOval(4, 4, 50, 50);

        // Bordure du pion.
        g2.setColor(contour);
        g2.setStroke(new BasicStroke(3f));
        g2.drawOval(4, 4, 50, 50);

        if (estDame) {
            // Anneau central pour distinguer une dame.
            g2.setColor(estBlanc ? new Color(210, 170, 40) : new Color(255, 215, 90));
            g2.setStroke(new BasicStroke(4f));
            g2.drawOval(16, 16, 26, 26);
        }

        g2.dispose();
        return new ImageIcon(image);
    }
}
