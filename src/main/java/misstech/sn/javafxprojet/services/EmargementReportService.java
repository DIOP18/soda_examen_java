package misstech.sn.javafxprojet.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import misstech.sn.javafxprojet.entities.CoursPresenceStats;
import misstech.sn.javafxprojet.entities.Emargement;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;


import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;



public class EmargementReportService {

    private EntityManager entityManager;

    public EmargementReportService() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PERSISTENCE");
        this.entityManager = emf.createEntityManager();
    }

    private List<Emargement> getEmargementsByDateRange(LocalDate dateDebut, LocalDate dateFin) {
        return entityManager.createQuery(
                        "SELECT e FROM Emargement e WHERE e.date BETWEEN :dateDebut AND :dateFin ORDER BY e.date",
                        Emargement.class)
                .setParameter("dateDebut", dateDebut)
                .setParameter("dateFin", dateFin)
                .getResultList();
    }

    public void exportEmargementToExcel(LocalDate dateDebut, LocalDate dateFin, String outputPath) throws IOException {
        List<Emargement> emargements = getEmargementsByDateRange(dateDebut, dateFin);
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Émargements");

        // Création des en-têtes
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Date");
        headerRow.createCell(1).setCellValue("Cours");
        headerRow.createCell(2).setCellValue("Professeur");
        headerRow.createCell(3).setCellValue("Statut");
        headerRow.createCell(4).setCellValue("Heure début cours");
        headerRow.createCell(5).setCellValue("Heure fin cours");
        headerRow.createCell(6).setCellValue("Heure émargement");
        int rowNum = 1;
        for (Emargement emargement : emargements) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(emargement.getDate().toString());
            row.createCell(1).setCellValue(emargement.getCours().getNom());
            row.createCell(2).setCellValue(emargement.getProfesseur().getNom() + " " + emargement.getProfesseur().getPrenom());
            row.createCell(3).setCellValue(emargement.getStatut());
            row.createCell(4).setCellValue(emargement.getCours().getHeureDebut().toString());
            row.createCell(5).setCellValue(emargement.getCours().getHeureFin().toString());
            row.createCell(6).setCellValue(emargement.getHeureEmargement() != null ? emargement.getHeureEmargement().toString() : "N/A");
        }
        for (int i = 0; i < 7; i++) {
            sheet.autoSizeColumn(i);
        }

        // Écriture du fichier
        try (FileOutputStream fileOut = new FileOutputStream(outputPath)) {
            workbook.write(fileOut);
        }
        workbook.close();
    }

    public void exportEmargementToPDF(LocalDate dateDebut, LocalDate dateFin, String outputPath) throws Exception {
        List<Emargement> emargements = getEmargementsByDateRange(dateDebut, dateFin);

        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, new FileOutputStream(outputPath));
        document.open();
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
        Paragraph title = new Paragraph("MISSTECH SCHOOL: Rapport d'émargements PDF du " + dateDebut + " au " + dateFin, titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" ")); // Espace

        // Création du tableau
        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        String[] headers = {"Date", "Cours", "Professeur", "Statut", "Heure début", "Heure fin", "Heure émargement"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
        }
        for (Emargement emargement : emargements) {
            table.addCell(emargement.getDate().toString());
            table.addCell(emargement.getCours().getNom());
            table.addCell(emargement.getProfesseur().getNom() + " " + emargement.getProfesseur().getPrenom());
            table.addCell(emargement.getStatut());
            table.addCell(emargement.getCours().getHeureDebut().toString());
            table.addCell(emargement.getCours().getHeureFin().toString());
            table.addCell(emargement.getHeureEmargement() != null ? emargement.getHeureEmargement().toString() : "N/A");
        }

        document.add(table);
        document.close();
    }

    public List<CoursPresenceStats> getPresenceStatsByProfessor() {
        try {
            // Vérifier si l'EntityManager est ouvert
            if (!entityManager.isOpen()) {
                System.out.println("EntityManager est fermé. Création d'un nouveau...");
            }

            // Modifier la requête pour afficher les valeurs de statut pour debug
            String debugJpql = "SELECT DISTINCT e.statut FROM Emargement e";
            List<String> statusValues = entityManager.createQuery(debugJpql, String.class).getResultList();
            System.out.println("Valeurs de statut dans la base: " + statusValues);

            // Requête JPQL modifiée - utiliser UPPER pour ignorer la casse
            String jpql = "SELECT c.id, c.nom, p.id, p.nom, p.prenom, " +
                    "COUNT(e) as totalSeances, " +
                    "SUM(CASE WHEN UPPER(e.statut) = 'PRESENT' THEN 1 ELSE 0 END) as presences " +
                    "FROM Emargement e " +
                    "JOIN e.cours c " +
                    "JOIN e.professeur p " +
                    "GROUP BY c.id, c.nom, p.id, p.nom, p.prenom";

            // Exécuter la requête avec journalisation
            System.out.println("Exécution de la requête JPQL pour les statistiques de présence...");
            List<Object[]> results = entityManager.createQuery(jpql).getResultList();
            System.out.println("Nombre de résultats trouvés: " + results.size());

            // Traiter les résultats
            List<CoursPresenceStats> stats = new ArrayList<>();
            for (Object[] result : results) {
                try {
                    // Caster avec précaution
                    int coursId = ((Number) result[0]).intValue();
                    String coursNom = (String) result[1];
                    int profId = ((Number) result[2]).intValue();
                    String profNom = (String) result[3];
                    String profPrenom = (String) result[4];
                    long totalSeances = ((Number) result[5]).longValue();
                    long presences = ((Number) result[6]).longValue();

                    // Debug des valeurs avant calcul
                    System.out.println("Valeurs brutes - Cours: " + coursNom +
                            " - Total: " + totalSeances +
                            " - Présences: " + presences);

                    // Calculer le taux de présence en forçant l'arithmétique à virgule flottante
                    double tauxPresence = totalSeances > 0 ? (presences * 100.0) / totalSeances : 0;

                    // Créer et ajouter l'objet de statistiques
                    stats.add(new CoursPresenceStats(coursId, coursNom, profId, profNom, profPrenom,
                            tauxPresence, totalSeances, presences));

                    // Journalisation des données
                    System.out.println("Statistique ajoutée: " + coursNom + " - Prof: " + profNom +
                            " - Taux: " + tauxPresence + "% - Présences: " + presences +
                            "/" + totalSeances);
                } catch (Exception e) {
                    System.err.println("Erreur lors du traitement d'un résultat: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            System.out.println("Traitement terminé. Nombre de statistiques: " + stats.size());
            return stats;
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des statistiques: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>(); // Retourner une liste vide en cas d'erreur
        }
    }

    public ObservableList<Emargement> getAllEmargements() {
        ObservableList<Emargement> ListEmarge = FXCollections.observableArrayList();
        entityManager.getTransaction().begin();
        List<Emargement> resultat = entityManager.createQuery("FROM Emargement", Emargement.class).getResultList();
        entityManager.getTransaction().commit();
        ListEmarge.addAll(resultat);
        return ListEmarge;

    }
}
