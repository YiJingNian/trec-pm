package at.medunigraz.imi.bst.pmclassifier.apps;

import at.medunigraz.imi.bst.pmclassifier.*;
import cc.mallet.types.InstanceList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.*;

public class CrossVal {
    private static final Logger LOG = LogManager.getLogger();
    private final static int randomSeed = 1;

    public static void main(String args[]) throws DataReadingException, IOException, ClassNotFoundException {
        MalletClassifier classifier = new MalletClassifier();
        int numFolds = 10;

        Map<String, Document> documents = DataReader.readDocuments(new File("resources/gs2017DocsJson.zip"));
        InstancePreparator ip = InstancePreparator.getInstance();
        classifier.setInstancePreparator(ip);

        ip.trainTfIdf(documents.values());

        DataReader.addPMLabels(new File("resources/20180622processedGoldStandardTopics.tsv.gz"), documents);
        List<Document> pmList = documents.values().stream().filter(d -> !d.getPmLabel().equalsIgnoreCase("Not PM")).
                sorted(Comparator.comparing(Document::getId)).
                collect(toList());
        List<Document> notPmList = documents.values().stream().filter(d -> d.getPmLabel().equalsIgnoreCase("Not PM")).
                sorted(Comparator.comparing(Document::getId)).
                collect(toList());

        List<List<Document>> partitions = makeStratifiedPartitions(pmList, notPmList, numFolds);

        int corrAllover = 0;
        for (int fold = 0; fold < numFolds; fold++) {
            int currentFold = fold;
            Map<String, Document> train = IntStream.range(0, numFolds).filter(i -> i != currentFold).mapToObj(partitions::get).flatMap(Collection::stream).collect(toMap(Document::getId, d -> d));
            Map<String, Document> test = partitions.get(fold).stream().collect(toMap(Document::getId, d -> d));

            InstanceList ilist = ip.createClassificationInstances(train);
            LOG.info("Training on " + train.size() + " documents");
            classifier.train(ilist);
            classifier.writeClassifier(new File("tmp.gz"));
            ip.setTfidf(null);
            classifier.readClassifier("tmp.gz");

            LOG.info("Testing on " + test.size() + " documents");
            int corr = 0;
            for (Document document : test.values()) {
                String predict = classifier.predict(document);
                if (predict.equalsIgnoreCase(document.getPmLabel()))
                    ++corr;
            }
            LOG.info("Evaluation for fold " + fold + ":");
            LOG.info("Total: " + test.size());
            LOG.info("Correct: " + corr);
            LOG.info("That is " + (corr / (double) test.size()) * 100 + "%");


            corrAllover += corr;
        }

        LOG.info("Allover eval:");
        LOG.info("Total: " + documents.size());
        LOG.info("Correct: " + corrAllover);
        LOG.info("That is " + (corrAllover / (double) documents.size()) * 100 + "%");

    }

    private static List<List<Document>> makeStratifiedPartitions(List<Document> pmList, List<Document> notPmList, int numFolds) {
        List<List<Document>> ret = new ArrayList<>(numFolds);
        for (int i = 0; i < numFolds; i++)
            ret.add(new ArrayList<>());

        List<Document> pmListShuffled = new ArrayList<>(pmList);
        List<Document> notPmListShuffled = new ArrayList<>(notPmList);

        Collections.shuffle(pmListShuffled, new Random(randomSeed));
        Collections.shuffle(notPmList, new Random(randomSeed));

        for (int i = 0; i < Math.max(pmList.size(), notPmList.size()); i++) {
            int mod = i % numFolds;
            if (i < pmList.size())
                ret.get(mod).add(pmList.get(i));
            if (i < notPmList.size())
                ret.get(mod).add(notPmList.get(i));
        }
        return ret;
    }
}
