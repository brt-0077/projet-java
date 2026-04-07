public class Joueur {
    String nom;
    String couleur;
    int points;

    public Joueur(String nom, String couleur) {
        this.nom = nom;
        this.couleur = couleur;
        this.points = 0;
    }

    public String getNom() {
        return nom;
    }

    public String getCouleur() {
        return couleur;
    }

    public int getPoints() {
        return points;
    }

    public void ajouterPoints(int valeur) {
        if (valeur > 0) {
            points += valeur;
        }
    }
}
