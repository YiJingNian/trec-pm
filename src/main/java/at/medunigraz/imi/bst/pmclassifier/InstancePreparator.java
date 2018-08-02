package at.medunigraz.imi.bst.pmclassifier;

import at.medunigraz.imi.bst.pmclassifier.featurepipes.Document2TokenPipe;
import at.medunigraz.imi.bst.pmclassifier.featurepipes.Document2TokenSequencePipe;
import at.medunigraz.imi.bst.pmclassifier.featurepipes.MeshTagsForTokenSequencePipe;
import at.medunigraz.imi.bst.pmclassifier.featurepipes.TfIdfPipe;
import cc.mallet.pipe.*;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import com.wcohen.ss.BasicStringWrapper;
import com.wcohen.ss.BasicStringWrapperIterator;
import com.wcohen.ss.TFIDF;
import com.wcohen.ss.api.StringWrapper;
import com.wcohen.ss.api.Token;
import com.wcohen.ss.api.Tokenizer;
import com.wcohen.ss.tokens.BasicSourcedToken;
import com.wcohen.ss.tokens.BasicToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class InstancePreparator {
    public TFIDF getTfidf() {
        return tfidf;
    }

    public void setTfidf(TFIDF tfidf) {
        this.tfidf = tfidf;
    }

    private static final Logger LOG = LogManager.getLogger();

    private TFIDF tfidf;

    public InstanceList getInstancesForGoldData(File documentJsonZip, File gsTable) throws DataReadingException {

        Map<String, Document> docsById = DataReader.readDocuments(documentJsonZip);
        InstanceList ret = createClassificationInstances(gsTable, docsById);

        return ret;
    }

    public InstanceList createClassificationInstances(File gsTable, Map<String, Document> docsById) throws DataReadingException {
        InstanceList ret = new InstanceList(new SerialPipes(getPipes()));
        DataReader.addPMLabels(gsTable, docsById);
        docsById.values().stream().map(doc -> new Instance(doc, doc.getPmLabel(), doc.getId(), "")).forEach(ret::addThruPipe);
        return ret;
    }

    public InstanceList createClassificationInstances(Map<String, Document> docsById) {
        InstanceList ret = new InstanceList(new SerialPipes(getPipes()));
        docsById.values().stream().map(doc -> new Instance(doc, doc.getPmLabel(), doc.getId(), "")).forEach(ret::addThruPipe);
        return ret;
    }

    public void trainTfIdf(Collection<Document> documents) {
        tfidf = new TFIDF();
        List<StringWrapper> trainData = new ArrayList<>(documents.size());
        for (Document doc : documents) {
            trainData.add(new BasicStringWrapper(doc.getTitle() + " " + doc.getAbstractText()));
        }
        tfidf.train(new BasicStringWrapperIterator(trainData.iterator()));
    }


    private Collection<Pipe> getPipes() {
        return getTfIdfPipes();
    }

    private Collection<Pipe> getTfIdfPipes() {
        List<Pipe> pipes = new ArrayList<>();
        pipes.add(new Document2TokenPipe());
        pipes.add(new TfIdfPipe(tfidf));
        pipes.add(new Token2FeatureVector());
        return pipes;
    }



    private Collection<Pipe> getWordBasedPipes() {
        List<Pipe> pipes = new ArrayList<>();
        pipes.add(new Document2TokenSequencePipe());
        pipes.add(new MeshTagsForTokenSequencePipe());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new FeatureSequence2AugmentableFeatureVector(false));
        return pipes;
    }


}
