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
    Case acase;

    double stars;

    String title;

    @Column(length = 10485760)
    String text;

    Long timestamp;

    public void setAcase(final Case aCase) {
        setAcase(aCase, false);
    }

    public void setAcase(final Case aCase, final boolean repetition) {
        this.acase = aCase;
        if (aCase != null && !repetition) {
            aCase.setReview(this, true);
        }
    }

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
