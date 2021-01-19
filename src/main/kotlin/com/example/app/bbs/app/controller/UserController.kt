package com.example.app.bbs.app.controller

import com.example.app.bbs.app.service.UserManagerServiceImpl
import com.example.app.bbs.app.validator.UserValidator
import com.example.app.bbs.domain.entity.User
import com.example.app.bbs.domain.entity.UserRole
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
class UserController {

    @Autowired
    lateinit var userManagerServiceImpl: UserManagerServiceImpl

    @Autowired
    lateinit var userValidator: UserValidator

    @GetMapping("/user/login")
    fun getUserLogin(): String {
        return "user_login"
    }

    @GetMapping("/user/signup")
    fun getUserSignup(
            @ModelAttribute user: User,
            model: Model,
            redirectAttributes: RedirectAttributes
    ): String {
        model.addAttribute("user_role", UserRole.USER.name)

        if(model.containsAttribute("errors")) {
            val key: String = BindingResult.MODEL_KEY_PREFIX + "user"
            model.addAttribute(key, model.asMap()["errors"])
        }

        if(model.containsAttribute("request")) {
            model.addAttribute("user", model.asMap()["request"])
        }
        return "user_signup"
    }

    @PostMapping("/user/signup")
    fun userSignup(
            @Validated @ModelAttribute user: User,
            result: BindingResult,
            model: Model,
            redirectAttributes: RedirectAttributes
    ): String {
        userValidator.validate(user, result)

        if(result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", result)
            redirectAttributes.addFlashAttribute("request", user)
            return "redirect:/user/signup"
        }
        userManagerServiceImpl.registerUser(user, user.password)
        return "redirect:/user/login"
    }

    @GetMapping("/user/index")
    fun getUserIndex(): String {
        return "user_index"
    }

    @PostMapping("/user/login/auth")
    fun userLogin(): String {
        return "redirect:/user/index"
    }

    @GetMapping("/user/logout")
    fun getUserLogout(): String {
        return "redirect:/"
    }
}