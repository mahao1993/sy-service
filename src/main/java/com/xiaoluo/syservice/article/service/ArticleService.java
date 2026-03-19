package com.xiaoluo.syservice.article.service;

import com.xiaoluo.syservice.article.dto.ArticleResponse;
import com.xiaoluo.syservice.article.exception.ArticleNotFoundException;
import com.xiaoluo.syservice.article.repository.ArticleRepository;
import org.springframework.stereotype.Service;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;

    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    public ArticleResponse getById(Integer id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new ArticleNotFoundException(id));
    }
}
