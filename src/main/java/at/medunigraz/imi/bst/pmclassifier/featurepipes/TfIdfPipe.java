package at.medunigraz.imi.bst.pmclassifier.featurepipes;

import cc.mallet.pipe.Pipe;
import cc.mallet.types.Instance;
import cc.mallet.types.Token;
import com.wcohen.ss.TFIDF;

public class TfIdfPipe extends Pipe {
    private TFIDF tfidf;

    public TfIdfPipe(TFIDF tfidf) {
        this.tfidf = tfidf;
    }

    @Override
    public Instance pipe(Instance inst) {
        assert tfidf != null : "The TFIDF model has not been trained.";
        Token token = (Token) inst.getData();
        tfidf.prepare(token.getText());
        com.wcohen.ss.api.Token[] tokens = tfidf.getTokens();
        for (com.wcohen.ss.api.Token tfidfToken : tokens)
            token.setFeatureValue(tfidfToken.getValue(), tfidf.getWeight(tfidfToken));
        return inst;
    }
}
