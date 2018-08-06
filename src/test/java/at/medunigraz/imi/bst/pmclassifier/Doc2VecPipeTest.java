package at.medunigraz.imi.bst.pmclassifier;

import at.medunigraz.imi.bst.pmclassifier.featurepipes.Doc2VecPipe;
import cc.mallet.types.Instance;
import cc.mallet.types.Token;
import cc.mallet.util.PropertyList;
import org.junit.Ignore;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class Doc2VecPipeTest {
    /**
     * @Ignore Resource dependent
     */
    @Ignore
    @Test
    public void test(){
        Doc2VecPipe doc2VecPipe = new Doc2VecPipe();
        Token token = new Token("test text");
        Instance instance = new Instance(token, "", "", "");
        doc2VecPipe.pipe(instance);
        PropertyList.Iterator iterator = token.getFeatures().numericIterator();
        assertTrue(iterator.hasNext());
        while (iterator.hasNext()) {
            iterator.next();
            Object next = iterator.getNumericValue();
            System.out.println(next);
        }
    }
}
