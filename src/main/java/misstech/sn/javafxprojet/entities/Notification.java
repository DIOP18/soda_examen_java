package misstech.sn.javafxprojet.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")

    private int id;

    @Column(name = "message", nullable = false)
    private String message;

    @ManyToOne
    @JoinColumn(name = "destinataire_id", nullable = false)
    private Users destinataire; 

    @Column(name = "date_envoi", nullable = false)
    private LocalDateTime dateEnvoi;

    // Constructeurs
    public Notification() {}

    public Notification(String message, Users destinataire, LocalDateTime dateEnvoi) {
        this.message = message;
        this.destinataire = destinataire;
        this.dateEnvoi = dateEnvoi;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Users getDestinataire() {
        return destinataire;
    }

    public void setDestinataire(Users destinataire) {
        this.destinataire = destinataire;
    }

    public LocalDateTime getDateEnvoi() {
        return dateEnvoi;
    }

    public void setDateEnvoi(LocalDateTime dateEnvoi) {
        this.dateEnvoi = dateEnvoi;
    }
}
