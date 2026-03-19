package com.xiaoluo.syservice.article.repository;

import com.xiaoluo.syservice.article.dto.ArticleResponse;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ArticleRepository {

    private static final String FIND_BY_ID_SQL = """
            SELECT t.id, t.title, c.content, t.website_publish_time AS publish_time
            FROM table_title_insert t
            LEFT JOIN table_content_insert c ON c.title_id = t.id
            WHERE t.id = ?
            LIMIT 1
            """;

    private final JdbcTemplate jdbcTemplate;

    public ArticleRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<ArticleResponse> findById(Integer id) {
        return jdbcTemplate.query(
                FIND_BY_ID_SQL,
                rs -> rs.next()
                        ? Optional.of(new ArticleResponse(
                                rs.getInt("id"),
                                rs.getString("title"),
                                rs.getString("content"),
                                rs.getString("publish_time")))
                        : Optional.empty(),
                id
        );
    }
}
