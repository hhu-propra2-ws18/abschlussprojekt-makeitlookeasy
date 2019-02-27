package de.propra2.ausleiherino24.data;

import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.User;
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

public class CaseRepoTest {

    @Autowired
    private CaseRepository cases;

    @Autowired
    private UserRepository users;

    private Case case1;
    private Case case2;

    @Before
    public void init() {
        case1 = new Case();
        case1.setReceiver(new User());
        case1.setArticle(new Article());
        case1.setPrice(80D);
        case1.setDeposit(200D);
        case1.setStartTime(12022019L);
        case1.setEndTime(19022019L);

        case2 = new Case();
        case2.setReceiver(new User());
        case2.setArticle(new Article());
        case2.setPrice(60D);
        case2.setDeposit(150D);
        case2.setStartTime(10022019L);
        case2.setEndTime(15022019L);

        cases.saveAll(Arrays.asList(case1, case2));
    }

    @Test
    public void databaseShouldSaveEntities() {
        final List<Case> us = cases.findAll();
        Assertions.assertThat(us.size()).isEqualTo(2);
        Assertions.assertThat(us).containsExactlyInAnyOrder(case1, case2);
    }

    @Test
    public void databaseShouldRemoveCorrectEntity() {
        cases.delete(case2);

        final List<Case> us = cases.findAll();
        Assertions.assertThat(us.size()).isOne();
        Assertions.assertThat(us.get(0)).isEqualTo(case1);
    }

    @Test
    public void databaseShouldReturnCountOfTwoIfDatabaseHasTwoEntries() {
        Assertions.assertThat(cases.count()).isEqualTo(2);
    }

    @Test
    public void queryFindByArticleAndRequestStatusShouldReturnCaseWithCorrespondingArticleIfStatusIsMatching() {
        case2.setRequestStatus(Case.RUNNING);
        final List<Case> expectedCases = cases
                .findAllByArticleAndRequestStatus(case2.getArticle(), Case.RUNNING);
        Assertions.assertThat(expectedCases.size()).isOne();
        Assertions.assertThat(expectedCases.get(0)).isEqualTo(case2);
    }

    @Test
    public void queryFindByArticleAndRequestStatusShouldReturnNoCaseWithCorrespondingArticleIfStatusIsNotMatching() {
        case2.setRequestStatus(Case.RUNNING);
        final List<Case> expectedCases = cases
                .findAllByArticleAndRequestStatus(case2.getArticle(), Case.REQUEST_ACCEPTED);
        Assertions.assertThat(expectedCases.size()).isZero();
    }

    @Test
    public void customQueryFindAllByReceiverShouldReturnCaseWithCorrespondingReceiver() {
        final List<Case> expectedCase = cases.findAllByReceiver(case1.getReceiver());
        Assertions.assertThat(expectedCase.size()).isOne();
        Assertions.assertThat(expectedCase.get(0)).isEqualTo(case1);
    }

    @Test
    public void customQueryFindAllByArticleOwnerShouldReturnCaseWithCorrespondingArticleOwner() {
        case2.getArticle().setOwner(new User());
        case1.getArticle().setOwner(new User());

        final List<Case> expectedCase = cases.findAllByArticleOwner(case2.getOwner());
        Assertions.assertThat(expectedCase.size()).isOne();
        Assertions.assertThat(expectedCase.get(0)).isEqualTo(case2);
    }

    @Test
    public void customQueryFindAllByArticleOwnerShouldReturnTwoCaseWithCorrespondingArticleOwner() {
        final User user = new User();
        case2.getArticle().setOwner(user);
        case1.getArticle().setOwner(user);

        final List<Case> expectedCase = cases.findAllByArticleOwner(case2.getOwner());
        Assertions.assertThat(expectedCase.size()).isEqualTo(2);
        Assertions.assertThat(expectedCase).containsExactlyInAnyOrder(case1, case2);
    }


    @Test
    public void customQueryFindAllByArticleOwnerIdShouldReturnCaseWithCorrespondingArticleOwner() {
        final User owner1 = new User();
        final User owner2 = new User();
        users.saveAll(Arrays.asList(owner1, owner2));

        case2.getArticle().setOwner(owner2);
        case1.getArticle().setOwner(owner1);

        final List<Case> expectedCase = cases.findAllByArticleOwnerId(case2.getOwner().getId());
        Assertions.assertThat(expectedCase.size()).isOne();
        Assertions.assertThat(expectedCase.get(0)).isEqualTo(case2);
    }

    @Test
    public void customQueryFindAllByArticleOwnerIdShouldReturnTwoCaseWithCorrespondingArticleOwnerOrderedByRequestStatusAsc() {
        case1.setRequestStatus(Case.RUNNING);
        case2.setRequestStatus(Case.RUNNING_EMAILSENT);
        final User user = new User();
        users.save(user);

        case2.getArticle().setOwner(user);
        case1.getArticle().setOwner(user);

        final List<Case> expectedCase = cases.findAllByArticleOwnerId(case2.getOwner().getId());
        Assertions.assertThat(expectedCase.size()).isEqualTo(2);
        Assertions.assertThat(expectedCase).containsExactly(case1, case2);
    }

}
