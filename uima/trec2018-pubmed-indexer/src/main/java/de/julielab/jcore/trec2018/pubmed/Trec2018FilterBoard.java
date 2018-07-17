package de.julielab.jcore.trec2018.pubmed;

import de.julielab.jcore.consumer.es.ExternalResource;
import de.julielab.jcore.consumer.es.FilterBoard;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Trec2018FilterBoard extends FilterBoard {

    @ExternalResource(key="goldstandard")
    private List<String> gsData;


    @Override
    public void setupFilters() {
        try {
            /*
            Index(['unified_id', 'trec_topic_number', 'trec_doc_id', 'relevance_score',
       'title', 'abstract', 'topic_age', 'topic_sex', 'topic_disease',
       'topic_gene1', 'topic_gene2', 'topic_gene3', 'topic_other1',
       'topic_other2', 'topic_other3', 'pm_rel_desc', 'disease_desc',
       'gene1_annotation_desc', 'gene1_name', 'gene2_annotation_desc',
       'gene2_name', 'gene3_annotation_desc', 'gene3_name',
       'demographics_desc', 'other_desc'],
      dtype='object')
             */
           try(CSVParser csvRecords = CSVFormat.TDF.parse(new StringReader(gsData.stream().collect(Collectors.joining(System.getProperty("line.separator")))))) {
               Iterator<CSVRecord> it = csvRecords.iterator();
               while (it.hasNext()) {
                   CSVRecord record = it.next();
                   String trecTopicNumber = record.get("trec_topic_number");
                   String trecDocId = record.get("trec_doc_id");
                   String relevanceScore = record.get("relevance_score");
                   String topicAge = record.get("topic_age");
                   String topicSex = record.get("topic_sex");
                   String topicDisease = record.get("topic_disease");
                   String topicGene1 = record.get("topic_gene1");
                   String topicGene2 = record.get("topic_gene2");
                   String topicGene3 = record.get("topic_gene3");
                   String topicOther1 = record.get("topic_other1");
                   String topicOther2 = record.get("topic_other2");
                   String topicOther3 = record.get("topic_other3");
                   String pmRelDesc = record.get("pm_rel_desc");
                   record.get("disease_desc");
               }
           }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
