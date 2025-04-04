package misstech.sn.javafxprojet.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "emargements")
public class Emargement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")

    private int id;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "statut", nullable = false)
    private String statut;

    @ManyToOne
    @JoinColumn(name = "professeur_id", nullable = false)
    private Users professeur;

    @ManyToOne
    @JoinColumn(name = "cours_id", nullable = false)
    private Cours cours;
    @Column(name = "heure_actuelle")
    private LocalTime heureActuelle;

    @Column(name = "heure_emargement")
    private LocalTime heureEmargement;

    // Constructeurs
    public Emargement() {}



    public Emargement(LocalDate date, String statut, Users professeur, Cours cours, LocalTime heureActuelle, LocalTime heureEmargement) {
        this.date = date;
        this.statut = statut;
        this.professeur = professeur;
        this.cours = cours;
        this.heureActuelle = heureActuelle;
        this.heureEmargement = heureEmargement;
    }


    @Override
    public String toString() {
        return "Emargement{" +
                "id=" + id +
                ", date=" + date +
                ", statut='" + statut + '\'' +
                ", professeur=" + professeur +
                ", cours=" + cours +
                ", heureActuelle=" + heureActuelle +
                ", heureEmargement=" + heureEmargement +
                '}';
    }

    public LocalTime getHeureActuelle() { return heureActuelle; }
    public void setHeureActuelle(LocalTime heureActuelle) { this.heureActuelle = heureActuelle; }

    public LocalTime getHeureEmargement() { return heureEmargement; }
    public void setHeureEmargement(LocalTime heureEmargement) { this.heureEmargement = heureEmargement; }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Users getProfesseur() {
        return professeur;
    }

    public void setProfesseur(Users professeur) {
        this.professeur = professeur;
    }

    public Cours getCours() {
        return cours;
    }

    public void setCours(Cours cours) {
        this.cours = cours;
    }
}
