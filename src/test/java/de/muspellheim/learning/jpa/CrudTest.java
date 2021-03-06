package de.muspellheim.learning.jpa;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNotNull;

/**
 * Tests using the entity manager to create, retrieve, update and delete entities.
 */
public class CrudTest {

    private EntityManagerFactory emf;

    @Before
    public void setUp() {
        emf = Persistence.createEntityManagerFactory("learning-jpa");
    }

    @After
    public void tearDown() {
        emf.close();
    }

    private static Contact createAlice() {
        Contact c = new Contact();
        c.setName("Alice");
        c.setEmail("alice@test.com");
        return c;
    }

    private static Contact createBob() {
        Contact c = new Contact();
        c.setName("Bob");
        c.setEmail("bob@test.com");
        return c;
    }

    /**
     * Without transaction the persisted entity is not written to database.
     */
    @Test(expected = AssertionError.class)
    public void testCreate_PersistWithoutTransaction() {
        EntityManager em = emf.createEntityManager();
        em.persist(createAlice());
        em.close();

        assertNotNull(retrieveWithId("Alice"));
    }

    @Test
    public void testCreate() {
        create(createAlice());

        assertNotNull(retrieveWithId("Alice"));
    }

    /**
     * Create a new contact in database.
     */
    private void create(Contact contact) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.persist(contact);
        tx.commit();
        em.close();
    }

    @Test
    public void testRetrieveAll() {
        create(createAlice());
        create(createBob());

        List<Contact> contactList = retrieveAll();

        assertThat(contactList, contains(hasProperty("name", is("Alice")), hasProperty("name", is("Bob"))));
    }

    /**
     * Retrieve the list of contacts from database.
     */
    private List<Contact> retrieveAll() {
        EntityManager em = emf.createEntityManager();
        TypedQuery<Contact> query = em.createQuery("from Contact", Contact.class);
        List<Contact> resultList = query.getResultList();
        em.close();
        return resultList;
    }

    @Test
    public void testRetrieveWithId() {
        create(createAlice());
        create(createBob());

        Contact contact = retrieveWithId("Alice");

        assertThat(contact, hasProperty("name", is("Alice")));
    }

    /**
     * Retrieve the contact with given id from database.
     */
    private Contact retrieveWithId(String id) {
        EntityManager em = emf.createEntityManager();
        Contact result = em.find(Contact.class, id);
        em.close();
        return result;
    }

    @Test
    public void testRetrieveWithFilter() {
        create(createAlice());
        create(createBob());

        List<Contact> contactList = retrieveWithFilter();

        assertThat(contactList, contains(hasProperty("email", is("bob@test.com"))));
    }

    /**
     * Retrieve the list of contacts from database with email like "bob@test.com".
     */
    private List<Contact> retrieveWithFilter() {
        EntityManager em = emf.createEntityManager();
        TypedQuery<Contact> query = em.createQuery("from Contact where email like 'bob@test.com'", Contact.class);
        List<Contact> resultList = query.getResultList();
        em.close();
        return resultList;
    }

    /**
     * Without transaction the updated entity is not synchronized with database.
     */
    @Test(expected = AssertionError.class)
    public void testUpdate_MergeWithoutTransaction() {
        create(createAlice());
        create(createBob());

        Contact alice = retrieveWithId("Bob");
        alice.setEmail("noreply@test.com");
        EntityManager em = emf.createEntityManager();
        em.merge(alice);
        em.close();

        Contact actual = retrieveWithId("Bob");
        assertThat(actual, hasProperty("email", is("noreply@test.com")));
    }

    @Test
    public void testUpdate() {
        create(createAlice());
        create(createBob());

        Contact alice = retrieveWithId("Bob");
        alice.setEmail("noreply@test.com");
        update(alice);

        Contact actual = retrieveWithId("Bob");
        assertThat(actual, hasProperty("email", is("noreply@test.com")));
    }

    private void update(Contact contact) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.merge(contact);
        tx.commit();
        em.close();
    }

    /**
     * Without transaction the removed entity is not removed from database.
     */
    @Test(expected = AssertionError.class)
    public void testDelete_RemoveWithoutTransaction() {
        create(createAlice());
        create(createBob());

        Contact alice = retrieveWithId("Alice");
        EntityManager em = emf.createEntityManager();
        em.remove(em.merge(alice));
        em.close();

        List<Contact> contactList = retrieveAll();
        assertThat(contactList, contains(hasProperty("name", is("Bob"))));
    }

    @Test
    public void testDelete() {
        create(createAlice());
        create(createBob());

        Contact alice = retrieveWithId("Alice");
        delete(alice);

        List<Contact> contactList = retrieveAll();
        assertThat(contactList, contains(hasProperty("name", is("Bob"))));
    }

    private void delete(Contact contact) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.remove(em.merge(contact));
        tx.commit();
        em.close();
    }

}
