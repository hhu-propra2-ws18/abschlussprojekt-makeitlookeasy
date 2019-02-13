package de.propra2.ausleiherino24.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AdminController {

	@GetMapping("index")
	public ModelAndView getAdminIndex(){
		ModelAndView mav = new ModelAndView("accessed/admin/index");
		return mav;
	}
	
}
