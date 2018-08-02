package at.medunigraz.imi.bst.pmclassifier.apps;

import at.medunigraz.imi.bst.pmclassifier.*;
import cc.mallet.types.InstanceList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Train {
    private static final Logger LOG = LogManager.getLogger();
    public static void main(String args[]) throws DataReadingException, IOException {
        MalletClassifier classifier = new MalletClassifier();

        LOG.info("Reading documents");
        Map<String, Document> documents = DataReader.readDocuments(new File("resources/gs2017DocsJson.zip"));
        DataReader.addPMLabels(new File("resources/20180622processedGoldStandardTopics.tsv.gz"), documents);
        InstancePreparator ip = InstancePreparator.getInstance();
        ip.trainTfIdf(documents.values());
        classifier.setInstancePreparator(ip);
        InstanceList instances = ip.createClassificationInstances(documents);
        LOG.info("Training the model");
        classifier.train(instances);
        LOG.info("Storing the model to src/main/resources/models/malletPmClassifier.mod.gz");
        classifier.writeClassifier(new File("src/main/resources/models/malletPmClassifier.mod.gz"));

        LOG.info("Doing reclassification of training data to check that training was in order.");
        int corr = 0;
        for (Document doc : documents.values()) {
            String label = classifier.predict(doc);
            if (label.equalsIgnoreCase(doc.getPmLabel()))
                ++corr;
        }
        LOG.info("Total: " + documents.size());
        LOG.info("Correct: " + corr);
        LOG.info("That is " + (corr / (double) documents.size()) * 100 + "%");
    }
}
