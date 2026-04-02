public class Case {
    int x, y;
    Piece piece;

    public Case(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean estVide() {
        return piece == null;
    }
}
