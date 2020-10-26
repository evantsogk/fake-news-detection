package gr.evangelos_tsogkas.fakenewsdetection.domain;

import java.io.Serializable;

/**
 * Contains the news evaluation results provided by the models trained on two datasets.
 */
public class Evaluation implements Serializable {
    private int label_politifact, label_gossipcop;
    private float score_politifact, score_gossipcop;

    public Evaluation() {}

    /**
     * Constructor.
     * @param label_politifact PolitiFact label: 0 (fake news) or 1 (real news)
     * @param score_politifact PolitiFact score (probability)
     * @param label_gossipcop GossipCop label: 0 (fake news) or 1 (real news)
     * @param score_gossipcop GossipCop score (probability)
     */
    public Evaluation(int label_politifact, float score_politifact, int label_gossipcop, float score_gossipcop) {
        this.label_politifact = label_politifact;
        this.score_politifact = score_politifact;
        this.label_gossipcop = label_gossipcop;
        this.score_gossipcop = score_gossipcop;
    }

    /**
     * Converts a probability to a rounded percentage value.
     * @param score a probability value.
     * @return the rounded percentage value.
     */
    public float percentage(float score) {
        return Math.round(score*100);
    }

    /**
     *
     * @return the PolitiFact label.
     */
    public int getLabel_politifact() {
        return label_politifact;
    }

    /**
     *
     * @return the PolitiFact score.
     */
    public float getScore_politifact() {
        return score_politifact;
    }

    /**
     *
     * @return the GossipCop label.
     */
    public int getLabel_gossipcop() {
        return label_gossipcop;
    }

    /**
     *
     * @return the GossipCop score.
     */
    public float getScore_gossipcop() {
        return score_gossipcop;
    }

    /**
     *
     * @param label_politifact the PolitiFact label.
     */
    public void setLabel_politifact(int label_politifact) {
        this.label_politifact = label_politifact;
    }

    /**
     *
     * @param score_politifact the PolitiFact score.
     */
    public void setScore_politifact(float score_politifact) {
        this.score_politifact = score_politifact;
    }

    /**
     *
     * @param label_gossipcop the GossipCop label.
     */
    public void setLabel_gossipcop(int label_gossipcop) {
        this.label_gossipcop = label_gossipcop;
    }

    /**
     *
     * @param score_gossipcop the GossipCop score.
     */
    public void setScore_gossipcop(float score_gossipcop) {
        this.score_gossipcop = score_gossipcop;
    }
}

