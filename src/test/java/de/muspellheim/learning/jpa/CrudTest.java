package de.muspellheim.learning.jpa;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.*;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests using the entity manager to create, retrieve, update and delete entities.
 */
public class CrudTest {

    EntityManagerFactory emf;

    @Before
    public void setUp() {
        emf = Persistence.createEntityManagerFactory("learning-jpa");
    }

    @After
    public void tearDown() {
        emf.close();
    }

    @Test
    public void testCreate() {
        createAlice();
    }

    private void createAlice() {
        Contact alice = new Contact();
        alice.setName("Alice");
        alice.setEmail("alice@test.com");

        EntityManager em = emf.createEntityManager();
        em.persist(alice);
        em.close();
    }

    @Test
    public void testRetrieve() {
        createAlice();

        EntityManager em = emf.createEntityManager();
        TypedQuery<Contact> query = em.createQuery("from Contact", Contact.class);
        List<Contact> contactList = query.getResultList();
        em.close();

        Contact alice = new Contact();
        alice.setName("Alice");
        alice.setEmail("alice@test.com");
        assertEquals(1, contactList.size());
        assertEquals(alice, contactList.get(0));
    }


}
