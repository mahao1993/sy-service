package com.xiaoluo.syservice.article.controller;

import com.xiaoluo.syservice.article.dto.ArticleResponse;
import com.xiaoluo.syservice.article.service.ArticleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private static final Logger log = LoggerFactory.getLogger(ArticleController.class);

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping("/{id}")
    public ArticleResponse getArticleById(@PathVariable Integer id) {
        log.info("Received article query request, id={}", id);
        if (id == null || id < 1) {
            throw new IllegalArgumentException("id must be greater than 0");
        }
        ArticleResponse response = articleService.getById(id);
        log.info("Article query succeeded, id={}, publishTime={}", response.id(), response.publishTime());
        return response;
    }
}
