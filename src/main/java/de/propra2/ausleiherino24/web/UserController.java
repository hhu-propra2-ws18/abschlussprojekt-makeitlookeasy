package de.propra2.ausleiherino24.web;

import de.propra2.ausleiherino24.data.UserRepository;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/accessed/user")
public class UserController {
	/*
	UserController manages all requests that are exclusively available to logged-in users of the platform, except article/case handling.
	This includes profile management.
	 */

	private final UserRepository userRepository;
	private final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

	@Autowired
	public UserController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@GetMapping("/user")
	public ModelAndView displayUserProfile(@RequestParam("id") Long id) {
		User user = userRepository.getById(id);

		ModelAndView mav = new ModelAndView("profile");
		mav.addObject("user", user);
		return mav;
	}

	@GetMapping("/index")
	public String getIndex(HttpServletRequest request, Model model) {
		model.addAttribute("role", RoleService.getUserRole(request));
		return "/accessed/user/index";
	}

	@GetMapping("/profil")
	public String getProfil(HttpServletRequest request, Model model) {
		model.addAttribute("role", RoleService.getUserRole(request));
		return "/accessed/user/profiledit";
	}

	@GetMapping("/profil2")
	public String getProfi2l(HttpServletRequest request, Model model) {
		model.addAttribute("role", RoleService.getUserRole(request));
		return "/accessed/user/profile";
	}
}
