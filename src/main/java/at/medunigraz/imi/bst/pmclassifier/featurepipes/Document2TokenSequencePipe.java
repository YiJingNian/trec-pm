package at.medunigraz.imi.bst.pmclassifier.featurepipes;

import at.medunigraz.imi.bst.pmclassifier.DataPreparator;
import at.medunigraz.imi.bst.pmclassifier.Document;
import cc.mallet.pipe.Pipe;
import cc.mallet.types.*;

import java.io.IOException;
import java.util.List;

public class Document2TokenSequencePipe extends Pipe {

    public Document2TokenSequencePipe() {
        setTargetAlphabet(new LabelAlphabet());
    }

    @Override
    public Instance pipe(Instance inst) {
        Document doc = (Document) inst.getData();
        try {
            List<String> terms = DataPreparator.getTextTerms(doc);
            TokenSequence ts = new TokenSequence(terms.size());
            terms.stream().map(Token::new).forEach(ts::add);
            inst.setData(ts);
            Label label = ((LabelAlphabet) getTargetAlphabet()).lookupLabel(inst.getTarget());
            inst.setTarget(label);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inst;
    }
}
