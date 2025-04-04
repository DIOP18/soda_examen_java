package misstech.sn.javafxprojet.dao;

import jakarta.persistence.EntityManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import misstech.sn.javafxprojet.JPAUtil;
import misstech.sn.javafxprojet.entities.Emargement;

import java.util.List;

public class EmargementImpl implements IEmargement{
    EntityManager entityManager;

    public EmargementImpl(){
        this.entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
    }
    @Override
    public int add(Emargement emargement) {
        entityManager.getTransaction().begin();
        entityManager.persist(emargement);
        entityManager.getTransaction().commit();
        return 1;
    }

    @Override
    public int update(Emargement emargement) {
        return 0;
    }

    @Override
    public int delete(int id) {
        return 0;
    }

    @Override
    public ObservableList<Emargement> getAll() {
     ObservableList<Emargement> ListEmarge = FXCollections.observableArrayList();
     entityManager.getTransaction().begin();
     List<Emargement> resultat = entityManager.createQuery("FROM Emargement", Emargement.class).getResultList();
     entityManager.getTransaction().commit();
        ListEmarge.addAll(resultat);
        return ListEmarge;

    }

    @Override
    public Emargement get(int id) {
        return entityManager.find(Emargement.class,id);
    }
}
