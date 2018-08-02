package de.julielab.jcore.ae.pmclassifer;

import static de.julielab.jcore.ae.pmclassifer.PMClassificationAnnotator.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


import de.julielab.jcore.types.*;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * Unit tests for jcore-pmclassifier-ae.
 */
public class PMClassificationAnnotatorTest {
    @Test
    public void testCreateClassifierInput() throws UIMAException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        AnalysisEngine engine = AnalysisEngineFactory.createEngine(PMClassificationAnnotator.class, PARAM_EXECUTABLE, "python", PARAM_SCRIPT, "../../scripts/pmClassifier.py", PARAM_PM_MODEL, "../../models/pm_model.sav", PARAM_TFIDFMODEL, "../../models/tfidfmodel.sav");
        engine.collectionProcessComplete();
        JCas jCas = JCasFactory.createJCas("de.julielab.jcore.types.jcore-document-meta-types",
                "de.julielab.jcore.types.jcore-document-structure-types");
        jCas.setDocumentText("Is this already precision medicine? I guess not.");
        Title title = new Title(jCas, 0, 35);
        title.addToIndexes();
        AbstractText abstractText = new AbstractText(jCas, 36, 48);
        abstractText.addToIndexes();
        MeshHeading majorHeading = new MeshHeading(jCas);
        majorHeading.setDescriptorNameMajorTopic(true);
        majorHeading.setDescriptorName("human");
        majorHeading.addToIndexes();
        MeshHeading majorHeading2 = new MeshHeading(jCas);
        majorHeading2.setDescriptorNameMajorTopic(true);
        majorHeading2.setDescriptorName("genes");
        majorHeading2.addToIndexes();
        MeshHeading minorHeading = new MeshHeading(jCas);
        minorHeading.setDescriptorNameMajorTopic(false);
        minorHeading.setDescriptorName("mouse");
        minorHeading.addToIndexes();

        Method m = PMClassificationAnnotator.class.getDeclaredMethod("createClassifierInput", JCas.class);
        m.setAccessible(true);
        ClassifierInput input = (ClassifierInput) m.invoke(PMClassificationAnnotator.class, jCas);

        assertEquals("Is this already precision medicine?", input.getTitle());
        assertEquals("I guess not.", input.getAbstractText());
        assertEquals("human genes", input.getMeshMajor());
        assertEquals("mouse", input.getMeshMinor());
    }

    /**
     * NOTE: This test will only succeed if you have all dependencies of the Python program installed, including
     * auto-sklearn
     * @throws UIMAException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @Test
    public void testEngine() throws UIMAException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        AnalysisEngine engine = AnalysisEngineFactory.createEngine(PMClassificationAnnotator.class, PARAM_EXECUTABLE, "python", PARAM_SCRIPT, "../../scripts/pmClassifier.py", PARAM_PM_MODEL, "../../models/pm_model.sav", PARAM_TFIDFMODEL, "../../models/tfidfmodel.sav");
        JCas jCas = JCasFactory.createJCas("de.julielab.jcore.types.jcore-document-meta-types",
                "de.julielab.jcore.types.jcore-document-structure-types");
        jCas.setDocumentText("Is this already precision medicine? I guess not.");
        Title title = new Title(jCas, 0, 35);
        title.addToIndexes();
        AbstractText abstractText = new AbstractText(jCas, 36, 48);
        abstractText.addToIndexes();
        MeshHeading majorHeading = new MeshHeading(jCas);
        majorHeading.setDescriptorNameMajorTopic(true);
        majorHeading.setDescriptorName("human");
        majorHeading.addToIndexes();
        MeshHeading majorHeading2 = new MeshHeading(jCas);
        majorHeading2.setDescriptorNameMajorTopic(true);
        majorHeading2.setDescriptorName("genes");
        majorHeading2.addToIndexes();
        MeshHeading minorHeading = new MeshHeading(jCas);
        minorHeading.setDescriptorNameMajorTopic(false);
        minorHeading.setDescriptorName("mouse");
        minorHeading.addToIndexes();

        engine.process(jCas);

        AutoDescriptor ad = JCasUtil.selectSingle(jCas, AutoDescriptor.class);
        assertNotNull(ad.getDocumentClasses());
        assertEquals(1, ad.getDocumentClasses().size());
        DocumentClass documentClass = ad.getDocumentClasses(0);
        // We don't want to test the classifier here, we accept both outcomes
        assertTrue(documentClass.getClassname().equals("PM") || documentClass.getClassname().equals("Not PM"));

        engine.collectionProcessComplete();
    }
}
