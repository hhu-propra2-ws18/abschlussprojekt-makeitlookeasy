package de.propra2.ausleiherino24.model;

import java.text.SimpleDateFormat;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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

    public static final int REQUESTED = 1;
    public static final int REQUEST_ACCEPTED = 2;
    public static final int REQUEST_DECLINED = 3;
    public static final int RENTAL_NOT_POSSIBLE = 4; //Falls der Artikel zu gegebenem Zeitraum bereits verliehen ist

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    PPTransaction ppTransaction;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long startTime;
    private Long endTime;
    private int price;
    private int deposit;
    private int requestStatus;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private CustomerReview customerReview;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private User receiver;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Article article;

    /**
     * Die Konstruktion ist nötig, damit der Case stets mit geupdatet wird. Analoges ist im Case
     * Siehe
     * <a href="https://notesonjava.wordpress.com/2008/11/03/managing-the-bidirectional-relationship/">hier</a>
     */

    public void setArticle(Article article) {
        setArticle(article, false);
    }

    void setArticle(Article article, boolean repetition) {
        this.article = article;
        if (article != null && !repetition) {
            article.addCase(this, true);
        }
    }

    public User getOwner() {
        return this.article.getOwner();
    }

    /**
     * Formatiert die Startzeit in dd.mm.yyy
     */
    public String getFormattedStartTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        return simpleDateFormat.format(startTime);
    }

    /**
     * Formatiert die Endzeit in dd.mm.yyy
     */
    public String getFormattedEndTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        return simpleDateFormat.format(endTime);
    }

    public boolean getActive() {
        return article.isActive();
    }

}
