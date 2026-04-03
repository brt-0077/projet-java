import java.util.ArrayList;
import java.util.Random;

public class IA {
    private Random random = new Random();

    public int[] jouerCoup(Jeu jeu) {
        ArrayList<int[]> coupsValides = getCoupsValides(jeu);
        
        if (coupsValides.isEmpty()) {
            return null; // Pas de coups valides
        }
        
        // Choix aléatoire avec préférence pour les mauvaises captures
        int[] meilleurCoup = coupsValides.get(0);
        int maxCaptures = 0;

        for (int[] coup : coupsValides) {
            int captures = compterCaptures(jeu, coup[0], coup[1], coup[2], coup[3]);
            if (captures > maxCaptures) {
                maxCaptures = captures;
                meilleurCoup = coup;
            } else if (captures == maxCaptures && random.nextDouble() < 0.3) {
                meilleurCoup = coup;
            }
        }

        return meilleurCoup;
    }

    private ArrayList<int[]> getCoupsValides(Jeu jeu) {
        ArrayList<int[]> coups = new ArrayList<>();
        
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Case depart = jeu.plateau.cases[i][j];
                
                if (depart.estVide() || !depart.piece.getCouleur().equals("noir")) {
                    continue;
                }
                
                Piece p = depart.piece;
                if (p instanceof Pion) {
                    // Deplacements simples et captures en saut.
                    int[][] deltas = {{1, -1}, {1, 1}, {-1, -1}, {-1, 1}, {2, -2}, {2, 2}, {-2, -2}, {-2, 2}};
                    for (int[] d : deltas) {
                        int newX = i + d[0];
                        int newY = j + d[1];
                        if (newX >= 0 && newX < 8 && newY >= 0 && newY < 8) {
                            if (jeu.estCoupValide(i, j, newX, newY)) {
                                coups.add(new int[]{i, j, newX, newY});
                            }
                        }
                    }
                } else {
                    // Une dame peut viser toutes les cases d'une diagonale.
                    int[][] directions = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
                    for (int[] dir : directions) {
                        int newX = i + dir[0];
                        int newY = j + dir[1];
                        while (newX >= 0 && newX < 8 && newY >= 0 && newY < 8) {
                            if (jeu.estCoupValide(i, j, newX, newY)) {
                                coups.add(new int[]{i, j, newX, newY});
                            }
                            newX += dir[0];
                            newY += dir[1];
                        }
                    }
                }
            }
        }
        
        return coups;
    }

    private int compterCaptures(Jeu jeu, int x1, int y1, int x2, int y2) {
        Piece p = jeu.plateau.cases[x1][y1].piece;
        if (p == null) return 0;

        if (p instanceof Pion) {
            return (Math.abs(x2 - x1) == 2 && Math.abs(y2 - y1) == 2) ? 1 : 0;
        }

        int captures = 0;
        int dx = (x2 > x1) ? 1 : -1;
        int dy = (y2 > y1) ? 1 : -1;
        int x = x1 + dx;
        int y = y1 + dy;

        while (x != x2 && y != y2) {
            Piece piece = jeu.plateau.cases[x][y].piece;
            if (piece != null && piece.getCouleur().equals("blanc")) {
                captures++;
            }
            x += dx;
            y += dy;
        }

        return captures;
    }
}
