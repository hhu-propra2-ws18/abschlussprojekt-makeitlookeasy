package de.propra2.ausleiherino24.service;


import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class RoleServiceTest {

  private MockHttpServletRequest requestMock;

  @Before
  public void init() {
    requestMock = new MockHttpServletRequest();
  }

  @Test
  public void getUserRoleTest() {
    requestMock.addUserRole("user");

    Assertions.assertThat(RoleService.getUserRole(requestMock)).isEqualTo("user");
  }

  @Test
  public void getUserRoleTest2() {
    requestMock.addUserRole("admin");

    Assertions.assertThat(RoleService.getUserRole(requestMock)).isEqualTo("admin");
  }

  @Test
  public void getUserRoleTest3() {
    requestMock.addUserRole("");

    Assertions.assertThat(RoleService.getUserRole(requestMock)).isEqualTo("");
  }
}
