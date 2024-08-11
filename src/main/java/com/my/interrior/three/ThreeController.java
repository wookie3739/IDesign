package com.my.interrior.three;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ThreeController {

	@GetMapping("/auth/three")
	public String goToThreeDashBoard() {
		return "three";
	}
}
