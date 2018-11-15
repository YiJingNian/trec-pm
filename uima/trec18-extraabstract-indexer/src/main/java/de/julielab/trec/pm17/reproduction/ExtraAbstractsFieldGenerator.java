package de.julielab.trec.pm17.reproduction;

import de.julielab.jcore.consumer.es.FieldGenerator;
import de.julielab.jcore.consumer.es.FilterRegistry;
import de.julielab.jcore.consumer.es.preanalyzed.Document;
import de.julielab.jcore.types.AbstractText;
import de.julielab.jcore.types.Date;
import de.julielab.jcore.types.PubType;
import de.julielab.jcore.types.Title;
import de.julielab.jcore.types.Header;
import de.julielab.jcore.utility.JCoReTools;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import java.util.Collection;

public class ExtraAbstractsFieldGenerator extends FieldGenerator {
    public ExtraAbstractsFieldGenerator(FilterRegistry filterRegistry) {
        super(filterRegistry);
    }

    @Override
    public Document addFields(JCas jCas, Document document) {
        addDocId(jCas, document);
        addTitle(jCas, document);
        addAbstract(jCas, document);
        addPublicationDate(jCas, document);
        addDocumentSource(jCas, document);
        return document;
    }

    private void addDocumentSource(JCas jCas, Document document) {
        document.addField("documentSource", "extraabstracts");
    }

    private void addPublicationDate(JCas jCas, Document document) {
        Header header = JCasUtil.selectSingle(jCas, Header.class);
        PubType pubType = header.getPubTypeList(0);
        Date pubDate = pubType.getPubDate();
        document.addField("publicationDate", pubDate.getMonth() + " " + pubDate.getYear());
    }

    private void addDocId(JCas jCas, Document document) {
        String pmid = JCoReTools.getDocId(jCas);
        document.addField("pubmedId", pmid);
    }


    private void addAbstract(JCas jCas, Document document) {
        Collection<AbstractText> titles = JCasUtil.select(jCas, AbstractText.class);
        titles.stream().findFirst().ifPresent(abstractText -> document.addField("abstract", abstractText.getCoveredText()));
    }

    private void addTitle(JCas jCas, Document document) {
        Collection<Title> titles = JCasUtil.select(jCas, Title.class);
        titles.stream().findFirst().ifPresent(title -> document.addField("title", title.getCoveredText()));
    }
}
