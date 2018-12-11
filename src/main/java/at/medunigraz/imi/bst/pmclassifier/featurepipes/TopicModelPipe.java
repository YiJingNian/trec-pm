package at.medunigraz.imi.bst.pmclassifier.featurepipes;

import at.medunigraz.imi.bst.pmclassifier.Document;
import cc.mallet.pipe.Pipe;
import cc.mallet.types.Instance;
import cc.mallet.types.Token;

public class TopicModelPipe extends Pipe {


    @Override
    public Instance pipe(Instance inst) {
        Token token = (Token) inst.getData();
        Document document = (Document) inst.getSource();
        String text = token.getText();


        double[] topicWeight = document.getTopicWeight();
        for (int i = 0; i < topicWeight.length; ++i) {
            double w = topicWeight[i];
            token.setFeatureValue("topic_" + i, w);
            //System.out.println("topic_" + i + ": " + w);
        }


        return inst;
    }
}
