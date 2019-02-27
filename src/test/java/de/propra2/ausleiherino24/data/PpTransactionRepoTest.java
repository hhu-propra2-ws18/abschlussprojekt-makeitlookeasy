package de.propra2.ausleiherino24.data;

import de.propra2.ausleiherino24.model.PpTransaction;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles(profiles = "test")
public class PpTransactionRepoTest {

    @Autowired
    private PpTransactionRepository ppts;

    private PpTransaction trans1;
    private PpTransaction trans2;

    @BeforeEach
    public void init() {
        trans1 = new PpTransaction();
        trans2 = new PpTransaction();

        ppts.saveAll(Arrays.asList(trans1, trans2));
    }

    @Test
    public void databaseShouldSaveAndFindAllEntities() {
        final List<PpTransaction> us = ppts.findAll();
        Assertions.assertThat(us.size()).isEqualTo(2);
        Assertions.assertThat(us.get(0)).isEqualTo(trans1);
        Assertions.assertThat(us.get(1)).isEqualTo(trans2);
    }


}
