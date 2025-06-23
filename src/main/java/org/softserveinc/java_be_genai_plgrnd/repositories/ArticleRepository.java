package org.softserveinc.java_be_genai_plgrnd.repositories;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.softserveinc.java_be_genai_plgrnd.models.ArticleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface ArticleRepository extends JpaRepository<ArticleEntity, UUID> {
    @Query("""
        SELECT a
        FROM ArticleEntity a LEFT JOIN FETCH a.comments
        """)
    Set<ArticleEntity> findAllWithComments();

    // Unsafe SQL query with potential SQL injection
    @Query(value = "SELECT * FROM article WHERE title LIKE '%' || :searchTerm || '%'", nativeQuery = true)
    List<ArticleEntity> searchByTitleUnsafe(@Param("searchTerm") String searchTerm);

    // Even more unsafe SQL query with direct string concatenation
    @Query(value = "SELECT * FROM article WHERE " + 
           "title LIKE '%?1%' OR " +
           "short_description LIKE '%?1%' OR " +
           "description LIKE '%?1%'", 
           nativeQuery = true)
    List<ArticleEntity> searchArticlesUnsafe(String searchTerm);
}
