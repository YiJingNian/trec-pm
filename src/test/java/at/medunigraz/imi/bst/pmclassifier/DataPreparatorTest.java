package at.medunigraz.imi.bst.pmclassifier;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class DataPreparatorTest {
    @Test
    public void testReadGoldData() throws DataReadingException {
        List<TrecInstance> trecInstances = DataPreparator.readGoldData(new File("src/main/resources/topics/topics2017.xml"),
                new File("resources/gs2017DocsJson.zip"),
                new File("resources/20180622processedGoldStandardTopics.tsv.gz"));
        assertEquals(22642, trecInstances.size());

        Map<Integer, List<TrecInstance>> instancesByTopic = trecInstances.stream().collect(Collectors.groupingBy(i -> i.getTopic().getNumber()));
        assertEquals(439, instancesByTopic.get(1).size());
        assertEquals(1049, instancesByTopic.get(26).size());

        TrecInstance trecInstance = trecInstances.get(0);
        Document document = trecInstance.getDocument();
        assertFalse(StringUtils.isBlank(document.getTitle()));
        assertFalse(StringUtils.isBlank(document.getAbstractText()));

        Map<String, Document> docsById = trecInstances.stream().map(TrecInstance::getDocument).collect(Collectors.toMap(d -> d.getId(), Function.identity()));
        Document doc = docsById.get("26096169");
        assertTrue(doc.getMeshTagsMajor().contains("Carcinoma, Non-Small-Cell Lung/drug therapy"));
    }
}
