package de.propra2.ausleiherino24.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// TODO: Extract duplicate code.

/**
 * User hat neben Person eine eigene ID, um diesen als Plattformbenutzer explizit separat ansteuern
 * zu können.
 */
@Data
@Entity
@Table(name = "userDB")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    @Column(unique = true)
    private String email;

    private String role;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "owner")
    private List<Article> articleList;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
    private Person person;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "receiver")
    private List<Case> casesReceiver;

    /**
     * constructor to create userDetails.
     *
     * @param user containing data to be extracted
     */
    public User(final User user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.email = user.getEmail();
        this.role = user.getRole();
    }

    public void addArticle(final Article article) {
        addArticle(article, false);
    }

    @SuppressWarnings("Duplicates")
    void addArticle(final Article article, final boolean repetition) {
        if (article == null) {
            return;
        }
        if (articleList == null) {
            articleList = new ArrayList<>();
        }
        if (articleList.contains(article)) {
            articleList.set(articleList.indexOf(article), article);
        } else {
            articleList.add(article);
        }
        if (!repetition) {
            article.setOwner(this, true);
        }
    }

    public void setPerson(final Person person) {
        setPerson(person, false);
    }

    void setPerson(final Person person, final boolean repetition) {
        this.person = person;
        if (person != null && !repetition) {
            person.setUser(this, true);
        }
    }

    //TODO: override setPassword to hash password
}
