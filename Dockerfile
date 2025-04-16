# Utiliser une image avec Java 21
FROM eclipse-temurin:21-jdk

# Installation des dépendances nécessaires pour JavaFX
RUN apt-get update && apt-get install -y \
    libgl1 \
    libx11-6 \
    libxext6 \
    libxtst6 \
    libxrender1 \
    libxi6 \
    libfreetype6 \
    libfontconfig1 \
    x11-apps \
    && rm -rf /var/lib/apt/lists/*

# Définir le répertoire de travail
WORKDIR /app

# Copier le JAR généré dans le conteneur
COPY target/javafx-projet-1.0-SNAPSHOT.jar app.jar

# Configuration pour éviter les problèmes avec JavaFX dans Docker
ENV _JAVA_OPTIONS="-Djava.awt.headless=false -Dtestfx.robot=glass -Dtestfx.headless=true -Dprism.order=sw"

# Commande pour exécuter l'application
CMD ["java", "-jar", "app.jar"]