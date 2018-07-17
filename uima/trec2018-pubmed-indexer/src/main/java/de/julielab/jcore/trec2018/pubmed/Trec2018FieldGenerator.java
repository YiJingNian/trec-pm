package de.julielab.jcore.trec2018.pubmed;

import de.julielab.jcore.consumer.es.ArrayFieldValue;
import de.julielab.jcore.consumer.es.FieldGenerationException;
import de.julielab.jcore.consumer.es.FieldGenerator;
import de.julielab.jcore.consumer.es.FilterRegistry;
import de.julielab.jcore.consumer.es.preanalyzed.Document;
import de.julielab.jcore.consumer.es.preanalyzed.RawToken;
import de.julielab.jcore.types.*;
import de.julielab.jcore.types.Date;
import de.julielab.jcore.utility.JCoReTools;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import java.util.*;

public class Trec2018FieldGenerator extends FieldGenerator {


    private static final Set<String> unwantedGsFields = new HashSet<>(Arrays.asList("unified_id", "title", "abstract"));

        public Trec2018FieldGenerator(FilterRegistry filterRegistry) {
            super(filterRegistry);
        }

        @Override
        public Document addFields(JCas jCas, Document document) throws CASException, FieldGenerationException {
            addPmid(jCas, document);
            addTitle(jCas, document);
            addFullAbstract(jCas, document);
            addAbstractSections(jCas, document);
            addLastAbstractSectionOrFullAbstract(jCas, document);
            addPublicationDate(jCas, document);
            addMeshTags(jCas, document);
            addDocumentSource(jCas, document);
            addGsInfo(jCas, document);
            return document;
        }

    private void addGsInfo(JCas jCas, Document document) {
        Trec2018FilterBoard filterBoard = filterRegistry.getFilterBoard(Trec2018FilterBoard.class);
        Map<String, CSVRecord> gsRecords = filterBoard.gsRecords;
        String docId = JCoReTools.getDocId(jCas);
        CSVRecord record = gsRecords.get(docId);
        if (record != null) {
            Map<String, Integer> gsHeaderMap = filterBoard.gsHeaderMap;
            for (String header : gsHeaderMap.keySet()) {
                if (unwantedGsFields.contains(header))
                    continue;
                String value = record.get(header);
                if (!StringUtils.isBlank(value))
                    document.addField(header, value);
            }
        }
    }

    /**
         * We do this to reproduce the behavior of the parsing algorithm of Michel's TREC-PM-17 coworker where only the last abstract section was used in structured abstracts.
         * @param jCas
         * @param document
         */
        private void addLastAbstractSectionOrFullAbstract(JCas jCas, Document document) {
            Collection<AbstractSection> sections = JCasUtil.select(jCas, AbstractSection.class);
            if (sections.isEmpty()) {
                JCasUtil.select(jCas, AbstractText.class).stream().findAny().ifPresent(anno ->  document.addField("abstract_lastsection", anno.getCoveredText()));
            } else {
                Optional<AbstractSection> reduce = sections.stream().reduce((a, b) -> b);
                if (reduce.isPresent()) {
                    AbstractSection lastSection = reduce.get();
                    AbstractSectionHeading heading = (AbstractSectionHeading) lastSection.getAbstractSectionHeading();
                    String nlmCategory = heading.getNlmCategory();
                    document.addField("lastAbstractSectionNlmCategory", nlmCategory);
                    document.addField("abstract_lastsection", lastSection.getCoveredText());
                }
            }
        }

        private void addAbstractSections(JCas jCas, Document document) {
            Collection<AbstractSection> sections = JCasUtil.select(jCas, AbstractSection.class);
            if (!sections.isEmpty()) {
                for (AbstractSection section : sections) {
                    AbstractSectionHeading heading = (AbstractSectionHeading) section.getAbstractSectionHeading();
                    String nlmCategory = heading.getNlmCategory();
                    document.addField("abstract_" + nlmCategory, section.getCoveredText());
                }
            }
        }

        private void addDocumentSource(JCas jCas, Document document) {
            document.addField("documentSource", "medline");
        }

        private void addPublicationDate(JCas jCas, Document document) {
            Header header = JCasUtil.selectSingle(jCas, Header.class);
            PubType pubType = header.getPubTypeList(0);
            Date pubDate = pubType.getPubDate();
            document.addField("publicationDate", pubDate.getMonth() + " " + pubDate.getYear());
        }

        private void addPmid(JCas jCas, Document document) {
            String pmid = JCoReTools.getDocId(jCas);
            document.addField("pubmedId", pmid);
        }

        private void addMeshTags(JCas jCas, Document document) {
            Collection<MeshHeading> meshHeadings = JCasUtil.select(jCas, MeshHeading.class);
            ArrayFieldValue allHeadings = new ArrayFieldValue();
            ArrayFieldValue majorTopicHeadings = new ArrayFieldValue();
            ArrayFieldValue minorTopicHeadings = new ArrayFieldValue();
            for (MeshHeading heading : meshHeadings) {
                allHeadings.add(new RawToken(heading.getDescriptorName()));
                if (heading.getDescriptorNameMajorTopic()) {
                    majorTopicHeadings.add(new RawToken(heading.getDescriptorName()));
                } else {
                    minorTopicHeadings.add(new RawToken(heading.getDescriptorName()));
                }
            }
            document.addField("meshTags", allHeadings);
            document.addField("meshTagsMajor", majorTopicHeadings);
            document.addField("meshMinor", minorTopicHeadings);
        }

        private void addFullAbstract(JCas jCas, Document document) {
            Collection<AbstractText> abstractAnnotation = JCasUtil.select(jCas, AbstractText.class);
            abstractAnnotation.stream().findFirst().ifPresent(abstractText -> document.addField("abstract", abstractText.getCoveredText()));
        }

        private void addTitle(JCas jCas, Document document) {
            Collection<Title> titles = JCasUtil.select(jCas, Title.class);
            titles.stream().findFirst().ifPresent(title -> document.addField("title", title.getCoveredText()));
        }





}
