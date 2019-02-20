package de.propra2.ausleiherino24.model;

import java.text.SimpleDateFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Table(name = "caseDB")
@NoArgsConstructor
@AllArgsConstructor
public class Case {

	public static final String REQUESTED = "Requested";
	public static final String REQUEST_ACCEPTED = "Request accepted";
	public static final String REQUEST_DECLINED = "Request declined";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private Long startTime;

	private Long endTime;

	private int price;

	private int deposit;

	private String requestStatus;

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

	public String getFormattedStartTime(){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
		return simpleDateFormat.format(startTime);
	}

	public String getFormattedEndTime(){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
		return simpleDateFormat.format(endTime);
	}

}
