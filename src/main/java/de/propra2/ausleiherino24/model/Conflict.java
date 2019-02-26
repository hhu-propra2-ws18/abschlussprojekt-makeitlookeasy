package de.propra2.ausleiherino24.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
public class Conflict {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "conflict")
    @NotNull
    Case conflictedCase;

    @NotNull
    @Size(min = 15, max = 2048)
    String conflictDescription;

    @NotNull
    String conflictReporterUsername;

    public User getOwner() {
        return conflictedCase.getOwner();
    }

    public User getReceiver() {
        return conflictedCase.getReceiver();
    }

    public Double getDeposit() {
        return conflictedCase.getDeposit();
    }

    public void setConflictedCaseConflict(final Conflict conflict) {
        conflictedCase.setConflict(conflict);
    }
}
