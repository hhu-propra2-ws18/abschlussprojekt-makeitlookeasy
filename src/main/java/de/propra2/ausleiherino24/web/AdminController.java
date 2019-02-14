package de.propra2.ausleiherino24.web;

import de.propra2.ausleiherino24.service.RoleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;


/**
 * Adminseite
 *
 */
@Controller
@RequestMapping("/accessed/admin")
public class AdminController {

	@GetMapping("index")
	public ModelAndView getAdminIndex(HttpServletRequest request, Model model){
		model.addAttribute("role", RoleService.getUserRole(request));
		return new ModelAndView("accessed/admin/index");
	}
	
}
