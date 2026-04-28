package com.java.springmvc.Controller.client;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.java.springmvc.domain.Product;
import com.java.springmvc.domain.User;
import com.java.springmvc.domain.dto.RegisterDTO;
import com.java.springmvc.service.ProductService;
import com.java.springmvc.service.UserService;

import jakarta.validation.Valid;

@Controller
public class HomePageController {

    private final ProductService productService;
    private final UserService userService;

    public HomePageController(ProductService productService,
            UserService userService) {
        this.productService = productService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String getHomePage(Model model) {
        List<Product> listProducts = this.productService.handleGetAllProduct();
        model.addAttribute("products", listProducts);
        return "client/home/view-home";
    }

    @GetMapping("/register")
    public String getRegisterPage(Model model) {
        model.addAttribute("register", new RegisterDTO());
        return "client/auth/register";
    }

    @PostMapping("/register")
    public String RegisterForm(Model model,
            @ModelAttribute("register") @Valid RegisterDTO registerDTO,
            BindingResult registerUserBindingResult) {
        if (registerUserBindingResult.hasErrors()) {
            return "client/auth/register";
        }
        User user = this.userService.regiterDTOtoUser(registerDTO);
        user.setRole(this.userService.handleGetRoleByName("USER"));
        this.userService.handleCreateUser(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String getLoginPage(Model model) {
        return "client/auth/login";
    }
}
