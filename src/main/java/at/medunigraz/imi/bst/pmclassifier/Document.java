package at.medunigraz.imi.bst.pmclassifier;

import java.util.List;

public class Document {
    private String id;
    private String title;
    private String abstractText;
    private List<String> genes;
    private List<String> organisms;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAbstractText() {
        return abstractText;
    }

    public void setAbstractText(String abstractText) {
        this.abstractText = abstractText;
    }

    public List<String> getGenes() {
        return genes;
    }

    public void setGenes(List<String> genes) {
        this.genes = genes;
    }

    public List<String> getOrganisms() {
        return organisms;
    }

    public void setOrganisms(List<String> organisms) {
        this.organisms = organisms;
    }
}
