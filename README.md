Learning JPA [![Build Status](https://travis-ci.org/falkoschumann/learning-jpa.png?branch=master)](https://travis-ci.org/falkoschumann/learning-jpa)
============

Learning Java Persistence API (JPA) with learning tests.

The use of create, retrieve, update and delete show the `CrudTest` class.


Common Practice
---------------

All operations to manipulate an entity must be wrapped by transaction. A
transaction can fail, in this case the transaction must be roll back.

In a non-managed environment you can use the following pattern:

    EntityManager em = emf.createEntityManager();
    EntityTransaction tx = null;
    try {
        tx = em.getTransaction();
        tx.begin();
        
        // do some work

        tx.commit();
    } catch (RuntimeException ex) {
        if (tx != null && tx.isActive()) {
            tx.rollback();
        }
        throw ex;
    } finally {
        em.close();
    }
