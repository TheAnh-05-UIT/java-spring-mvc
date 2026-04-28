package com.java.springmvc.Controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.core.model.Model;

@Controller
public class DashboardController {

    @GetMapping("/admin")
    public String getDashboardAdmin(Model model) {
        return "admin/dashboard/view-dashboard";
    }
}
