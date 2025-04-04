package misstech.sn.javafxprojet.dao;

import jakarta.persistence.EntityManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import misstech.sn.javafxprojet.JPAUtil;
import misstech.sn.javafxprojet.entities.Salle;

import java.util.List;

public class SalleImpl implements ISalle {
    EntityManager entityManager;

    public SalleImpl() {
        this.entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
    }

    @Override
    public int add(Salle salle) {
        entityManager.getTransaction().begin();
        entityManager.persist(salle);
        entityManager.getTransaction().commit();
        return 1;
    }

    @Override
    public int update(Salle salle) {
        entityManager.getTransaction().begin();
        Salle salleDB = entityManager.merge(salle);
        salleDB.setLibelle(salle.getLibelle());
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
    public ObservableList<Salle> getAll() {
        ObservableList<Salle> listSalles = FXCollections.observableArrayList();
        entityManager.getTransaction().begin();
        List<Salle> resultat = entityManager.createQuery("FROM Salle", Salle.class).getResultList();
        entityManager.getTransaction().commit();
        listSalles.addAll(resultat);
        return listSalles;
    }

    @Override
    public Salle get(int id) {
        return entityManager.find(Salle.class, id);
    }
}