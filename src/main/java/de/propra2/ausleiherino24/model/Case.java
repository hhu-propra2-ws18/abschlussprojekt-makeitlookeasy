package de.propra2.ausleiherino24.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "caseDB")
public class Case {

    public Boolean active;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    Long startTime;
    Long endTime;
    int price;
    int deposit;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    User receiver;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    Article article;

    public Case() {
    }

    public Case(Long id, Long startTime, Long endTime, int price, int deposit, Boolean active,
            User receiver, Article article) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
        this.deposit = deposit;
        this.active = active;
        this.receiver = receiver;
        this.article = article;
    }

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
            article.setACase(this, true);
        }
    }

    public User getOwner() {
        return this.article.getOwner();
    }

}
