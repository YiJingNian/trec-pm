package de.julielab.jcore.pmclassifer;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ClassifierInput {

    private String title;
    @JsonProperty("abstract")
    private String abstractText;
    @JsonProperty("major_mesh")
    private String meshMajor;
    @JsonProperty("minor_mesh")
    private String meshMinor;

    public ClassifierInput(String title, String abstractText, String meshMajor, String meshMinor) {
        this.title = title;
        this.abstractText = abstractText;
        this.meshMajor = meshMajor;
        this.meshMinor = meshMinor;
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

    public String getMeshMajor() {
        return meshMajor;
    }

    public void setMeshMajor(String meshMajor) {
        this.meshMajor = meshMajor;
    }

    public String getMeshMinor() {
        return meshMinor;
    }

    public void setMeshMinor(String meshMinor) {
        this.meshMinor = meshMinor;
    }
}
