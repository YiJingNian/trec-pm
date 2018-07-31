package at.medunigraz.imi.bst.pmclassifier;

import at.medunigraz.imi.bst.trec.model.Topic;

public class TrecInstance {
    private Topic topic;
    private Document document;
    private String pmLabel;
    private int relevanceLabel;

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public String getPmLabel() {
        return pmLabel;
    }

    public void setPmLabel(String pmLabel) {
        this.pmLabel = pmLabel;
    }

    public int getRelevanceLabel() {
        return relevanceLabel;
    }

    public void setRelevanceScore(int relevanceLabel) {
        this.relevanceLabel = relevanceLabel;
    }
}
