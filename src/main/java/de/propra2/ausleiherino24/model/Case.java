package de.propra2.ausleiherino24.model;

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

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;

	Long startTime;

	Long endTime;

	int price;

	int deposit;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	CustomerReview customerReview;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	User receiver;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	Article article;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	PPTransaction ppTransaction;


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

	public String getFormattedStartTime(){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
		return simpleDateFormat.format(startTime);
	}

	public String getFormattedEndTime(){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
		return simpleDateFormat.format(endTime);
	}

	public boolean getActive(){
		return article.isActive();
	}

}
