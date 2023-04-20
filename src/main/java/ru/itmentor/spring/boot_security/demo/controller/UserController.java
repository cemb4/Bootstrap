package ru.itmentor.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.itmentor.spring.boot_security.demo.model.Role;
import ru.itmentor.spring.boot_security.demo.model.User;
import ru.itmentor.spring.boot_security.demo.service.RegistrationService;
import ru.itmentor.spring.boot_security.demo.service.RoleService;
import ru.itmentor.spring.boot_security.demo.service.UserService;


import javax.validation.Valid;
import java.util.*;


@Controller
public class UserController {
    private final UserService userService;
    private final RegistrationService registrationService;

    private final RoleService roleService;


    @Autowired
    public UserController(UserService userService, RegistrationService registrationService, RoleService roleService) {
        this.userService = userService;
        this.registrationService = registrationService;
        this.roleService = roleService;
    }

//    @RequestMapping("/user")
//    public String getUserPage(Model model) {
//        model.addAttribute("users", userService.findAll());
//        return "user";
//    }
//
//    @RequestMapping("/admin")
//    public String getAdminPage(Model model) {
//        model.addAttribute("users", userService.findAll());
//        return "admin";
//    }
//
//    @GetMapping("/registration")
//    public String registrationPage(Model model) {
//        model.addAttribute("user", new User());
//        return "registration";
//    }
//
//    @PostMapping("/registration")
//    public String performRegistration(@ModelAttribute("user") @Valid User user,
//                                      BindingResult bindingResult, Authentication auth) {
//        if (bindingResult.hasErrors())
//            return "registration";
//        registrationService.register(user);
//        return userAdminRedirect(auth);
//    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model) {
        model.addAttribute("user", userService.findOne(id));
        return "admin/adminPage";
    }

    @GetMapping("/add")
    public String newUser(Model model, Authentication authentication) {
        model.addAttribute("user", new User());
        model.addAttribute("authentication", authentication);
        model.addAttribute("roles", roleService.findAll());
        return "admin/newUser";
    }

    @PostMapping("/new")
    public String create(@ModelAttribute("user") @Valid User user, @RequestParam("selectedRolesForNewUser") ArrayList<Role> roles,
                         BindingResult bindingResult,
                         Authentication auth) {
        if (bindingResult.hasErrors())
            return "new";
        HashSet<Role> role = new HashSet<>(roles);
        user.setRoles(role);
        userService.save(user);
        return userAdminRedirect(auth);
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
        ;
        model.addAttribute("user", userService.findOne(id));
        return "admin/adminPage";
    }

    //    @PatchMapping("/{id}/update")
//    public String update(@ModelAttribute("user") @Valid User user,
//                         BindingResult bindingResult,
//                         @PathVariable("id") int id,
//                         Authentication auth) {
//        if (bindingResult.hasErrors())
//            return "/update";
//        userService.update(id, user);
//        return userAdminRedirect(auth);
//
//    }
    @PatchMapping("/{id}/update")
    public String update(@ModelAttribute("user") @Valid User user, @RequestParam(value = "selectedRoles", defaultValue = "2")
    ArrayList<Role> roles,
                         BindingResult bindingResult,
                         @PathVariable("id") int id,
                         Authentication auth) {
        if (bindingResult.hasErrors())
            return "update";
        HashSet<Role> roles1 = new HashSet<>(roles);
        user.setRoles(roles1);
        userService.update(id, user);
        return userAdminRedirect(auth);

    }

    @DeleteMapping("/{id}/delete")
    public String delete(@PathVariable("id") int id, Authentication auth) {
        userService.delete(id);
        return userAdminRedirect(auth);
    }

    private String userAdminRedirect(Authentication auth) {
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        Optional<? extends GrantedAuthority> roleAdmin = authorities.stream()
                .filter(a -> Objects.equals("ROLE_ADMIN", a.getAuthority()))
                .findAny();
        if (roleAdmin.isPresent()) return "redirect:/adminPage";
        else return "redirect:/login";
    }
}