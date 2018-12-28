package at.medunigraz.imi.bst.pmclassifier.featurepipes;

import at.medunigraz.imi.bst.pmclassifier.InstancePreparator;
import cc.mallet.pipe.Pipe;
import cc.mallet.types.Instance;
import cc.mallet.types.Token;
import com.wcohen.ss.TFIDF;

public class TfIdfPipe extends Pipe {

    @Override
    public Instance pipe(Instance inst) {
        TFIDF tfidf = InstancePreparator.getInstance().getTfidf();
        assert tfidf != null : "The TFIDF model has not been trained.";
        Token token = (Token) inst.getData();
        com.wcohen.ss.api.Token[] tokens;
        // Synchronization is required because the InstancePreparator is static and thus, the TFIDF object
        // is the same for all threads and it is not thread safe (found out the hard way)
        synchronized (tfidf) {
            tfidf.prepare(token.getText());
            tokens = tfidf.getTokens();
        }
        for (com.wcohen.ss.api.Token tfidfToken : tokens)
            token.setFeatureValue(tfidfToken.getValue(), tfidf.getWeight(tfidfToken));
        return inst;
    }
}
