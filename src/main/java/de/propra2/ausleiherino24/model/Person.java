package de.propra2.ausleiherino24.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Person beinhaltet die Stammdaten.
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Person {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  Long id;

  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  User user;

  String firstName;

  String lastName;

  String address;

  public void setUser(User user) {
    setUser(user, false);
  }

  void setUser(User user, boolean repetition) {
    this.user = user;
    if (user != null && !repetition) {
      user.setPerson(this, true);
    }
  }

}
