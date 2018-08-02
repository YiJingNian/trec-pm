package at.medunigraz.imi.bst.pmclassifier;

import cc.mallet.classify.Classification;
import cc.mallet.classify.Classifier;
import cc.mallet.classify.MaxEntTrainer;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Label;

import java.io.File;

public class MalletClassifier {
    private Classifier classifier;
    private InstancePreparator instancePreparator;

    public InstancePreparator getInstancePreparator() {
        return instancePreparator;
    }

    public void setInstancePreparator(InstancePreparator instancePreparator) {
        this.instancePreparator = instancePreparator;
    }

    public void train(File documentJsonZip, File gsTable) throws DataReadingException {
        InstanceList instances = instancePreparator.getInstancesForGoldData(documentJsonZip, gsTable);
        train(instances);
    }

    public void train(InstanceList instances) {
        MaxEntTrainer maxEntTrainer = new MaxEntTrainer();
        classifier = maxEntTrainer.train(instances);
    }


    public String predict(Document document) {
        Instance instance = classifier.getInstancePipe().instanceFrom(new Instance(document, "unknown", document.getId(), ""));
        Classification classification = classifier.classify(instance);
        Label bestLabel = classification.getLabeling().getBestLabel();
        return (String) bestLabel.getEntry();
    }

    public Classifier getClassifier() {
        return classifier;
    }
}
