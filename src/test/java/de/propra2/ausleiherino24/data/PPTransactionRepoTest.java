package de.propra2.ausleiherino24.data;

import de.propra2.ausleiherino24.model.PPTransaction;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles(profiles = "test")
public class PPTransactionRepoTest {

    @Autowired
    private PpTransactionRepository ppts;

    private PPTransaction trans1;
    private PPTransaction trans2;

    @Before
    public void init() {
        trans1 = new PPTransaction();
        trans2 = new PPTransaction();

        ppts.saveAll(Arrays.asList(trans1, trans2));
    }

    @Test
    public void databaseShouldSaveAndFindAllEntities() {
        final List<PPTransaction> us = ppts.findAll();
        Assertions.assertThat(us.size()).isEqualTo(2);
        Assertions.assertThat(us.get(0)).isEqualTo(trans1);
        Assertions.assertThat(us.get(1)).isEqualTo(trans2);
    }


}
