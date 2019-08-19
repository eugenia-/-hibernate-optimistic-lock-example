package com.company;

import com.company.entity.IPersonEntity;
import com.company.entity.PersonLoyalEntity;
import com.company.entity.PersonStrictEntity;
import com.company.entity.PersonVersionedEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.persistence.OptimisticLockException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * The util class contains CRUD operations and other common methods to manipulate entities.
 *
 * @author <a href="mailto:evfgesha@gmail.com">Eugenia Novikova</a>
 */
public class CrudUtil<T extends IPersonEntity> {

  private final SessionFactory sessionFactory;

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private Class<T> clazz;

  /**
   * The default constructor.
   *
   * @param clazz parameterized class.
   */
  CrudUtil(Class<T> clazz) {

    java.util.Properties properties = new Properties();
    try {
      properties.load(this.getClass().getClassLoader().getResourceAsStream("hibernate.properties"));
    } catch (IOException e) {
      throw new RuntimeException(String.format("Error to initiate %s.", this.getClass().getName()), e);
    }

    sessionFactory = new Configuration()
        .configure() // configures settings from hibernate.cfg.xml
        .addProperties(properties)
        .addAnnotatedClass(PersonVersionedEntity.class)
        .addAnnotatedClass(PersonLoyalEntity.class)
        .addAnnotatedClass(PersonStrictEntity.class)
        .buildSessionFactory();


    this.clazz = clazz;
  }

  /**
   * Edits a person name by id.
   *
   * @param sleepTime time in milliseconds to wait before committing the changes.
   * @param personId  person id.
   * @return A string result message of the transaction operations.
   */
  public String editPersonCountry(long sleepTime, String personId) {
    String status;
    long threadId = Thread.currentThread().getId();
    System.out.println(String.format("%s thread updating person country by %s person id", threadId, personId));
    Session session = null;

    try {
      session = sessionFactory.openSession();
      session.beginTransaction();
      IPersonEntity fetchedPersonEntity = session.get(clazz, personId);
      sleep(sleepTime);
      fetchedPersonEntity.setCountry(String.format("%s changed by %s thread", fetchedPersonEntity.getCountry(), threadId));
      status = updatePersonFragment(session, fetchedPersonEntity);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    return status;
  }

  /**
   * Edits a person name by id.
   *
   * @param sleepTime time in milliseconds to wait before committing the changes.
   * @param personId  person id.
   * @return A string result message of the transaction operations.
   */
  public String editPersonName(long sleepTime, String personId) {

    String status;
    long threadId = Thread.currentThread().getId();
    System.out.println(String.format("%s thread updating person name by %s person id", threadId, personId));
    Session session = null;

    try {
      session = sessionFactory.openSession();
      session.beginTransaction();
      IPersonEntity fetchedPersonEntity = session.get(clazz, personId);

      sleep(sleepTime);

      fetchedPersonEntity.setName(String.format("%s updatedBy %s thread", fetchedPersonEntity.getName(), threadId));
      status = updatePersonFragment(session, fetchedPersonEntity);

    } finally {
      if (session != null) {
        session.close();
      }
    }
    return status;

  }

  /**
   * A reusable fragment that is used for saving {@link T} entity.
   *
   * @param session             session.
   * @param fetchedPersonEntity entity.
   * @return A string result message of the transaction operations.
   */
  private String updatePersonFragment(Session session, IPersonEntity fetchedPersonEntity) {
    long threadId = Thread.currentThread().getId();
    String status;
    try {
      session.update(fetchedPersonEntity);
      session.getTransaction().commit();
      status = String.format("Tread %s, Transaction Status: %s.", threadId, session.getTransaction().getStatus());
    } catch (OptimisticLockException e) {
      status = String.format("Tread %s, Transaction Status: %s. Caused by %s", threadId, session.getTransaction().getStatus(), e.getMessage());
    }
    return status;
  }

  /**
   * Prints persons that are stored in the DB.
   *
   * @throws JsonProcessingException exception.
   */
  public void printPersons() throws JsonProcessingException {
    List<T> result = listPersons();
    System.out.println("Persons in db:");
    for (IPersonEntity personEntity : result) {
      System.out.println(OBJECT_MAPPER.writeValueAsString(personEntity));
    }
  }

  /**
   * Creates a person into the DB.
   *
   * @param personEntity an entity to insert.
   */
  public void insertPerson(T personEntity) {
    System.out.println("Insert persons...");
    Session session = sessionFactory.openSession();
    session.beginTransaction();
    session.save(personEntity);
    session.getTransaction().commit();
    session.close();
  }

  /**
   * Updates persons names one by one.
   */
  public void updatePersonNames() {
    System.out.println("Update persons...");
    Session session = sessionFactory.openSession();
    List<T> personEntityList = listPersons();
    session.beginTransaction();

    for (IPersonEntity personEntity : personEntityList) {
      personEntity.setName(personEntity.getName() + "1");
      session.update(personEntity);
    }
    session.getTransaction().commit();
    session.close();
  }

  /**
   * Fetch person list from DB.
   *
   * @return List of {@link T}.
   */
  public List<T> listPersons() {
    Session session = sessionFactory.openSession();
    session.beginTransaction();
    List<T> result = session.createQuery("from " + clazz.getName()).list();
    session.close();
    return result;
  }

  /**
   * Causes the currently executing thread to sleep.
   *
   * @param sleepTime time in milliseconds.
   */
  private static void sleep(long sleepTime) {
    if (sleepTime > 0) {
      try {
        System.out.println(String.format("%s thread sleep for %s ms", Thread.currentThread().getId(), sleepTime));
        Thread.sleep(sleepTime);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
