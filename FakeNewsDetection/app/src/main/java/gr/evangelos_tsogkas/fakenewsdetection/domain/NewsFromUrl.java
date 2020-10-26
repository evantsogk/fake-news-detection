package gr.evangelos_tsogkas.fakenewsdetection.domain;

import java.io.Serializable;

/**
 * News object created from news url.
 */
public class NewsFromUrl extends News implements Serializable {
    private String news_url, news_image;

    public NewsFromUrl() {}

    /**
     * Constructor.
     * @param news_url the news url.
     */
    public NewsFromUrl(String news_url) {
        this.news_url = news_url;
    }

    /**
     *
     * @return the news url.
     */
    public String getNews_url() {
        return news_url;
    }

    /**
     *
     * @param news_url the news url.
     */
    public void setNews_url(String news_url) {
        this.news_url = news_url;
    }

    /**
     *
     * @return the news image url.
     */
    public String getNews_image() {
        return news_image;
    }

    /**
     *
     * @param news_image the news image url.
     */
    public void setNews_image(String news_image) {
        this.news_image = news_image;
    }
}
