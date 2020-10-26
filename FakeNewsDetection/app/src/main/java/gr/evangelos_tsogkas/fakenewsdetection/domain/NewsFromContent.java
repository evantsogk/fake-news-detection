package gr.evangelos_tsogkas.fakenewsdetection.domain;

import java.io.Serializable;

/**
 * News object created from the provided news content.
 */
public class NewsFromContent extends News implements Serializable {
    private String image;

    public NewsFromContent(){}

    /**
     * Constructor.
     * @param text the news text content.
     * @param image the news attached image.
     */
    public NewsFromContent(String text, String image) {
        setText(text);
        this.image = image;
    }

    /**
     *
     * @return the news attached image.
     */
    public String getImage() {
        return image;
    }

    /**
     *
     * @param image the news attached image.
     */
    public void setImage(String image) {
        this.image = image;
    }
}
