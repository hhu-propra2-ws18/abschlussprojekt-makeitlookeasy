package de.propra2.ausleiherino24.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import lombok.Data;

@Data
@Entity
public class Article {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  Long id;

  String name;

  @Column(length = 10485760)
  String description;

  String image;

  Boolean active;    // If this is true the article is not available for rental ("deleted")

  Boolean reserved;  // If this is true the article is not available for rental ("reserved/rented")

  Category category;

  @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  User owner;

  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  Case aCase;


  public Article() {
  }

  //for testing
  public Article(Long id, String name, String description, Boolean active, Boolean reserved,
      User owner, String image) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.active = active;
    this.reserved = reserved;
    this.owner = owner;
    this.image = image;
  }

  //for testing
  public Article(Long id, Boolean active, Boolean reserved, Category category) {
    this.id = id;
    this.active = active;
    this.reserved = reserved;
    this.category = category;
  }

  /**
   * Die Konstruktion ist nötig, damit der Case stets mit geupdatet wird. Analoges ist im Case
   * Siehe
   * <a href="https://notesonjava.wordpress.com/2008/11/03/managing-the-bidirectional-relationship/">hier</a>
   */
  public void setACase(Case aCase) {
    setACase(aCase, false);
  }

  void setACase(Case aCase, boolean repetition) {
    this.aCase = aCase;
    if (aCase != null && !repetition) {
      aCase.setArticle(this, true);
    }
  }

  public void setOwner(User user) {
    setOwner(user, false);
  }

  void setOwner(User user, boolean repetition) {
    this.owner = user;
    if (user != null && !repetition) {
      user.addArticle(this, true);
    }
  }
}
