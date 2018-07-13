package de.julielab.jcore.pmclassifer;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.julielab.ipc.javabridge.Options;
import de.julielab.ipc.javabridge.StdioBridge;
import de.julielab.jcore.types.*;
import de.julielab.jcore.utility.JCoReTools;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class PMClassificationAnnotator extends JCasAnnotator_ImplBase {
    public static final String PARAM_EXECUTABLE = "PythonExecutable";
    public static final String PARAM_SCRIPT = "ScriptPath";
    public static final String PARAM_TFIDFMODEL = "TfidfModel";
    public static final String PARAM_PM_MODEL = "PmModel";
    private final static Logger log = LoggerFactory.getLogger(PMClassificationAnnotator.class);
    private StdioBridge classifierBridge;
    private ObjectMapper om;
    @ConfigurationParameter(name = PARAM_EXECUTABLE)
    private String pythonExecutable;
    @ConfigurationParameter(name = PARAM_SCRIPT)
    private String scriptPath;
    @ConfigurationParameter(name = PARAM_TFIDFMODEL)
    private String tfidfModel;
    @ConfigurationParameter(name = PARAM_PM_MODEL)
    private String pmModel;


    /**
     * This method is called a single time by the framework at component
     * creation. Here, descriptor parameters are read and initial setup is done.
     */
    @Override
    public void initialize(final UimaContext aContext) throws ResourceInitializationException {
        if (log.isInfoEnabled()) {
            log.info("Component configuration:");
            for (String configName : aContext.getConfigParameterNames())
                log.info("{}: {}", configName, aContext.getConfigParameterValue(configName));
        }
        pythonExecutable = (String) aContext.getConfigParameterValue(PARAM_EXECUTABLE);
        scriptPath = (String) aContext.getConfigParameterValue(PARAM_SCRIPT);
        tfidfModel = (String) aContext.getConfigParameterValue(PARAM_TFIDFMODEL);
        pmModel = (String) aContext.getConfigParameterValue(PARAM_PM_MODEL);


        Options options = new Options();
        options.setExecutable(pythonExecutable);
        options.setResultLineIndicator(line -> line.startsWith("Result: "));
        options.setResultTransformator(line -> line.substring(8));
        classifierBridge = new StdioBridge(options, "-u", scriptPath, pmModel, tfidfModel);
        try {
            classifierBridge.start();
        } catch (IOException e) {
            log.error("Could not start the STDIO connection to the Python PM classifier.", e);
            throw new ResourceInitializationException(e);
        }
        om = new ObjectMapper();
    }

    /**
     * This method is called for each document going through the component. This
     * is where the actual work happens.
     */
    @Override
    public void process(final JCas aJCas) throws AnalysisEngineProcessException {
        ClassifierInput classifierInput = createClassifierInput(aJCas);
        Map<String, ClassifierInput> inputMap = new HashMap<>();
        inputMap.put("0", classifierInput);
        try {
            String stringValue = om.writeValueAsString(inputMap);
            Optional<String> responseO = classifierBridge.sendAndReceive(stringValue).findAny();
            if (!responseO.isPresent()) {
                throw new IllegalStateException("The STDIO bridge to the Python PM classifier did not return a value");
            } else {
                String response = responseO.get();
                String[] outcomes = om.readValue(response, String[].class);
                if (outcomes.length > 1)
                    throw new IllegalStateException("The PM classifier returned " + outcomes.length + " outcomes but only 1 was expected.");
                AutoDescriptor ad;
                try {
                    ad = JCasUtil.selectSingle(aJCas, AutoDescriptor.class);
                } catch (IllegalArgumentException e) {
                    ad = new AutoDescriptor(aJCas);
                    ad.addToIndexes();
                }
                DocumentClass documentClass = new DocumentClass(aJCas);
                if (outcomes[0].equals("1"))
                    documentClass.setClassname("PM");
                else
                    documentClass.setClassname("Not PM");
                FSArray newArray = JCoReTools.addToFSArray(ad.getDocumentClasses(), documentClass);
                ad.setDocumentClasses(newArray);
            }
        } catch (InterruptedException | IOException e) {
            throw new AnalysisEngineProcessException(e);
        }
    }

    private static ClassifierInput createClassifierInput(JCas aJCas) {
        Collection<Title> titles = JCasUtil.select(aJCas, Title.class);
        Collection<AbstractText> abstractTexts = JCasUtil.select(aJCas, AbstractText.class);
        Collection<MeshHeading> meshHeadings = JCasUtil.select(aJCas, MeshHeading.class);

        String title = titles.stream().map(Annotation::getCoveredText).collect(Collectors.joining(" "));
        String abstractText = abstractTexts.stream().map(Annotation::getCoveredText).collect(Collectors.joining(" "));
        String meshMajor = meshHeadings.stream().filter(h -> h.getDescriptorNameMajorTopic()).map(h -> h.getDescriptorName()).collect(Collectors.joining(" "));
        String meshMinor = meshHeadings.stream().filter(h -> !h.getDescriptorNameMajorTopic()).map(h -> h.getDescriptorName()).collect(Collectors.joining(" "));

        return new ClassifierInput(title, abstractText, meshMajor, meshMinor);
    }

    @Override
    public void collectionProcessComplete() throws AnalysisEngineProcessException {
        super.collectionProcessComplete();
        try {
            classifierBridge.stop();
        } catch (InterruptedException e) {
            throw new AnalysisEngineProcessException(e);
        }
    }
}
