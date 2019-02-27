package de.propra2.ausleiherino24.model;

import java.text.SimpleDateFormat;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "caseDB")
@NoArgsConstructor
@AllArgsConstructor
public class Case {

    /**
     * Bitte die Werte nicht ändern. Diese dienen zur Sortierung und müssen gegebenefalls in
     * myOverview.html mit angepasst werden
     */

    public static final int REQUESTED = 1;
    public static final int REQUEST_ACCEPTED = 2;
    public static final int REQUEST_DECLINED = 12;

    //Falls der Artikel zu gegebenem Zeitraum bereits verliehen ist
    public static final int RENTAL_NOT_POSSIBLE = 4;

    public static final int RUNNING = 7; //Verleih läuft aktuell
    // TODO: Update views to support RUNNING_EMAILSENT as RUNNING.
    public static final int RUNNING_EMAILSENT = 8;
    public static final int OPEN_CONFLICT = 10; //Es gibt noch einen offenen Konflikt
    public static final int FINISHED = 14; //Verleih ist beendet

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    PPTransaction ppTransaction;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long startTime;
    private Long endTime;
    private Double price;
    private Double deposit;
    private int requestStatus;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "acase")
    private CustomerReview customerReview;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn
    private User receiver;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn
    private Article article;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Conflict conflict;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private CustomerReview review;

    /**
     * Die Konstruktion ist nötig, damit der Case stets mit geupdatet wird. Analoges ist im Case
     * Siehe
     * <a href="https://notesonjava.wordpress.com/2008/11/03/managing-the-bidirectional-relationship/">hier</a>
     */

    public void setArticle(final Article article) {
        setArticle(article, false);
    }

    void setArticle(final Article article, final boolean repetition) {
        this.article = article;
        if (article != null && !repetition) {
            article.addCase(this, true);
        }
    }

    public void setReview(final CustomerReview review) {
        setReview(review, false);
    }

    void setReview(final CustomerReview review, final boolean repetition) {
        this.review = review;
        if (review != null && !repetition) {
            review.setAcase(this, true);
        }
    }

    public User getOwner() {
        return this.article.getOwner();
    }

    /**
     * Formatiert die Startzeit in dd.mm.yyy
     */
    public String getFormattedStartTime() {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        return simpleDateFormat.format(startTime);
    }

    /**
     * Formatiert die Endzeit in dd.mm.yyy
     */
    public String getFormattedEndTime() {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        return simpleDateFormat.format(endTime);
    }

    public boolean isActive() {
        return article.isActive();
    }

}
