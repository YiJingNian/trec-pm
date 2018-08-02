package at.medunigraz.imi.bst.pmclassifier;

import at.medunigraz.imi.bst.pmclassifier.featurepipes.Document2TokenSequencePipe;
import cc.mallet.pipe.FeatureSequence2AugmentableFeatureVector;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.julielab.java.utilities.FileUtilities;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class DataPreparator {

    private static final Logger LOG = LogManager.getLogger();

    private DataPreparator() {
    }

    public static InstanceList getInstancesForGoldData(File documentJsonZip, File gsTable) throws DataReadingException {

        Map<String, Document> docsById = readDocuments(documentJsonZip);
        InstanceList ret = createClassificationInstances(gsTable, docsById);

        return ret;
    }

    public static List<String> getTextTerms(Document document) throws IOException {
        StandardAnalyzer analyzer = new StandardAnalyzer();
        String documentText = document.getTitle() + " " + document.getAbstractText();
        if (document.getMeshTags() != null)
            documentText += document.getMeshTags().stream().collect(Collectors.joining(" "));
        TokenStream ts = analyzer.tokenStream("text", documentText);
        ts.reset();
        CharTermAttribute termAtt = ts.getAttribute(CharTermAttribute.class);
        List<String> terms = new ArrayList<>();
        while (ts.incrementToken()) {
            String s = new String(termAtt.buffer(), 0, termAtt.length());
            terms.add(s);
        }
        return terms;
    }

    public static InstanceList createClassificationInstances(File gsTable, Map<String, Document> docsById) throws DataReadingException {
        InstanceList ret = new InstanceList(new SerialPipes(getPipes()));
        addPMLabels(gsTable, docsById);
        docsById.values().stream().map(doc -> new Instance(doc, doc.getPmLabel(), doc.getId(), "")).forEach(ret::addThruPipe);
        return ret;
    }

    public static InstanceList createClassificationInstances(Map<String, Document> docsById) {
        InstanceList ret = new InstanceList(new SerialPipes(getPipes()));
        docsById.values().stream().map(doc -> new Instance(doc, doc.getPmLabel(), doc.getId(), "")).forEach(ret::addThruPipe);
        return ret;
    }

    public static void addPMLabels(File gsTable, Map<String, Document> docsById) throws DataReadingException {
        try (CSVParser csvRecords = CSVFormat.TDF.withFirstRecordAsHeader().parse(FileUtilities.getReaderFromFile(gsTable))) {
            Iterator<CSVRecord> it = csvRecords.iterator();
            while (it.hasNext()) {
                CSVRecord record = it.next();
                String trecDocId = record.get("trec_doc_id");
                int trecTopicNumber = Integer.parseInt(record.get("trec_topic_number"));
                String pmRelDesc = record.get("pm_rel_desc");
                int relevanceScore = Integer.parseInt(record.get("relevance_score"));
                Document doc = docsById.get(trecDocId);
                if (doc == null) {
                    LOG.warn("Null document for doc ID " + trecDocId + ". Record: " + record);
                    continue;
                    //throw new IllegalStateException("Null document for doc ID " + trecDocId + ". Record: " + record);
                }
                // "Once PM, always PM"
                if (doc.getPmLabel() == null || doc.getPmLabel().equalsIgnoreCase("Not PM"))
                    doc.setPMLabel(pmRelDesc);
            }

        } catch (IOException e1) {
            throw new DataReadingException(e1);
        }
    }

    private static Collection<Pipe> getPipes() {
        List<Pipe> pipes = new ArrayList<>();
        pipes.add(new Document2TokenSequencePipe());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new FeatureSequence2AugmentableFeatureVector(false));
        return pipes;
    }

    public static Map<String, Document> readDocuments(File documentJsonZip) throws DataReadingException {
        Map<String, Document> docsById;

        try {
            docsById = new HashMap<>();
            ObjectMapper om = new ObjectMapper();
            ZipFile zipFile = new ZipFile(documentJsonZip);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = entries.nextElement();
                if (zipEntry.isDirectory())
                    continue;
                InputStream inputStream = zipFile.getInputStream(zipEntry);
                if (zipEntry.getName().toLowerCase().endsWith(".gz") || zipEntry.getName().toLowerCase().endsWith(".gzip"))
                    inputStream = new GZIPInputStream(inputStream);
                Document document = om.readValue(inputStream, Document.class);
                docsById.put(document.getId(), document);
            }
        } catch (IOException e) {
            throw new DataReadingException(e);
        }
        return docsById;
    }


}
