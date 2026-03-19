package com.xiaoluo.syservice.article.exception;

public class ArticleNotFoundException extends RuntimeException {

    public ArticleNotFoundException(Integer id) {
        super("Article not found for id: " + id);
    }
}
