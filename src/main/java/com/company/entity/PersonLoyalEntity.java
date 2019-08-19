package com.company.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;
import org.hibernate.annotations.SelectBeforeUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * The person class, for which optimistic locking based on dirty fields.
 *
 * @author <a href="mailto:evfgesha@gmail.com">Eugenia Novikova</a>
 */
@Entity(name = "PersonLoyalEntity")
@OptimisticLocking(type = OptimisticLockType.DIRTY)
@DynamicUpdate
@SelectBeforeUpdate
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonLoyalEntity implements IPersonEntity {

  @Id
  private String id;

  @Column(name = "name")
  private String name;

  @Column(name = "visit_count")
  private String country;


}
