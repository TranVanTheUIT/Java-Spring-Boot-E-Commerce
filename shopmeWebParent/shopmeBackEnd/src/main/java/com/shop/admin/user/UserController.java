package com.shop.admin.user;

import com.shop.commom.entity.Role;
import com.shop.commom.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class UserController {
    @Autowired
    private UserService service;

    @GetMapping("/users")
    public String listAll(Model model) {
        List<User> listUsers = service.listAll();
        model.addAttribute("listUsers", listUsers);

        return "users";
    }

    @GetMapping("/users/new")
    public String newUser(Model model) {

        List<Role> listRoles = service.listRoles();
        model.addAttribute("listRoles", listRoles);
        User user = new User();
        user.setEnabled(true);
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Create New User"); // title of page

        return "user_form";
    }

    @PostMapping("/users/save")
    public String saveUser(User user, RedirectAttributes redirectAttributes) {
        System.out.println(user);
        service.save(user);
        // message notify create user success
        redirectAttributes.addFlashAttribute("message", "The user has been saved successfully.");
        return "redirect:/users";
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            User user = service.get(id);
            List<Role> listRoles = service.listRoles();
            model.addAttribute("listRoles", listRoles);
            model.addAttribute("user", user);
            model.addAttribute("pageTitle", "Edit User");
            return "user_form";
        } catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/users";
        }
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            service.delete(id);
            redirectAttributes.addFlashAttribute("message", "The user ID " + id + " has been deleted successfully.");
        } catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());

        }

        return "redirect:/users";
    }

}
