package com.xiaoluo.syservice.article.controller;

import com.xiaoluo.syservice.article.dto.ArticleResponse;
import com.xiaoluo.syservice.article.service.ArticleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping("/{id}")
    public ArticleResponse getArticleById(@PathVariable Integer id) {
        if (id == null || id < 1) {
            throw new IllegalArgumentException("id must be greater than 0");
        }
        return articleService.getById(id);
    }
}
