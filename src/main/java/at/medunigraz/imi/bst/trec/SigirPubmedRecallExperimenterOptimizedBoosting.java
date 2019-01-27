package at.medunigraz.imi.bst.trec;

import at.medunigraz.imi.bst.trec.experiment.Experiment;

import java.util.HashMap;
import java.util.Map;

public class SigirPubmedRecallExperimenterOptimizedBoosting extends SuperSigirPubmedRecallExperimenter {
    public static void main(String[] args) {

        final Experiment.GoldStandard goldStandard = Experiment.GoldStandard.OFFICIAL;
        final Experiment.Task target = Experiment.Task.PUBMED;
        final int year = 2017;


        Map<String, String> templateProperties = new HashMap<>();
        templateProperties.put("disease_boost", "1.6");
        templateProperties.put("disease_topic_boost", "1");
        templateProperties.put("disease_prefterm_boost", "0.6");
        templateProperties.put("disease_syn_boost", "1.4");
        templateProperties.put("gene_boost", "1.6");
        templateProperties.put("gene_topic_boost", "1");
        templateProperties.put("gene_syn_boost", "0.6");
        templateProperties.put("gene_desc_boost", "0.2");
        templateProperties.put("title_boost", "^1.6");
        templateProperties.put("abstract_boost", "");
        templateProperties.put("keyword_boost", "");
        templateProperties.put("meshTags_boost", "");
        templateProperties.put("genes_field_boost", "");
        templateProperties.put("pos_words_boost", "1.1");
        templateProperties.put("neg_words_boost", "-0.2");
        templateProperties.put("cancer_boost", "1");
        templateProperties.put("chemo_boost", "1");
        templateProperties.put("dna_boost", "1");
        templateProperties.put("extra_boost", "1");
        templateProperties.put("pm_gs_boost", "1");
        templateProperties.put("pm_boost", "1");
        // Doesn't seem to do anything, the whole non-melanoma query part doesn't show much influence (none?)
        templateProperties.put("non_mel_boost", "-10");

        runExperiments(templateProperties, false, goldStandard, target, year, "", "");
    }
}
