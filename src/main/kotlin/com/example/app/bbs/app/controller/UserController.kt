package com.example.app.bbs.app.controller

import com.example.app.bbs.app.request.ArticleRequest
import com.example.app.bbs.app.service.UserDetailsImpl
import com.example.app.bbs.domain.repository.ArticleRepository
import com.example.app.bbs.app.service.UserManagerServiceImpl
import com.example.app.bbs.app.validator.UserValidator
import com.example.app.bbs.domain.entity.Article
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
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.RequestParam

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import java.util.*


@Controller
class UserController {

    val PAGE_SIGE: Int = 10
    val MESSAGE_REGISTER_NORMAL = "正常に投稿できました"

    @Autowired
    lateinit var  articleRepository: ArticleRepository

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
    fun getUserIndex(
            @ModelAttribute articleRequest: ArticleRequest,
            @AuthenticationPrincipal userDetailsImpl: UserDetailsImpl,
            @RequestParam(value = "page",
                    defaultValue = "0",
                    required = false) page: Int,
            model: Model
    ): String {
        model.addAttribute("user", userDetailsImpl.user)
        val pageable: Pageable = PageRequest.of(
                page,
                this.PAGE_SIGE,
                Sort.by(Sort.Direction.DESC, "updateAt")
                        .and(Sort.by(Sort.Direction.ASC, "id"))
        )

        val articles: Page<Article> = articleRepository.findAllByUserId(userDetailsImpl.user.id, pageable)
        model.addAttribute("pages", articles)

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

    @PostMapping("/user/article/register")
    fun userArticleRegister(
            @Validated @ModelAttribute articleRequest: ArticleRequest,
            result: BindingResult,
            @AuthenticationPrincipal userDetailsImpl: UserDetailsImpl,
            redirectAttributes: RedirectAttributes
    ): String {
        if(result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", result)
            redirectAttributes.addFlashAttribute("request", articleRequest)

            return "redirect:/user/index"
        }

        articleRepository.save(
                Article(
                        articleRequest.id,
                        articleRequest.name,
                        articleRequest.title,
                        articleRequest.contents,
                        articleRequest.articleKey,
                        Date(),
                        Date(),
                        userDetailsImpl.user.id
                )
        )

        redirectAttributes.addFlashAttribute("message", MESSAGE_REGISTER_NORMAL)

        return "redirect:/user/index"
    }
}