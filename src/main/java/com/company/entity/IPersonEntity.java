package com.company.entity;

import java.io.Serializable;

/**
 * Interface for Person entities.
 *
 * @author <a href="mailto:evfgesha@gmail.com">Eugenia Novikova</a>
 */

public interface IPersonEntity extends Serializable {

  String getId();

  void setId(String id);

  String getName();

  void setName(String name);

  String getCountry();

  void setCountry(String visitCount);
}
