package com.xiaoluo.syservice.article.service;

import com.xiaoluo.syservice.article.dto.ArticleResponse;
import com.xiaoluo.syservice.article.exception.ArticleNotFoundException;
import com.xiaoluo.syservice.article.repository.ArticleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ArticleService {

    private static final Logger log = LoggerFactory.getLogger(ArticleService.class);

    private final ArticleRepository articleRepository;

    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    public ArticleResponse getById(Integer id) {
        log.info("Loading article from database, id={}", id);
        return articleRepository.findById(id)
                .orElseThrow(() -> {
                    log.info("Article was not found, id={}", id);
                    return new ArticleNotFoundException(id);
                });
    }
}
