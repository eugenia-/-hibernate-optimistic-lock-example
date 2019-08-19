package com.company.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * The person class, for which optimistic locking based on all fields.
 *
 * @author <a href="mailto:evfgesha@gmail.com">Eugenia Novikova</a>
 */
@Entity(name = "PersonStrictEntity")
@OptimisticLocking(type = OptimisticLockType.ALL)
@DynamicUpdate
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonStrictEntity implements IPersonEntity {

  @Id
  private String id;

  @Column(name = "name")
  private String name;

  private String country;

}
