import java.util.ArrayList;

public class Jeu {

    Plateau plateau;
    Joueur joueur1, joueur2;
    Joueur joueurCourant;
    ArrayList<Coup> historique = new ArrayList<>();
    IA ia;
    boolean modePvIA = false;

    public Jeu() {
        plateau = new Plateau();
        joueur1 = new Joueur("Joueur 1", "blanc");
        joueur2 = new Joueur("Joueur 2", "noir");
        joueurCourant = joueur1;
    }

    public Jeu(boolean avecIA) {
        plateau = new Plateau();
        joueur1 = new Joueur("Joueur", "blanc");
        joueur2 = new Joueur("IA", "noir");
        joueurCourant = joueur1;
        this.modePvIA = avecIA;
        if (avecIA) {
            this.ia = new IA();
        }
    }

    public Jeu(boolean avecIA, String nomJoueur1, String nomJoueur2) {
        plateau = new Plateau();
        String n1 = (nomJoueur1 == null || nomJoueur1.trim().isEmpty()) ? "Joueur 1" : nomJoueur1.trim();
        String n2 = (nomJoueur2 == null || nomJoueur2.trim().isEmpty()) ? (avecIA ? "IA" : "Joueur 2") : nomJoueur2.trim();

        joueur1 = new Joueur(n1, "blanc");
        joueur2 = new Joueur(avecIA ? "IA" : n2, "noir");
        joueurCourant = joueur1;
        this.modePvIA = avecIA;
        if (avecIA) {
            this.ia = new IA();
        }
    }

    public boolean jouerCoup(int x1, int y1, int x2, int y2) {
        if (!estDansPlateau(x1, y1) || !estDansPlateau(x2, y2)) return false;

        Case depart = plateau.cases[x1][y1];
        Case arrivee = plateau.cases[x2][y2];

        if (depart.estVide()) return false;
        if (!arrivee.estVide()) return false;

        Piece p = depart.piece;

        if (!p.getCouleur().equals(joueurCourant.couleur)) return false;

        if (!estCoupValide(x1, y1, x2, y2)) return false;

        if (estCapture(p, x1, y1, x2, y2)) {
            supprimerPieceCapturee(p, x1, y1, x2, y2);
        }

        arrivee.piece = p;
        depart.piece = null;
        promouvoirSiNecessaire(x2, y2);

        historique.add(new Coup(x1, y1, x2, y2));
        changerJoueur();
        return true;
    }

    public boolean estCoupValide(int x1, int y1, int x2, int y2) {
        if (!estDansPlateau(x1, y1) || !estDansPlateau(x2, y2)) return false;

        Case depart = plateau.cases[x1][y1];
        Case arrivee = plateau.cases[x2][y2];

        if (depart.estVide() || !arrivee.estVide()) return false;

        Piece p = depart.piece;
        if (!p.getCouleur().equals(joueurCourant.couleur)) return false;

        if (p instanceof Pion) {
            return estCoupPionValide((Pion) p, x1, y1, x2, y2);
        }
        return estCoupDameValide(x1, y1, x2, y2);
    }

    private boolean estCoupPionValide(Pion pion, int x1, int y1, int x2, int y2) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        int direction = pion.getCouleur().equals("blanc") ? -1 : 1;

        // Deplacement simple.
        if (dx == direction && Math.abs(dy) == 1) {
            return true;
        }

        // Capture par saut d'une case adverse.
        if (Math.abs(dx) == 2 && Math.abs(dy) == 2) {
            int mx = x1 + dx / 2;
            int my = y1 + dy / 2;
            Piece milieu = plateau.cases[mx][my].piece;
            return milieu != null && !milieu.getCouleur().equals(pion.getCouleur());
        }

        return false;
    }

    private boolean estCoupDameValide(int x1, int y1, int x2, int y2) {
        int dx = x2 - x1;
        int dy = y2 - y1;

        if (Math.abs(dx) != Math.abs(dy)) return false;

        int sx = dx > 0 ? 1 : -1;
        int sy = dy > 0 ? 1 : -1;
        int x = x1 + sx;
        int y = y1 + sy;

        int ennemis = 0;
        while (x != x2 && y != y2) {
            Piece piece = plateau.cases[x][y].piece;
            if (piece != null) {
                if (piece.getCouleur().equals(joueurCourant.couleur)) return false;
                ennemis++;
                if (ennemis > 1) return false;
            }
            x += sx;
            y += sy;
        }

        return true;
    }

    private boolean estCapture(Piece p, int x1, int y1, int x2, int y2) {
        if (p instanceof Pion) {
            return Math.abs(x2 - x1) == 2 && Math.abs(y2 - y1) == 2;
        }

        int dx = x2 - x1;
        int dy = y2 - y1;
        int sx = dx > 0 ? 1 : -1;
        int sy = dy > 0 ? 1 : -1;
        int x = x1 + sx;
        int y = y1 + sy;

        while (x != x2 && y != y2) {
            Piece piece = plateau.cases[x][y].piece;
            if (piece != null && !piece.getCouleur().equals(p.getCouleur())) {
                return true;
            }
            x += sx;
            y += sy;
        }
        return false;
    }

    private void supprimerPieceCapturee(Piece p, int x1, int y1, int x2, int y2) {
        if (p instanceof Pion) {
            int mx = (x1 + x2) / 2;
            int my = (y1 + y2) / 2;
            plateau.cases[mx][my].piece = null;
            return;
        }

        int dx = x2 - x1;
        int dy = y2 - y1;
        int sx = dx > 0 ? 1 : -1;
        int sy = dy > 0 ? 1 : -1;
        int x = x1 + sx;
        int y = y1 + sy;

        while (x != x2 && y != y2) {
            Piece piece = plateau.cases[x][y].piece;
            if (piece != null && !piece.getCouleur().equals(p.getCouleur())) {
                plateau.cases[x][y].piece = null;
                return;
            }
            x += sx;
            y += sy;
        }
    }

    private void promouvoirSiNecessaire(int x, int y) {
        Piece p = plateau.cases[x][y].piece;
        if (!(p instanceof Pion)) return;

        if (p.getCouleur().equals("blanc") && x == 0) {
            plateau.cases[x][y].piece = new Dame("blanc");
        } else if (p.getCouleur().equals("noir") && x == 7) {
            plateau.cases[x][y].piece = new Dame("noir");
        }
    }

    private boolean estDansPlateau(int x, int y) {
        return x >= 0 && x < 8 && y >= 0 && y < 8;
    }

    private void changerJoueur() {
        joueurCourant = (joueurCourant == joueur1) ? joueur2 : joueur1;
    }

    public boolean estTourIA() {
        return modePvIA && joueurCourant == joueur2;
    }

    public int[] obtenirCoupIA() {
        if (ia != null) {
            return ia.jouerCoup(this);
        }
        return null;
    }

    public void passerTour() {
        changerJoueur();
    }

    public Joueur verifierVainqueur() {
        boolean blancAEncorePieces = couleurAPieces("blanc");
        boolean noirAEncorePieces = couleurAPieces("noir");

        if (!blancAEncorePieces) return joueur2;
        if (!noirAEncorePieces) return joueur1;

        boolean blancPeutJouer = couleurACoupsDisponibles("blanc");
        boolean noirPeutJouer = couleurACoupsDisponibles("noir");

        if (!blancPeutJouer) return joueur2;
        if (!noirPeutJouer) return joueur1;

        return null;
    }

    private boolean couleurAPieces(String couleur) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece p = plateau.cases[i][j].piece;
                if (p != null && p.getCouleur().equals(couleur)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean couleurACoupsDisponibles(String couleur) {
        for (int x1 = 0; x1 < 8; x1++) {
            for (int y1 = 0; y1 < 8; y1++) {
                Piece p = plateau.cases[x1][y1].piece;
                if (p == null || !p.getCouleur().equals(couleur)) continue;

                for (int x2 = 0; x2 < 8; x2++) {
                    for (int y2 = 0; y2 < 8; y2++) {
                        if (estCoupValidePourCouleur(couleur, x1, y1, x2, y2)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean estCoupValidePourCouleur(String couleur, int x1, int y1, int x2, int y2) {
        if (!estDansPlateau(x1, y1) || !estDansPlateau(x2, y2)) return false;

        Case depart = plateau.cases[x1][y1];
        Case arrivee = plateau.cases[x2][y2];
        if (depart.estVide() || !arrivee.estVide()) return false;

        Piece p = depart.piece;
        if (!p.getCouleur().equals(couleur)) return false;

        if (p instanceof Pion) {
            return estCoupPionValide((Pion) p, x1, y1, x2, y2);
        }
        return estCoupDameValidePourCouleur(couleur, x1, y1, x2, y2);
    }

    private boolean estCoupDameValidePourCouleur(String couleur, int x1, int y1, int x2, int y2) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        if (Math.abs(dx) != Math.abs(dy)) return false;

        int sx = dx > 0 ? 1 : -1;
        int sy = dy > 0 ? 1 : -1;
        int x = x1 + sx;
        int y = y1 + sy;
        int ennemis = 0;

        while (x != x2 && y != y2) {
            Piece piece = plateau.cases[x][y].piece;
            if (piece != null) {
                if (piece.getCouleur().equals(couleur)) return false;
                ennemis++;
                if (ennemis > 1) return false;
            }
            x += sx;
            y += sy;
        }
        return true;
    }
}
