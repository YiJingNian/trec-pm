package at.medunigraz.imi.bst.pmclassifier;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Document {
    @JsonProperty("pubmedId")
    private String id;
    private String title;
    @JsonProperty("abstract")
    private String abstractText;
    private List<String> genes;
    private List<String> organisms;
    private List<String> meshTags;
    private List<String> meshTagsMajor;

    public List<String> getMeshTags() {
        return meshTags;
    }

    public void setMeshTags(List<String> meshTags) {
        this.meshTags = meshTags;
    }

    public List<String> getMeshTagsMajor() {
        return meshTagsMajor;
    }

    public void setMeshTagsMajor(List<String> meshTagsMajor) {
        this.meshTagsMajor = meshTagsMajor;
    }

    public List<String> getMeshMinor() {
        return meshMinor;
    }

    public void setMeshMinor(List<String> meshMinor) {
        this.meshMinor = meshMinor;
    }

    private List<String> meshMinor;

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
