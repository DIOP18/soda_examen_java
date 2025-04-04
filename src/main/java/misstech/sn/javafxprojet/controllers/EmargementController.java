package misstech.sn.javafxprojet.controllers;

import jakarta.persistence.EntityManager;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import misstech.sn.javafxprojet.dao.EmargementImpl;
import misstech.sn.javafxprojet.entities.Cours;
import misstech.sn.javafxprojet.entities.Emargement;
import misstech.sn.javafxprojet.entities.Role;
import misstech.sn.javafxprojet.entities.Users;
import javafx.util.StringConverter;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


public class EmargementController implements Initializable {
    private final EmargementImpl emargement = new EmargementImpl();
    private EntityManagerFactory emf;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final DateTimeFormatter displayTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");


    @FXML
    private ComboBox<Cours> cmbCours;

    @FXML
    private ComboBox<Users> cmbProfesseur;

    @FXML
    private TableColumn<Emargement, String> colCours;

    @FXML
    private TableColumn<Emargement, LocalDate> colDate;

    @FXML
    private TableColumn<Emargement, LocalTime> colHeureActuelle;

    @FXML
    private TableColumn<Emargement, LocalTime> colHeureEmargement;

    @FXML
    private TableColumn<Emargement, Integer> colId;

    @FXML
    private TableColumn<Emargement, String> colProf;

    @FXML
    private DatePicker dtDate;

    @FXML
    private TableView<Emargement> tableEmargement;

    @FXML
    private TextField tfHeureActuelle;

    @FXML
    private TextField tfHeureEmargement;

    @FXML
    private TextField tfHeureFiltre;
    @FXML
    private DatePicker dtDateFiltre;
    private Users connectedUser;

    public void setConnectedUser(Users user) {
        this.connectedUser = user;
        loadConnectedProfesseur();
    }


    @FXML
    private TextField tfId;

    @FXML
    private TextField tfStatut;




    @FXML
    void OnConsulterEmargemment(ActionEvent event) {
        try {
            LocalDate filtreDate = dtDateFiltre.getValue();
            LocalTime filtreHeure = null;
            if (tfHeureFiltre.getText() != null && !tfHeureFiltre.getText().trim().isEmpty()) {
                try {
                    String heureFiltre = formatHeure(tfHeureFiltre.getText().trim());
                    filtreHeure = LocalTime.parse(heureFiltre, DateTimeFormatter.ofPattern("HH:mm:ss"));
                } catch (DateTimeParseException e) {
                    showAlert("Erreur", "Format d'heure invalide ! Utilisez HH:mm (ex: 14:30)", Alert.AlertType.ERROR);
                    return;
                }
            }
            if (filtreHeure == null && filtreDate == null) {
                showAlert("Erreur", "Veuillez spécifier au moins une date ou une heure pour le filtrage", Alert.AlertType.ERROR);
                return;
            }
            List<Emargement> emargements = getEmargements(filtreHeure, filtreDate);
            tableEmargement.getItems().clear();
            tableEmargement.getItems().addAll(emargements);

            if (emargements.isEmpty()) {
                showAlert("Info", "Aucun émargement trouvé pour les critères spécifiés.", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Info", emargements.size() + " émargement(s) trouvé(s).", Alert.AlertType.INFORMATION);
            }
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la consultation : " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    private String formatHeure(String heure) {
        if (heure == null || heure.trim().isEmpty()) {
            return null;
        }

        heure = heure.trim();

        // Si l'heure est au format HH:mm, ajouter les secondes
        if (heure.length() == 5 && heure.contains(":")) {
            return heure + ":00";
        }
        // Si l'heure est juste HH, ajouter minutes et secondes
        else if (heure.length() <= 2 && !heure.contains(":")) {
            return heure + ":00:00";
        }
        return heure;
    }
    @FXML
    void OnReturn(ActionEvent event) {
        try {
            Parent dashboardView = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/pages/ConnectionPage.fxml")));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(dashboardView));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private List<Emargement> getEmargements(LocalTime filtreHeure, LocalDate filtreDate) {
        EntityManager em = emf.createEntityManager();
        List<Emargement> emargements;
        try {
            // La requête de base
            String jpql = "SELECT e FROM Emargement e WHERE 1=1";

            // Ajouter les conditions selon les filtres disponibles
            if (filtreDate != null) {
                jpql += " AND e.date = :date";
            }

            TypedQuery<Emargement> query = em.createQuery(jpql, Emargement.class);

            // Définir les paramètres si nécessaire
            if (filtreDate != null) {
                query.setParameter("date", filtreDate);
            }

            List<Emargement> resultats = query.getResultList();



            // Filtrer par heure si une heure est spécifiée
            if (filtreHeure != null) {
                emargements = resultats.stream()
                        .filter(e -> {
                            if (e.getHeureEmargement() == null) {
                                System.out.println("Émargement avec heure d'émargement nulle trouvé");
                                return false;
                            }

                            long secondsA = e.getHeureEmargement().toSecondOfDay();
                            long secondsB = filtreHeure.toSecondOfDay();
                            boolean matches = Math.abs(secondsA - secondsB) < 900; // 15 minutes

                            System.out.println("Émargement ID: " + e.getId() +
                                    ", Heure d'émargement: " + e.getHeureEmargement() +
                                    ", Heure actuelle: " + e.getHeureActuelle() +
                                    ", Filtre: " + filtreHeure +
                                    ", Différence (sec): " + Math.abs(secondsA - secondsB) +
                                    ", Correspond: " + matches);

                            return matches;
                        })
                        .collect(Collectors.toList());
            } else {
                emargements = resultats;
            }


        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des émargements: " + e.getMessage());
            e.printStackTrace();
            emargements = List.of(); // Liste vide en cas d'erreur
        } finally {
            em.close();
        }
        return emargements;
    }

    private boolean validateInputs() {
        if (cmbProfesseur.getValue() == null) {
            showAlert("VALIDATION", "Veuillez sélectionner un professeur", Alert.AlertType.ERROR);
            return false;
        }

        if (cmbCours.getValue() == null) {
            showAlert("VALIDATION", "Veuillez sélectionner un cours", Alert.AlertType.ERROR);
            return false;
        }

        if (dtDate.getValue() == null) {
            showAlert("VALIDATION", "Veuillez sélectionner une date", Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }


    @FXML
    void onValiderPresence(ActionEvent event) {

        if (!validateInputs()) {
            return;
        }

        LocalTime heureActuelle = LocalTime.now();
        LocalTime heureEmargement = cmbCours.getValue().getHeureDebut();

        if (heureEmargement == null) {
            showAlert("ERREUR", "Heure de début du cours non définie!", Alert.AlertType.ERROR);
            return;
        }

        // Vérifier si un émargement existe déjà pour ce prof/cours/date
        if (checkEmargementExists(cmbProfesseur.getValue(), cmbCours.getValue(), dtDate.getValue())) {
            showAlert("ATTENTION", "Un émargement existe déjà pour ce professeur, ce cours et cette date!",
                    Alert.AlertType.WARNING);
            return;
        }

        // Vérifier si le professeur est en retard
        if (heureActuelle.isAfter(heureEmargement.plusMinutes(10))) {
            showAlert("ATTENTION", "Vous êtes en retard de plus de 10 minutes!",
                    Alert.AlertType.WARNING);
            return;
        }

        Emargement nouveauEmarge = new Emargement(
                dtDate.getValue(),
                tfStatut.getText(),
                cmbProfesseur.getValue(),
                cmbCours.getValue(),
                heureActuelle,  // Heure actuelle réelle
                heureEmargement // Heure de début du cours
        );

        try {
            emargement.add(nouveauEmarge);
            showAlert("VALIDATION", "Présence validée avec succès", Alert.AlertType.CONFIRMATION);
            // Rafraîchir le tableau après ajout
            refreshTableView();
        } catch (Exception e) {
            showAlert("ERREUR", "Erreur lors de l'enregistrement: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }

    }

    private void refreshTableView() {
        try {
            LocalDate filtreDate = dtDateFiltre != null ? dtDateFiltre.getValue() : null;
            LocalTime filtreHeure = null;

            if (tfHeureFiltre != null && !tfHeureFiltre.getText().trim().isEmpty()) {
                try {
                    String heureFiltre = formatHeure(tfHeureFiltre.getText().trim());
                    filtreHeure = LocalTime.parse(heureFiltre);
                } catch (DateTimeParseException e) {
                    // En cas d'erreur de parsing, on ignore le filtre d'heure
                    System.err.println("Erreur de parsing d'heure: " + e.getMessage());
                }
            }

            // Si des filtres sont actifs, les appliquer
            if (filtreHeure != null || filtreDate != null) {
                List<Emargement> emargements = getEmargements(filtreHeure, filtreDate);
                tableEmargement.getItems().clear();
                tableEmargement.getItems().addAll(emargements);
            } else {
                // Sinon, charger tous les émargements
                loadAllEmargements();
            }
        } catch (Exception e) {
            e.printStackTrace();
            // En cas d'erreur, essayer de charger tous les émargements
            loadAllEmargements();
        }
    }
    private void loadAllEmargements() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Emargement> query = em.createQuery("SELECT e FROM Emargement e ORDER BY e.date DESC, e.heureActuelle DESC", Emargement.class);
            List<Emargement> emargements = query.getResultList();
            tableEmargement.getItems().clear();
            tableEmargement.getItems().addAll(emargements);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
    private boolean checkEmargementExists(Users prof, Cours cours, LocalDate date) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PERSISTENCE");
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(e) FROM Emargement e WHERE e.professeur.id = :profId AND e.cours.id = :coursId AND e.date = :date",
                    Long.class);
            query.setParameter("profId", prof.getId());
            query.setParameter("coursId", cours.getId());
            query.setParameter("date", date);

            return query.getSingleResult() > 0;
        } finally {
            em.close();
            emf.close();
        }
    }
    public void loadConnectedProfesseur() {
        // Si l'utilisateur n'est pas encore initialisé, ne rien faire
        if (connectedUser == null) return;

        // Créer une liste avec uniquement le professeur connecté
        cmbProfesseur.getItems().clear();
        cmbProfesseur.getItems().add(connectedUser);

        // Configurer l'affichage comme vous le faites déjà dans loadProfesseur()
        cmbProfesseur.setCellFactory(param -> new ListCell<Users>() {
            @Override
            protected void updateItem(Users item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNom() + " " + item.getPrenom());
                }
            }
        });

        cmbProfesseur.setConverter(new StringConverter<Users>() {
            @Override
            public String toString(Users user) {
                return user == null ? null : user.getNom() + " " + user.getPrenom();
            }

            @Override
            public Users fromString(String string) {
                return null;
            }
        });

        // Sélectionner automatiquement le professeur connecté
        cmbProfesseur.setValue(connectedUser);

        // Charger les cours pour ce professeur
        loadCoursForProfesseur(connectedUser);
    }

    private void setupCoursComboBox() {
        cmbCours.setCellFactory(param -> new ListCell<Cours>() {
            @Override
            protected void updateItem(Cours item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNom());
                }
            }
        });

        cmbCours.setConverter(new StringConverter<Cours>() {
            @Override
            public String toString(Cours cours) {
                return cours == null ? null : cours.getNom();
            }

            @Override
            public Cours fromString(String string) {
                return null;
            }
        });

        cmbCours.setOnAction(event -> {
            Cours selectedCours = cmbCours.getValue();
            if (selectedCours != null) {
                // Mettre à jour l'heure d'émargement (heure de début du cours)
                LocalTime heureDebut = selectedCours.getHeureDebut();
                if (heureDebut != null) {
                    tfHeureEmargement.setText(heureDebut.format(timeFormatter));
                } else {
                    tfHeureEmargement.setText("");
                }
            } else {
                tfHeureEmargement.setText("");
            }
        });
    }
    private void loadCoursForProfesseur(Users professeur) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PERSISTENCE");
        EntityManager em = emf.createEntityManager();
        try {
            cmbCours.getItems().clear();
            TypedQuery<Cours> query = em.createQuery(
                    "SELECT c FROM Cours c WHERE c.professeur.id = :profId", Cours.class);
            query.setParameter("profId", professeur.getId());
            List<Cours> coursProf = query.getResultList();
            cmbCours.getItems().addAll(coursProf);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement des cours: " + e.getMessage(), Alert.AlertType.ERROR);
        } finally {
            em.close();
            emf.close();
        }
    }




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        emf = Persistence.createEntityManagerFactory("PERSISTENCE");
        // Dans la méthode initialize(), ajoutez:
        if (dtDateFiltre != null) {
            // Par défaut, pas de date sélectionnée pour le filtre
            dtDateFiltre.setValue(null);

            // Listener pour rafraîchir automatiquement quand on change la date
            dtDateFiltre.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (oldVal != newVal) {
                    refreshTableView();
                }
            });
        }
        tfHeureFiltre.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));

// Ajoutez un écouteur pour formatter automatiquement lors de la perte de focus
        tfHeureFiltre.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {  // Quand le champ perd le focus
                try {
                    String texte = tfHeureFiltre.getText().trim();
                    if (!texte.isEmpty()) {
                        // Essayer de parser et reformatter
                        LocalTime time = LocalTime.parse(formatHeure(texte));
                        tfHeureFiltre.setText(time.format(DateTimeFormatter.ofPattern("HH:mm")));
                    }
                } catch (Exception e) {
                    // Garder le texte tel quel en cas d'erreur
                }
            }
        });
        tableEmargement.getItems().clear();
        colId.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getId()));
        colDate.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getDate()));
        colProf.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getProfesseur().getNom() + " " + data.getValue().getProfesseur().getPrenom()));
        colCours.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCours().getNom()));
        colHeureEmargement.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getHeureEmargement()));
        colHeureActuelle.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getHeureActuelle()));
        dtDate.setValue(LocalDate.now());
        tfStatut.setText("Present");
        LocalTime heureActuelle = LocalTime.now();
        tfHeureActuelle.setText(heureActuelle.format(timeFormatter));
        dtDate.setEditable(false);
        setupCoursComboBox();

        tfHeureFiltre.setOnAction(event -> OnConsulterEmargemment(event));
        loadAllEmargements();
        System.out.println("Initialisation de l'interface d'émargement terminée");
    }
}