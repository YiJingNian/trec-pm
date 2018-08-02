package at.medunigraz.imi.bst.pmclassifier.featurepipes;


import at.medunigraz.imi.bst.pmclassifier.Document;
import cc.mallet.pipe.Pipe;
import cc.mallet.types.Instance;
import cc.mallet.types.Token;
import cc.mallet.types.TokenSequence;

public class MeshTagsPipe extends Pipe {
    @Override
    public Instance pipe(Instance inst) {
        Document document = (Document) inst.getSource();
        if (document.getMeshTags() != null){
            TokenSequence ts = (TokenSequence) inst.getData();
            document.getMeshTags().stream().map(Token::new).forEach(ts::add);
        }
        return inst;
    }
}
