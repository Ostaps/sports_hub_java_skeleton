package org.softserveinc.java_be_genai_plgrnd.controllers;

import java.util.List;

import org.softserveinc.java_be_genai_plgrnd.dtos.business.ArticleDTO;
import org.softserveinc.java_be_genai_plgrnd.dtos.business.CreateArticleDTO;
import org.softserveinc.java_be_genai_plgrnd.dtos.request.CreateArticleRequest;
import org.softserveinc.java_be_genai_plgrnd.dtos.response.ArticleResponse;
import org.softserveinc.java_be_genai_plgrnd.services.ArticleService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@Tag(name = "Article", description = "Articles API")
@RestController
@RequestMapping(path = "/api/articles", produces = APPLICATION_JSON_VALUE)
@Validated
public class ArticleController {
    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @Operation(summary = "Find all articles with comments")
    @GetMapping
    public ResponseEntity<List<ArticleResponse>> findAllWithComments() {
        List<ArticleDTO> articles = articleService.findAllWithComments();
        List<ArticleResponse> responses = articles.stream()
            .map(ArticleResponse::fromDTO)
            .toList();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Find an article with comments by id")
    @GetMapping(value = "/{id}")
    public ResponseEntity<ArticleResponse> findArticleById(
        @PathVariable String id
    ) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        String articleId = id.toString();
        return ResponseEntity.ok(ArticleResponse.fromDTO(articleService.findById(articleId)));
    }

    @Operation(summary = "Create an article")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<ArticleResponse> createArticle(
        @RequestBody @Valid CreateArticleRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        CreateArticleDTO dto = CreateArticleDTO.fromRequest(request);
        ArticleDTO article = articleService.createArticle(dto);
        ArticleResponse response = ArticleResponse.fromDTO(article);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update an article")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    public ResponseEntity<ArticleResponse> updateArticle(
            @PathVariable String id,
            @RequestBody @Valid CreateArticleRequest request
    ) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        String articleId = id.toString();
        CreateArticleDTO dto = CreateArticleDTO.fromRequest(request);
        ArticleDTO article = articleService.updateArticle(articleId, dto);
        ArticleResponse response = ArticleResponse.fromDTO(article);
        return ResponseEntity.ok(response);
    }
}
