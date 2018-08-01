package at.medunigraz.imi.bst.pmclassifier;

import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MalletClassifierTest {

    @Test
    public void testTrain() throws DataReadingException {
        MalletClassifier classifier = new MalletClassifier();
        Map<String, Document> documents = DataPreparator.readDocuments(new File("resources/gs2017DocsJson.zip"));
        Map<String, Document> train = new HashMap<>();
        Map<String, Document> test = new HashMap<>();
        int i = 0;
        for (Map.Entry<String, Document> e : documents.entrySet()) {
            if (i < 7000)
                train.put(e.getKey(), e.getValue());
            else
                test.put(e.getKey(), e.getValue());
            ++i;
        }
        InstanceList ilist = DataPreparator.createClassificationInstances(new File("resources/20180622processedGoldStandardTopics.tsv.gz"), train);
        classifier.train(ilist);
        System.out.println(train.size());
        System.out.println(test.size());

        DataPreparator.addPMLabels(new File("resources/20180622processedGoldStandardTopics.tsv.gz"), test);
        int corr = 0;
        for (Document document : test.values()) {
            String predict = classifier.predict(document);
            if (predict.equalsIgnoreCase(document.getPmLabel()))
                ++corr;
        }
        System.out.println("Total: " + test.size());
        System.out.println("Correct: " + corr);
        System.out.println("That is " + (corr/(double)test.size())*100 + "%");
    }
}
