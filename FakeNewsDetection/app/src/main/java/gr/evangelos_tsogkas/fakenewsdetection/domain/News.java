package gr.evangelos_tsogkas.fakenewsdetection.domain;


import java.io.Serializable;

/**
 * Abstract class for news object.
 */
public abstract class News implements Serializable {
    private String text;
    private Evaluation evaluation =  new Evaluation();

    /**
     *
     * @return the news text content.
     */
    public String getText() {
        return text;
    }

    /**
     *
     * @param text the news text content.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     *
     * @return the evaluation object of the news.
     */
    public Evaluation getEvaluation() {
        return evaluation;
    }

    /**
     *
     * @param evaluation the evaluation object of the news.
     */
    public void setEvaluation(Evaluation evaluation) {
        this.evaluation = evaluation;
    }
}
