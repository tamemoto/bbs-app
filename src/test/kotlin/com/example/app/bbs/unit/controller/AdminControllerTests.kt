package com.example.app.bbs.unit.controller

import com.example.app.bbs.app.controller.AdminController
import com.example.app.bbs.domain.entity.Article
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.context.jdbc.Sql

@ExtendWith(SpringExtension::class)
@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTests {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var target: AdminController

    @Test
    fun noAuthenticationTest() {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/admin/index")
        )
                .andExpect(status().is3xxRedirection)
                .andExpect(redirectedUrlPattern("**/login"))
    }

    @Test
    @WithMockUser(username = "admin")
    fun authenticationTest() {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/admin/index")
        )
                .andExpect(status().isOk)
                .andExpect(model().attributeExists("pages"))
                .andExpect(model().attributeExists("isAdmin"))
                .andExpect(view().name("admin_index"))
    }

    @Test
    @Sql(statements = ["INSERT INTO article (name, title, contents, article_key) VALUES ('test', 'test', 'test', 'test');"])
    @WithMockUser(username = "admin")
    fun singleDeleteExistsArticleTest() {
        var latestArticle: Article = target.articleRepository.findAll().last()

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/admin/article/delete/" + latestArticle.id)
                        .with(csrf())
        )
                .andExpect(status().is3xxRedirection)
                .andExpect(view().name("redirect:/admin/index"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(flash().attribute("message", target.MESSAGE_DELETE_NORMAL))
    }

    @Test
    @WithMockUser(username = "admin")
    fun multiDeleteNotSelectedArticleTest() {
        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/admin/article/deletes")
                        .with(csrf())
        )
                .andExpect(status().is3xxRedirection)
                .andExpect(view().name("redirect:/admin/index"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(flash().attribute("message", target.MESSAGE_ARTICLE_NOT_SELECTED))
    }

    @Test
    @Sql(statements = [
        "INSERT INTO article(name, title, contents, article_key) VALUES ('test', 'test', 'test', 'test');",
        "INSERT INTO article(name, title, contents, article_key) VALUES ('test', 'test', 'test', 'test');",
        "INSERT INTO article(name, title, contents, article_key) VALUES ('test', 'test', 'test', 'test');",
    ])
    @WithMockUser(username = "admin")
    fun multiDeleteSelectedArticleTest() {
        val latestArticles: List<Article> = target.articleRepository.findAll()
        val ids = latestArticles.map { it.id }.joinToString(",")

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/admin/article/deletes")
                        .with(csrf())
                        .param("article_checks", ids)
        )
                .andExpect(status().is3xxRedirection)
                .andExpect(view().name("redirect:/admin/index"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(flash().attribute("message", target.MESSAGE_DELETE_NORMAL))
    }
}