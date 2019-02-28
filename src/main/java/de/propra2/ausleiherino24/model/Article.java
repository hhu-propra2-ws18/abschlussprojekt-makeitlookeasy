package de.propra2.ausleiherino24.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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

    /**
     * If forSale is true, this field will be used as price. If forSale is false, this field will be
     * used as cost per day for rental.
     */
    private Double costPerDay;

    private String location;

    /**
     * true: Article will be sold. false: Article is available for rental.
     */
    private boolean forSale;

    /**
     * true: Artikel existiert noch false: Artikel gelöscht.
     */
    private boolean active;

    /**
     * true: Other users may rent the article. false: Currently, the article is not to borrow.
     */
    private boolean forRental;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn
    private User owner;

    private Category category;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "article")
    private List<Case> cases;

    /**
     * The addCase methods are necessary to be self-made, so that the article is updated in the case
     * object. See:
     * <a href="https://notesonjava.wordpress.com/2008/11/03/managing-the-bidirectional-relationship/">hier</a>
     */

    public void addCase(final Case thisCase) {
        addCase(thisCase, false);
    }

    void addCase(final Case thisCase, final boolean repetition) {
        if (thisCase == null) {
            return;
        }
        if (cases == null) {
            cases = new ArrayList<>();
        }
        if (cases.contains(thisCase)) {
            cases.set(cases.indexOf(thisCase), thisCase);
        } else {
            cases.add(thisCase);
        }
        if (!repetition) {
            thisCase.setArticle(this, true);
        }
    }

    public void setOwner(final User user) {
        setOwner(user, false);
    }

    void setOwner(final User user, final boolean repetition) {
        this.owner = user;
        if (user != null && !repetition) {
            user.addArticle(this, true);
        }
    }

    /**
     * true: if article has only cases where the request status is REQUEST_DECLINED,
     * RENTAL_NOT_POSSIBLE or FINISHED. false: otherwise
     */
    public boolean allCasesClosed() {

        if (getCases() != null) {
            return getCases().stream()
                    .filter(closedCases -> closedCases.getRequestStatus() != Case.REQUEST_DECLINED
                            && closedCases.getRequestStatus() != Case.RENTAL_NOT_POSSIBLE
                            && closedCases.getRequestStatus() != Case.FINISHED)
                    .collect(Collectors.toList()).isEmpty();
        }

        return true;
    }

    //TODO: Löschen, falls nicht mehr gebraucht
    @Override
    public String toString() {
        return "Article: \n"
                + "  " + "id: " + id + "\n"
                + "  " + "name: " + name + "\n"
                + "  " + "description: " + description + "\n"
                + "  " + "image: " + image + "\n"
                + "  " + "deposit: " + deposit + "\n"
                + "  " + "costPerDay: " + costPerDay + "\n"
                + "  " + "location: " + location + "\n"
                + "  " + "active: " + active + "\n"
                + "  " + "forRental: " + forRental + "\n"
                + "  " + "category: " + category + "\n";

    }
}
