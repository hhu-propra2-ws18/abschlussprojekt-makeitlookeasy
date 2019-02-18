package de.propra2.ausleiherino24.web;

import de.propra2.ausleiherino24.service.UserService;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


/**
 * Adminseite.
 */
@Controller
@RequestMapping("/accessed/admin")
public class AdminController {

  private final UserService userService;

  public AdminController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("index")
  public ModelAndView getAdminIndex(Principal principal, Model model) throws Exception {
    model.addAttribute("user", userService.findUserByPrincipal(principal));
    return new ModelAndView("accessed/admin/index");
  }

}
