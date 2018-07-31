package de.julielab.jcore.trec2018.pubmed;

import de.julielab.jcore.consumer.es.ExternalResource;
import de.julielab.jcore.consumer.es.FilterBoard;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Trec2018FilterBoard extends FilterBoard {

    public Map<String, CSVRecord> gsRecords;
    @ExternalResource(key = "goldstandard")
    private List<String> gsData;
    public Map<String, Integer> gsHeaderMap;

    @Override
    public void setupFilters() {
        gsRecords = new HashMap<>();
        try (CSVParser csvRecords = CSVFormat.TDF.withFirstRecordAsHeader().parse(new StringReader(gsData.stream().collect(Collectors.joining(System.getProperty("line.separator")))))) {
            gsHeaderMap = csvRecords.getHeaderMap();
            Iterator<CSVRecord> it = csvRecords.iterator();
            while (it.hasNext()) {
                CSVRecord record = it.next();
                String trecDocId = record.get("trec_doc_id");
                if (gsRecords.containsKey(trecDocId)) {
                    System.out.println("ID: " + trecDocId);
                    System.out.println("New: " + record);
                    System.out.println("Old: " + gsRecords.get(trecDocId));
                    System.out.println("---");
                }
                gsRecords.put(trecDocId, record);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
