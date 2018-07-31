package de.julielab.pmclassifier.jsonoutput;

import de.julielab.jcore.consumer.es.ArrayFieldValue;
import de.julielab.jcore.consumer.es.FieldGenerationException;
import de.julielab.jcore.consumer.es.FieldGenerator;
import de.julielab.jcore.consumer.es.FilterRegistry;
import de.julielab.jcore.consumer.es.preanalyzed.Document;
import de.julielab.jcore.consumer.es.preanalyzed.RawToken;
import de.julielab.jcore.types.*;
import de.julielab.jcore.utility.JCoReTools;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;

import java.util.Collection;

public class PmClassifierInputFieldGenerator extends FieldGenerator {

    public PmClassifierInputFieldGenerator(FilterRegistry filterRegistry) {
        super(filterRegistry);
    }

    @Override
    public Document addFields(JCas jCas, Document document) throws CASException, FieldGenerationException {
        addPmid(jCas, document);
        addTitle(jCas, document);
        addFullAbstract(jCas, document);
        addMeshTags(jCas, document);
        addGenes(jCas, document);
        addOrganisms(jCas, document);
        return document;
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

    private void addPmid(JCas jCas, Document document) {
        String pmid = JCoReTools.getDocId(jCas);
        document.addField("pubmedId", pmid);
    }


    private void addGenes(JCas jCas, Document document) {
        Collection<Gene> genes = JCasUtil.select(jCas, Gene.class);
        ArrayFieldValue genesFieldValue = new ArrayFieldValue();
        for (Gene gene : genes) {
            genesFieldValue.add(new RawToken(gene.getCoveredText()));
        }
        document.addField("genes", genesFieldValue);
    }

    private void addOrganisms(JCas jCas, Document document) {
        Collection<Organism> organisms = JCasUtil.select(jCas, Organism.class);
        ArrayFieldValue fieldValue = new ArrayFieldValue();
        for (Organism o : organisms) {
            FSArray resourceEntryList = o.getResourceEntryList();
            if (resourceEntryList != null) {
                for (int i = 0; i < resourceEntryList.size(); ++i) {
                    ResourceEntry entry = (ResourceEntry) resourceEntryList.get(i);
                    if (entry != null)
                        fieldValue.add(new RawToken(entry.getEntryId()));
                }
            }
        }
        document.addField("organisms", fieldValue);
    }
}
