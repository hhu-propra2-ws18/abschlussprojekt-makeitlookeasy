package de.propra2.ausleiherino24.propayhandler.model;

import de.propra2.ausleiherino24.model.Case;
import java.text.SimpleDateFormat;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "acase")
public class PpTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "ppTransaction")
    Case acase;

    Long date;

    Double lendingCost;

    boolean cautionPaid;

    Long reservationId;

    /**
     * Gets total payment. Either lendingCosts + deposit or just the lendingCosts.
     */
    public Double getTotalPayment() {
        if (cautionPaid) {
            return lendingCost + acase.getDeposit();
        }
        return lendingCost;
    }

    /**
     * Returns date as String, needed for BankAccount View.
     */
    public String getFormattedDate() {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        return simpleDateFormat.format(date);
    }


}
