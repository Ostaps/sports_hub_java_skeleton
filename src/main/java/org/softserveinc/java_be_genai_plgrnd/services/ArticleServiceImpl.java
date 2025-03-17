package org.softserveinc.java_be_genai_plgrnd.services;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.softserveinc.java_be_genai_plgrnd.dtos.business.ArticleDTO;
import org.softserveinc.java_be_genai_plgrnd.dtos.business.CreateArticleDTO;
import org.softserveinc.java_be_genai_plgrnd.exception.ResourceNotFoundException;
import org.softserveinc.java_be_genai_plgrnd.models.ArticleEntity;
import org.softserveinc.java_be_genai_plgrnd.models.ImageStorageEntity;
import org.softserveinc.java_be_genai_plgrnd.models.ReactionEntity;
import org.softserveinc.java_be_genai_plgrnd.models.enums.ContentType;
import org.softserveinc.java_be_genai_plgrnd.repositories.ArticleRepository;
import org.softserveinc.java_be_genai_plgrnd.repositories.ReactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ArticleServiceImpl implements ArticleService {
    private final ArticleRepository articleRepository;
    private final ReactionRepository reactionRepository;

    public ArticleServiceImpl(
        ArticleRepository articleRepository,
        ReactionRepository reactionRepository
    ) {
        this.articleRepository = articleRepository;
        this.reactionRepository = reactionRepository;
    }

    /**
     * Find all articles with comments.
     *
     * @return list of articles with comments sorted by creation timestamp in descending order
     */
    @Override
    @Transactional(readOnly = true)
    public List<ArticleDTO> findAllWithComments() {
        final var articles = articleRepository.findAllWithComments();
        final var articleReactions = reactionRepository
            .findAllByContentType(ContentType.ARTICLE)
            .stream()
            .collect(Collectors.groupingBy(ReactionEntity::getContentId));

        return articles.stream()
            .map(article -> ArticleDTO.fromEntity(article, articleReactions.get(article.getId())))
            .sorted((a1, a2) -> a2.creationTimestamp().compareTo(a1.creationTimestamp()))
            .toList();
    }

    /**
     * Find article by id.
     *
     * @param id article id
     * @return article
     * @throws ResourceNotFoundException if article not found
     */
    @Override
    public ArticleDTO findById(String id) {
        return articleRepository.findById(UUID.fromString(id))
            .map(article -> {
                final var articleReactions = reactionRepository
                    .findAllByContentTypeAndContentId(ContentType.ARTICLE, article.getId())
                    .stream()
                    .toList();
                return ArticleDTO.fromEntity(article, articleReactions);
            })
            .orElseThrow(() -> new ResourceNotFoundException("Article", id));
    }

    /**
     * Create article.
     *
     * @param createArticleDTO article data
     * @return created article
     */
    @Override
    @Transactional
    public ArticleDTO createArticle(CreateArticleDTO createArticleDTO) {
        final var image = new ImageStorageEntity();
        image.setImage(createArticleDTO.image());

        final var article = new ArticleEntity();
        article.setTitle(createArticleDTO.title());
        article.setShortDescription(createArticleDTO.shortDescription());
        article.setDescription(createArticleDTO.description());
        article.setImageStorage(image);
        return ArticleDTO.fromEntity(articleRepository.save(article), Collections.emptyList());
    }
}
