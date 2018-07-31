package at.medunigraz.imi.bst.pmclassifier;

import at.medunigraz.imi.bst.trec.model.Topic;
import cc.mallet.classify.Classifier;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class MalletClassifier {
    private Classifier classifier;

    public void train(List<Pair<Topic, Document>> trainData) {

    }

    public String predict(Pair<Topic, Document> inputPair) {

        return null;
    }
}
