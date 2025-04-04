package misstech.sn.javafxprojet.entities;

public class CoursPresenceStats {
    private int coursId;
    private String coursNom;
    private int profId;
    private String profNom;
    private String profPrenom;
    private double tauxPresence;
    private long totalSeances;
    private long presences;
    public CoursPresenceStats(int coursId, String coursNom, int profId, String profNom,
                              String profPrenom, double tauxPresence, long totalSeances, long presences) {
        this.coursId = coursId;
        this.coursNom = coursNom;
        this.profId = profId;
        this.profNom = profNom;
        this.profPrenom = profPrenom;
        this.tauxPresence = tauxPresence;
        this.totalSeances = totalSeances;
        this.presences = presences;
    }

    public int getCoursId() {
        return coursId;
    }

    public String getCoursNom() {
        return coursNom;
    }

    public int getProfId() {
        return profId;
    }

    public String getProfNom() {
        return profNom;
    }

    public String getProfPrenom() {
        return profPrenom;
    }

    public double getTauxPresence() {
        return tauxPresence;
    }

    public long getTotalSeances() {
        return totalSeances;
    }

    public long getPresences() {
        return presences;
    }

    public void setCoursId(int coursId) {
        this.coursId = coursId;
    }

    public void setCoursNom(String coursNom) {
        this.coursNom = coursNom;
    }

    public void setProfId(int profId) {
        this.profId = profId;
    }

    public void setProfNom(String profNom) {
        this.profNom = profNom;
    }

    public void setProfPrenom(String profPrenom) {
        this.profPrenom = profPrenom;
    }

    public void setTauxPresence(double tauxPresence) {
        this.tauxPresence = tauxPresence;
    }

    public void setTotalSeances(long totalSeances) {
        this.totalSeances = totalSeances;
    }

    public void setPresences(long presences) {
        this.presences = presences;
    }
    public String getProfesseurNomComplet() {
        return profNom + " " + profPrenom;
    }

    public String getCoursLabel() {
        return coursNom + " (" + getProfesseurNomComplet() + ")";
    }
}
