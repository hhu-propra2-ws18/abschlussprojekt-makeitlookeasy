package de.propra2.ausleiherino24.model;

import java.text.SimpleDateFormat;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class CustomerReview {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @NotNull
    Case aCase;

    double stars;

    String title;

    @Column(length = 10485760)
    String text;

    Long timestamp;

    /**
     * sets which case belongs to review.
     *
     * @param aCase case to set
     */
    public void setACase(final Case aCase) {
        setACase(aCase, false);
    }

    /**
     * sets which case belongs to review.
     *
     * @param aCase case to set
     * @param repetition for database error prevention
     */
    public void setACase(final Case aCase, final boolean repetition) {
        this.aCase = aCase;
        if (aCase != null && !repetition) {
            aCase.setReview(this, true);
        }
    }

    /**
     * converts review to string.
     *
     * @return returns converted Form of Review
     */
    @Override
    public String toString() {
        return "Stars: " + stars + "\n"
                + "Title: " + title + "\n"
                + "Text: " + text + "\n"
                + "timestamp: " + timestamp;
    }

    /**
     * Is used in HTML view to convert long into time string.
     */
    public String getFormattedTime() {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        return simpleDateFormat.format(timestamp);
    }
}
