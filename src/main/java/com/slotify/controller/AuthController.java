package com.slotify.controller;

import com.slotify.model.User;
import com.slotify.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {   

    private final UserService userService;

    public AuthController(UserService userService) {  
        this.userService = userService;
    }

    @GetMapping("/")
    public String root(HttpSession session) {
        if (session.getAttribute("currentUser") != null) {
            return "redirect:/home";
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
public String loginPage(@RequestParam(required = false) String ok, Model model) {
    if ("registered".equals(ok)) {
        model.addAttribute("success", "Account created. Please sign in.");
    }
    return "login";
}

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        try {
            User user = userService.login(email, password);
            session.setAttribute("currentUser", user);
            return "redirect:/home";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "login";
        }
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String fullName,
                           @RequestParam String email,
                           @RequestParam String password,
                           Model model) {
        try {
            userService.register(fullName, email, password, "STUDENT");
            return "redirect:/login?ok=registered";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "register";
        }
    }

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "home";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}