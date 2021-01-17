package com.example.app.bbs.domain.repository

import com.example.app.bbs.domain.entity.Article
import org.springframework.data.jpa.repository.JpaRepository
import javax.transaction.Transactional

interface ArticleRepository: JpaRepository<Article, Int> {
    @Transactional
    fun deleteByIdIn(ids: List<Int>)
}