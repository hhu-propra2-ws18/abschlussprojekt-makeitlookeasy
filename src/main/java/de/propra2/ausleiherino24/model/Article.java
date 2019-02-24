package de.propra2.ausleiherino24.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @Column(length = 10485760)
    private String description;

    private String image;

    private Double deposit;

    private Double costPerDay;

    private String location;

    /**
     * true: it is possible to rent the article false: owner does not want to have the article for
     * rent right now.
     */
    private boolean active;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn
    private User owner;

    private Category category;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "article")
    private List<Case> cases;

    /**
     * Die Konstruktion ist nötig, damit der Case stets mit geupdated wird. Analoges ist im Case
     * Siehe
     * <a href="https://notesonjava.wordpress.com/2008/11/03/managing-the-bidirectional-relationship/">hier</a>
     */

    public void addCase(Case aCase) {
        addCase(aCase, false);
    }

    @SuppressWarnings("Duplicates")
        // TODO: Duplicate code
    void addCase(Case aCase, boolean repetition) {
        if (aCase == null) {
            return;
        }
        if (cases == null) {
            cases = new ArrayList<>();
        }
        if (cases.contains(aCase)) {
            cases.set(cases.indexOf(aCase), aCase);
        } else {
            cases.add(aCase);
        }
        if (!repetition) {
            aCase.setArticle(this, true);
        }
    }

    public void removeCase(Case aCase) {
        cases.remove(aCase);
        aCase.setArticle(null);
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
