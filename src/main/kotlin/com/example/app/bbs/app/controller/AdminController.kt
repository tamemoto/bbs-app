package com.example.app.bbs.app.controller

import com.example.app.bbs.domain.entity.Article
import com.example.app.bbs.domain.repository.ArticleRepository
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Pageable
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes


@Controller
class AdminController {
    var PAGE_SIZE: Int = 10
    var MESSAGE_ARTTCLE_DOES_NOT_EXIST = "対象の記事が見つかりませんでした"
    var MESSAGE_DELETE_NORMAL = "正常に動作しました"
    var ALERT_CLASS_ERROR = "alert-error"
    var MESSAGE_ARTICLE_NOT_SELECTED = "削除する記事を選択してください"

    @Autowired
    lateinit var articleRepository: ArticleRepository

    @GetMapping("/admin/index")
    fun getAdminIndex(@RequestParam(
            value = "page",
            defaultValue = "0",
            required = false
    ) page: Int, model: Model): String {
        val pageable: Pageable = PageRequest.of(
                page,
                PAGE_SIZE,
                Sort.by(Sort.Direction.DESC, "updateAt")
                        .and(Sort.by(Sort.Direction.ASC, "id"))
        )
        val articles: Page<Article> = articleRepository.findAll(pageable)
        model.addAttribute("pages", articles)
        model.addAttribute("isAdmin", true)

        return "admin_index"
    }

    @PostMapping("/admin/article/delete/{id}")
    fun deleteArticle(@PathVariable id: Int,
                      redirectAttributes: RedirectAttributes
    ) : String {
        if (!articleRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute("message", MESSAGE_ARTTCLE_DOES_NOT_EXIST)
            return "redirect:/admin/index"
        }

        articleRepository.deleteById(id)

        redirectAttributes.addFlashAttribute("message", MESSAGE_DELETE_NORMAL)
        return "redirect:/admin/index"
    }

    @PostMapping("/admin/article/deletes")
    fun deleteArticles(
            @RequestParam(value = "article_checks", required = false)
            checkboxValue: List<Int>?,
            redirectAttributes: RedirectAttributes
    ): String {
        if (checkboxValue == null || checkboxValue.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", MESSAGE_ARTICLE_NOT_SELECTED)
            redirectAttributes.addFlashAttribute("alert_class", ALERT_CLASS_ERROR)
            return "redirect:/admin/index"
        }

        articleRepository.deleteByIdIn(checkboxValue)
        redirectAttributes.addFlashAttribute("message", MESSAGE_DELETE_NORMAL)
        return "redirect:/admin/index"
    }
}