package at.medunigraz.imi.bst.pmclassifier.featurepipes;

import at.medunigraz.imi.bst.pmclassifier.DataReader;
import at.medunigraz.imi.bst.pmclassifier.Document;
import cc.mallet.pipe.Pipe;
import cc.mallet.types.*;

import java.io.IOException;
import java.util.List;

public class Document2TokenPipe extends Pipe {

    public Document2TokenPipe() {
        setTargetAlphabet(new LabelAlphabet());
    }

    @Override
    public Instance pipe(Instance inst) {
        Document doc = (Document) inst.getData();
        Token token = new Token(doc.getTitle() + " " + doc.getAbstractText());
        inst.setData(token);
        Label label = ((LabelAlphabet) getTargetAlphabet()).lookupLabel(inst.getTarget());
        inst.setTarget(label);
        inst.setSource(doc);
        return inst;
    }
}
