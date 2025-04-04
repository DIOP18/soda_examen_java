package misstech.sn.javafxprojet.dao;

import jakarta.persistence.EntityManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import misstech.sn.javafxprojet.JPAUtil;
import misstech.sn.javafxprojet.entities.Cours;
import misstech.sn.javafxprojet.entities.Users;

import java.util.List;

public class CoursImpl implements ICours{
    EntityManager entityManager;

    public CoursImpl(){
        this.entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
    }
    @Override
    public int add(Cours cours) {
        entityManager.getTransaction().begin();
        entityManager.persist(cours);
        entityManager.getTransaction().commit();
        return 1;
    }

    @Override
    public int update(Cours cours) {
        entityManager.getTransaction().begin();
        entityManager.merge(cours);
        entityManager.getTransaction().commit();
        return 1;
    }

    @Override
    public int delete(int id) {
        entityManager.getTransaction().begin();
        entityManager.remove(get(id));
        entityManager.getTransaction().commit();
        return 1;
    }

    @Override
    public ObservableList<Cours> getAll() {
        ObservableList<Cours> listCours = FXCollections.observableArrayList();
        entityManager.getTransaction().begin();
        List<Cours> resultat = entityManager.createQuery("FROM Cours", Cours.class).getResultList();
        entityManager.getTransaction().commit();
        listCours.addAll(resultat);
        return listCours;
    }

    @Override
    public Cours get(int id) {
        return entityManager.find(Cours.class, id);
    }
}
