package de.propra2.ausleiherino24.data;

import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.Conflict;
import de.propra2.ausleiherino24.model.User;
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
public class ConflictRepositoryTest {

    @Autowired
    private ConflictRepository conflicts;

    private User user1;
    private User user2;

    private Conflict c1;

    @BeforeEach
    public void init() {
        user2 = new User();
        user2.setUsername("user2");
        user1 = new User();
        user1.setUsername("user1");

        final Article case1Art = new Article();
        case1Art.setOwner(user2);
        Case case1 = new Case();
        case1.setReceiver(user1);
        case1.setArticle(case1Art);
        case1.setPrice(80D);
        case1.setDeposit(200D);
        case1.setStartTime(12022019L);
        case1.setEndTime(19022019L);

        final Article case2Art = new Article();
        case2Art.setOwner(user1);
        Case case2 = new Case();
        case2.setReceiver(user2);
        case2.setArticle(case2Art);
        case2.setPrice(60D);
        case2.setDeposit(150D);
        case2.setStartTime(10022019L);
        case2.setEndTime(15022019L);

        c1 = new Conflict();
        case1.setConflict(c1);
        c1.setConflictedCase(case1);
        c1.setConflictReporterUsername(case1.getReceiver().getUsername());
        c1.setConflictDescription("Article not as described");

        Conflict c2 = new Conflict();
        case2.setConflict(c2);
        c2.setConflictedCase(case2);
        c2.setConflictReporterUsername(case2.getOwner().getUsername());
        c2.setConflictDescription("Article returned damaged");

        conflicts.saveAll(Arrays.asList(c1, c2));
    }

    @Test
    public void customQueryFindAllByReceiverShouldReturnConflictWithCorrespondingCaseReceiver() {
        final List<Conflict> expectedConflict = conflicts.findAllByReceiver(user1);
        Assertions.assertThat(expectedConflict.size()).isOne();
        Assertions.assertThat(expectedConflict.get(0)).isEqualTo(c1);
    }

    @Test
    public void customQueryFindAllByArticleOwnerShouldReturnConflictWithCorrespondingCaseArticleOwner() {
        final List<Conflict> expectedConflict = conflicts.findAllByArticleOwner(user2);
        Assertions.assertThat(expectedConflict.size()).isOne();
        Assertions.assertThat(expectedConflict.get(0)).isEqualTo(c1);
    }

}
