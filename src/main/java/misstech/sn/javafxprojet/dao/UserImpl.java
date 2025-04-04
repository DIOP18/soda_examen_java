package misstech.sn.javafxprojet.dao;

import jakarta.persistence.EntityManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import misstech.sn.javafxprojet.JPAUtil;
import misstech.sn.javafxprojet.entities.Users;

import java.util.List;

public class UserImpl implements IUsers{
    EntityManager entityManager;

    public UserImpl(){this.entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();}
    @Override
    public int add(Users users) {
        entityManager.getTransaction().begin();
        entityManager.persist(users);
        entityManager.getTransaction().commit();
        return 1;
    }

    @Override
    public int update(Users users) {
        entityManager.getTransaction().begin();
        Users userDB = entityManager.merge(users);
        userDB.setNom(users.getNom());
        userDB.setPrenom(users.getPrenom());
        userDB.setPassword(users.getPassword());
        userDB.setEmail(users.getEmail());
        userDB.setRole(users.getRole());
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
    public ObservableList<Users> getAll() {
        ObservableList<Users> listUsers = FXCollections.observableArrayList();
        entityManager.getTransaction().begin();
        List<Users> resultat = entityManager.createQuery("FROM Users", Users.class).getResultList();
        entityManager.getTransaction().commit();
        listUsers.addAll(resultat);
        return listUsers;
    }

    @Override
    public Users get(int id) {
        return entityManager.find(Users.class, id);
    }
}
