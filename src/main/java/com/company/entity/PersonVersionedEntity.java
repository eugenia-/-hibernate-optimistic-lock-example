package com.company.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OptimisticLock;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;

/**
 * Person entity with version field, which is managed by Hibernate.
 *
 * @author <a href="mailto:evfgesha@gmail.com">Eugenia Novikova</a>
 */
@Entity(name = "Person")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonVersionedEntity implements IPersonEntity {

  @Id
  private String id;

  @Column(name = "name")
  private String name;

  /**
   * Property which should not bump up the entity version.
   */
  @OptimisticLock(excluded = true)
  private String country;

  @Version
  private int version;


}
