public class Dame extends Piece {

    public Dame(String couleur) {
        super(couleur);
    }

    @Override
    public boolean mouvementValide(int x1, int y1, int x2, int y2) {
        return Math.abs(x2 - x1) == Math.abs(y2 - y1);
    }
}
