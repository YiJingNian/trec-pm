package at.medunigraz.imi.bst.pmclassifier;

import at.medunigraz.imi.bst.trec.model.Topic;
import at.medunigraz.imi.bst.trec.model.TopicSet;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.julielab.java.utilities.FileUtilities;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class DataPreparator {


    public static List<TrecInstance> readGoldData(File topics, File documentJsonZip, File gsTable) throws DataReadingException {

        TopicSet topicSet = new TopicSet(topics);
        Map<Integer, Topic> topicsById = topicSet.getTopics().stream().collect(Collectors.toMap(t -> t.getNumber(), Function.identity()));

        Map<String, Document> docsById = readDocuments(documentJsonZip);
        List<TrecInstance> ret = createClassificationInstances(gsTable, topicsById, docsById);

        return ret;
    }

    private static List<TrecInstance> createClassificationInstances(File gsTable, Map<Integer, Topic> topicsById, Map<String, Document> docsById) throws DataReadingException {
        List<TrecInstance> ret = new ArrayList<>();
        try (CSVParser csvRecords = CSVFormat.TDF.withFirstRecordAsHeader().parse(FileUtilities.getReaderFromFile(gsTable))) {
            Iterator<CSVRecord> it = csvRecords.iterator();
            while (it.hasNext()) {
                CSVRecord record = it.next();
                String trecDocId = record.get("trec_doc_id");
                int trecTopicNumber = Integer.parseInt(record.get("trec_topic_number"));
                String pmRelDesc = record.get("pm_rel_desc");
                int relevanceScore = Integer.parseInt(record.get("relevance_score"));
                Document doc = docsById.get(trecDocId);
                if (doc == null)
                    throw new IllegalStateException("Null document for doc ID " + trecDocId + ". Record: " + record);
                Topic topic = topicsById.get(trecTopicNumber);
                TrecInstance instance = new TrecInstance();
                instance.setTopic(topic);
                instance.setDocument(doc);
                instance.setPmLabel(pmRelDesc);
                instance.setRelevanceScore(relevanceScore);
                ret.add(instance);
            }

        } catch (IOException e1) {
            throw new DataReadingException(e1);
        }
        return ret;
    }

    private static Map<String, Document> readDocuments(File documentJsonZip) throws DataReadingException {
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

    private DataPreparator(){}
}
