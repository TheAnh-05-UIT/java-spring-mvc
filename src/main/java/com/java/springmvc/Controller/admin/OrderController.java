package com.java.springmvc.Controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class OrderController {

    @RequestMapping("/admin/order")
    public String getDashboardAdmin() {
        return "admin/order/view-order";
    }
}
