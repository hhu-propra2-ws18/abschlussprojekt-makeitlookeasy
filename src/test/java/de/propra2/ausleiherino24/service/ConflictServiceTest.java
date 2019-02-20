package de.propra2.ausleiherino24.service;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;

import de.propra2.ausleiherino24.data.ConflictRepository;
import de.propra2.ausleiherino24.email.EmailSender;
import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.Conflict;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.propayhandler.AccountHandler;

@RunWith(SpringRunner.class)
public class ConflictServiceTest {
	private AccountHandler accountHandler;
	private EmailSender emailSender;
	private ConflictRepository conflictRepository;
	private ConflictService conflictService;

	@Before
	public void init() {
		emailSender = Mockito.mock(EmailSender.class);
		conflictRepository = Mockito.mock(ConflictRepository.class);
		accountHandler = Mockito.mock(AccountHandler.class);
		conflictService = new ConflictService(conflictRepository, emailSender, accountHandler);
	}

	@Test
	public void test() {
		MockHttpServletRequest req = new MockHttpServletRequest();
		req.addUserRole("admin");
		Assertions.assertThat(conflictService.isUserAdmin(req)).isTrue();
	}

	@Test
	public void test2() {
		MockHttpServletRequest req = new MockHttpServletRequest();
		req.addUserRole("user");
		Assertions.assertThat(conflictService.isUserAdmin(req)).isFalse();
	}

	@Test(expected=Exception.class)
	public void test3() throws Exception {
		User user = null;
		Conflict c1 = new Conflict();
		Conflict c2 = new Conflict();

		conflictService.isConflictedArticleOwner(c1, user);
	}

	@Test
	public void test4() throws Exception {
		User user = new User();
		Article art = new Article();
		art.setOwner(user);
		Case ca = new Case();
		ca.setArticle(art);
		Conflict c1 = new Conflict();
		Conflict c2 = new Conflict();
		c1.setConflictedCase(ca);

		Assertions.assertThat(conflictService.isConflictedArticleOwner(c1, user)).isTrue();
	}

	@Test
	public void test5() throws Exception {
		User user = new User();
		User user2 = new User();
		Article art = new Article();
		art.setOwner(user);
		Case ca = new Case();
		ca.setArticle(art);
		Conflict c1 = new Conflict();
		Conflict c2 = new Conflict();
		c1.setConflictedCase(ca);

		Assertions.assertThat(conflictService.isConflictedArticleOwner(c1, user2)).isFalse();
	}

	@Test(expected=Exception.class)
	public void test6() throws Exception {
		User user = new User();
		User user2 = new User();
		user2.setUsername("user2");
		Article art = new Article();
		art.setOwner(user2);
		Case ca = new Case();
		ca.setArticle(art);
		Conflict c1 = new Conflict();
		Conflict c2 = new Conflict();
		c1.setConflictedCase(ca);
		c1.setConflictReporterUsername("user1");

		Mockito.when(conflictRepository.findById(1L)).thenReturn(Optional.of(c1));

		conflictService.getConflict(1L, user2);
	}


	@Test()
	public void test7() throws Exception {
		User user = new User();
		User user2 = new User();
		user.setUsername("user1");
		Article art = new Article();
		art.setOwner(user);
		Case ca = new Case();
		ca.setArticle(art);
		Conflict c1 = new Conflict();
		Conflict c2 = new Conflict();
		c1.setConflictedCase(ca);
		c1.setConflictReporterUsername("user1");

		Mockito.when(conflictRepository.findById(1L)).thenReturn(Optional.of(c1));

		Assertions.assertThat(conflictService.getConflict(1L, user)).isEqualTo(c1);
	}

	@Test(expected=Exception.class)
	public void test8() throws Exception {
		User user = new User();
		User user2 = new User();
		user.setUsername("user1");
		Article art = new Article();
		art.setOwner(user);
		Case ca = new Case();
		ca.setArticle(art);
		Conflict c1 = new Conflict();
		Conflict c2 = new Conflict();
		c1.setConflictedCase(ca);
		c1.setConflictReporterUsername("user1");

		Mockito.when(conflictRepository.findById(1L)).thenReturn(Optional.empty());
		conflictService.getConflict(1L, user);
	}

}
