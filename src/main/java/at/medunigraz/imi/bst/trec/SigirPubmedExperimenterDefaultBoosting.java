package at.medunigraz.imi.bst.trec;

import at.medunigraz.imi.bst.trec.experiment.Experiment;

import java.util.HashMap;
import java.util.Map;

public class SigirPubmedExperimenterDefaultBoosting extends SuperSigirPubmedExperimenter{
    public static void main(String[] args) {

        final Experiment.GoldStandard goldStandard = Experiment.GoldStandard.OFFICIAL;
        final Experiment.Task target = Experiment.Task.PUBMED;
        final int year = 2017;


        Map<String, String> templateProperties = new HashMap<>();
        templateProperties.put("disease_boost", "1");
        templateProperties.put("disease_topic_boost", "1");
        templateProperties.put("disease_prefterm_boost", "1");
        templateProperties.put("disease_syn_boost", "1");
        templateProperties.put("gene_boost", "1");
        templateProperties.put("gene_topic_boost", "1");
        templateProperties.put("gene_syn_boost", "1");
        templateProperties.put("gene_desc_boost", "1");
        templateProperties.put("gene_hyper_boost", "1");
        templateProperties.put("title_boost", "");
        templateProperties.put("abstract_boost", "");
        templateProperties.put("keyword_boost", "");
        templateProperties.put("meshTags_boost", "");
        templateProperties.put("genes_field_boost", "");
        templateProperties.put("pos_words_boost", "1");
        templateProperties.put("neg_words_boost", "-1");
        templateProperties.put("cancer_boost", "1");
        templateProperties.put("chemo_boost", "1");
        templateProperties.put("dna_boost", "1");
        templateProperties.put("extra_boost", "1");
        templateProperties.put("pm_gs_boost", "1");
        templateProperties.put("pm_boost", "1");
        templateProperties.put("non_mel_boost", "-1");

        String defaultMultiMatch = "most_fields";
        templateProperties.put("dis_syn_multi_match_type", defaultMultiMatch);
        templateProperties.put("dis_multi_match_type", defaultMultiMatch);
        templateProperties.put("dis_hyper_multi_match_type", defaultMultiMatch);
        templateProperties.put("gene_multi_match_type", defaultMultiMatch);
        templateProperties.put("gene_syn_multi_match_type", defaultMultiMatch);
        templateProperties.put("gene_desc_multi_match_type", defaultMultiMatch);
        templateProperties.put("gene_hyper_multi_match_type", defaultMultiMatch);
        templateProperties.put("cancer_multi_match_type", defaultMultiMatch);
        templateProperties.put("dna_multi_match_type", defaultMultiMatch);
        templateProperties.put("neg_boost_multi_match_type", defaultMultiMatch);
        templateProperties.put("pos_boost_multi_match_type", defaultMultiMatch);



        runExperiments(templateProperties, goldStandard, target, year, "", "");
    }
}
