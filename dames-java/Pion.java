public class Pion extends Piece {

    public Pion(String couleur) {
        super(couleur);
    }

    @Override
    public boolean mouvementValide(int x1, int y1, int x2, int y2) {
        int direction = couleur.equals("blanc") ? -1 : 1;

        // Un pion avance d'une ligne dans sa direction et d'une colonne en diagonale.
        return (x2 - x1) == direction && Math.abs(y2 - y1) == 1;
    }
}
