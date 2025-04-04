module misstech.sn.javafxprojet {
    requires javafx.controls;
    requires javafx.fxml;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires java.sql;
    requires com.google.api.client;
    requires com.google.api.client.auth;
    requires com.google.api.client.json.gson;
    requires com.google.api.client.extensions.java6.auth;
    requires com.google.api.client.extensions.jetty.auth;
    requires com.google.api.services.gmail;
    requires com.google.auth;
    requires com.google.auth.oauth2;
    requires java.mail;
    requires google.api.client;
    requires jdk.httpserver;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires org.apache.pdfbox;
    requires itextpdf;
    requires javafx.swing;





    opens misstech.sn.javafxprojet to javafx.fxml;
    opens misstech.sn.javafxprojet.entities to org.hibernate.orm.core, jakarta.persistence;
    opens misstech.sn.javafxprojet.controllers to javafx.fxml;
    exports misstech.sn.javafxprojet;
    exports misstech.sn.javafxprojet.controllers;
    exports misstech.sn.javafxprojet.dao;
    exports misstech.sn.javafxprojet.entities;
    exports misstech.sn.javafxprojet.services;


}