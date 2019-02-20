package de.propra2.ausleiherino24.model;

import lombok.Data;

@Data
public class ResolveConflict {

    private User depositReceiver;

    private Long conflictId;
}
