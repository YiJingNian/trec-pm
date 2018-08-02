package at.medunigraz.imi.bst.pmclassifier.featurepipes;

import at.medunigraz.imi.bst.pmclassifier.Document;
import cc.mallet.pipe.Pipe;
import cc.mallet.types.Instance;
import cc.mallet.types.Token;

public class HasGenesPipe extends Pipe {
    @Override
    public Instance pipe(Instance inst) {
        Document document = (Document) inst.getSource();
        Token token = (Token) inst.getData();
        if (document.getGenes() != null) {
            token.setFeatureValue("hasGenes", document.getGenes().size());
        }
        return inst;
    }
}
