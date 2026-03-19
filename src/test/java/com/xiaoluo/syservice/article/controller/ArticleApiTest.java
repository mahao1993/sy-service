package com.xiaoluo.syservice.article.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.xiaoluo.syservice.article.dto.ArticleResponse;
import com.xiaoluo.syservice.article.exception.ArticleNotFoundException;
import com.xiaoluo.syservice.article.service.ArticleService;
import com.xiaoluo.syservice.common.exception.GlobalExceptionHandler;
import com.xiaoluo.syservice.security.apikey.ApiKeyAuthFilter;
import com.xiaoluo.syservice.security.apikey.ApiKeyProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ArticleController.class)
@Import({ApiKeyAuthFilter.class, GlobalExceptionHandler.class, ArticleApiTest.TestConfig.class})
@TestPropertySource(properties = {
        "security.api-key.header-name=X-API-Key",
        "security.api-key.value=test-api-key"
})
class ArticleApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ArticleService articleService;

    @Test
    void shouldRejectRequestWithoutApiKey() throws Exception {
        mockMvc.perform(get("/api/articles/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid API key."));
    }

    @Test
    void shouldReturnArticleWhenApiKeyIsValid() throws Exception {
        given(articleService.getById(1))
                .willReturn(new ArticleResponse(1, "test title", "test content", "2026-03-19 10:30:00"));

        mockMvc.perform(get("/api/articles/1")
                        .header("X-API-Key", "test-api-key"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("test title"))
                .andExpect(jsonPath("$.content").value("test content"))
                .andExpect(jsonPath("$.publishTime").value("2026-03-19 10:30:00"));
    }

    @Test
    void shouldReturnNotFoundWhenArticleDoesNotExist() throws Exception {
        given(articleService.getById(999))
                .willThrow(new ArticleNotFoundException(999));

        mockMvc.perform(get("/api/articles/999")
                        .header("X-API-Key", "test-api-key"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Article not found for id: 999"));
    }

    @TestConfiguration
    @EnableConfigurationProperties(ApiKeyProperties.class)
    static class TestConfig {
    }
}
