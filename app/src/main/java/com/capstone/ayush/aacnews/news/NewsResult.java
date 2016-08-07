package com.capstone.ayush.aacnews.news;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ayush on 06-08-2016.
 */
public class NewsResult {
    private String status;
    private String source;
    private String sortBy;
    private List<Articles> articles = new ArrayList<Articles>();

    public List<Articles> getArticles() {
        return articles;
    }

    public void setArticles(List<Articles> articles) {
        this.articles = articles;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
