package misstech.sn.javafxprojet.entities;

import jakarta.persistence.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;


@Entity
@Table(name = "cours")
public class Cours {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "description")
    private String description;

    @Column(name = "heure_debut", nullable = false)
    private LocalTime heureDebut;

    @Column(name = "heure_fin", nullable = false)
    private LocalTime heureFin;

    @ManyToOne
    @JoinColumn(name = "salle_id", nullable = false)
    private Salle salle;
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "professeur_id")
    private Users professeur;
    @Column(name = "date_cours", nullable = false)
    private LocalDate date;

    // Constructeurs
    public Cours() {}

    public Cours(String nom, String description, LocalTime heureDebut, LocalTime heureFin, Salle salle, LocalDate date) {
        this.nom = nom;
        this.description = description;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.salle = salle;
        this.date = date;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public Users getProfesseur() {
        return professeur;
    }
    public void setProfesseur(Users professeur) {
        this.professeur = professeur;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalTime getHeureDebut() {
        return heureDebut;
    }

    public void setHeureDebut(LocalTime heureDebut) {
        this.heureDebut = heureDebut;
    }

    public LocalTime getHeureFin() {
        return heureFin;
    }

    public void setHeureFin(LocalTime heureFin) {
        this.heureFin = heureFin;
    }

    public Salle getSalle() {
        return salle;
    }

    public void setSalle(Salle salle) {
        this.salle = salle;
    }


    @Override
    public String toString() {
        return "Cours{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", heureDebut=" + heureDebut +
                ", heureFin=" + heureFin +
                ", salle=" + salle +
                '}';
    }
    public long getDuree() {
        return Duration.between(heureDebut, heureFin).toMinutes();
    }
    public boolean isValidDuration() {
        return getDuree() > 0 && getDuree() <= 240;
    }


}
