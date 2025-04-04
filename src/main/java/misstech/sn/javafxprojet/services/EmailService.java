package misstech.sn.javafxprojet.services;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

public class EmailService {
    public static void envoyerEmail(String destinataire, String sujet, String contenu) {
        final String expediteur = "sodadiop2004@gmail.com"; // Remplace par ton adresse Gmail
        final String motDePasse = "yjmwqrgskstkbumm"; // Utilise le mot de passe d'application ici

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(expediteur, motDePasse);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(expediteur));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinataire));
            message.setSubject(sujet);

            // Créer un message multipart pour pouvoir ajouter du contenu HTML
            MimeMultipart multipart = new MimeMultipart();

            // Créer une partie avec le contenu HTML
            BodyPart messageBodyPart = new MimeBodyPart();
            String contenuHtml = "<html><body>" + contenu + "</body></html>";
            messageBodyPart.setContent(contenuHtml, "text/html");

            // Ajouter la partie au multipart
            multipart.addBodyPart(messageBodyPart);

            // Mettre le message multipart comme contenu de l'email
            message.setContent(multipart);

            // Envoyer l'e-mail
            Transport.send(message);
            System.out.println("E-mail envoyé avec succès !");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
