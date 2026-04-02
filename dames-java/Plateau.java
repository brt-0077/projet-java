public class Plateau {
    Case[][] cases = new Case[8][8];

    public Plateau() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                cases[i][j] = new Case(i, j);
            }
        }
        initialiser();
    }

    public void initialiser() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i + j) % 2 == 1) {
                    cases[i][j].piece = new Pion("noir");
                }
            }
        }

        for (int i = 5; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i + j) % 2 == 1) {
                    cases[i][j].piece = new Pion("blanc");
                }
            }
        }
    }
}
