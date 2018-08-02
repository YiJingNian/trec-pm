package at.medunigraz.imi.bst.pmclassifier;

import cc.mallet.classify.Classification;
import cc.mallet.classify.Classifier;
import cc.mallet.classify.MaxEntTrainer;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Label;
import de.julielab.java.utilities.FileUtilities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.rmi.server.UID;

public class MalletClassifier implements Serializable{

    private static final long serialVersionUID = 2018_08_02L;

    private static final Logger LOG = LogManager.getLogger();
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

    public void writeClassifier(File destination) throws IOException {
        LOG.info("Writing classifier to " + destination.getAbsolutePath());
        try (BufferedOutputStream os = FileUtilities.getOutputStreamToFile(destination); ObjectOutputStream oos = new ObjectOutputStream(os)) {
            oos.writeObject(classifier);
            oos.writeObject(instancePreparator);
        }
    }



    public void readClassifier(File source) throws IOException, ClassNotFoundException {
        LOG.info("Reading classifier from " + source.getAbsolutePath());
        try (BufferedInputStream is = FileUtilities.getInputStreamFromFile(source); ObjectInputStream ois = new ObjectInputStream(is)) {
            classifier = (Classifier) ois.readObject();
            instancePreparator = (InstancePreparator) ois.readObject();
        }

    }
}
