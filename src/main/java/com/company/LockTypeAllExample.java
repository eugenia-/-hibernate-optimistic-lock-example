package com.company;

import com.company.entity.IPersonEntity;
import com.company.entity.PersonStrictEntity;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main class to run an example of Optimistic locking which uses entity without version field,
 * but {@link org.hibernate.annotations.OptimisticLocking} annotation
 * with {@link org.hibernate.annotations.OptimisticLockType#ALL} lock type.
 *
 * @author <a href="mailto:evfgesha@gmail.com">Eugenia Novikova</a>
 */
public class LockTypeAllExample {

  private static final int PERSON_COUNT = 2;

  private static CrudUtil<PersonStrictEntity> crudUtil = new CrudUtil<>(PersonStrictEntity.class);

  public static void main(String[] args) throws JsonProcessingException, InterruptedException {

    System.out.println("Insert persons...");
    int i = 0;
    while (i < PERSON_COUNT) {
      crudUtil.insertPerson(PersonStrictEntity.builder().id(UUID.randomUUID().toString()).name("InitialName_" + ++i).country("InitialCounty").build());
    }

    crudUtil.printPersons();

    // Run threads that modify a versioned personEntity, verify that Optimistic Lock Exception is thrown.
    IPersonEntity personEntity = crudUtil.listPersons().get(0);

     /* The following callable objects in list will read persons from db to update and will sleep for some milliseconds
    before committing the changes, The commit delays will result in OptimisticLock exception.*/
    List<Callable<String>> callableList = Arrays.asList(
        () -> crudUtil.editPersonCountry(500, personEntity.getId()),
        () -> crudUtil.editPersonName(1000, personEntity.getId())
    );

    ExecutorService executorService = Executors.newFixedThreadPool(callableList.size());

    executorService.invokeAll(callableList)
        .stream()
        .map(future -> {
          try {
            return future.get();
          } catch (Exception e) {
            throw new IllegalStateException(e);
          }
        })
        .forEach(System.out::println);

    executorService.shutdown();

    crudUtil.printPersons();
  }

}
