package org.softserveinc.java_be_genai_plgrnd.services;

import java.util.List;

import org.softserveinc.java_be_genai_plgrnd.dtos.business.ArticleDTO;
import org.softserveinc.java_be_genai_plgrnd.dtos.business.CreateArticleDTO;

public interface ArticleService {
    List<ArticleDTO> findAllWithComments();

    ArticleDTO findById(String id);

    ArticleDTO createArticle(CreateArticleDTO createArticleDTO);

    ArticleDTO updateArticle(String id, CreateArticleDTO updateDTO);

    ArticleDTO getArticleById(String id);
}
