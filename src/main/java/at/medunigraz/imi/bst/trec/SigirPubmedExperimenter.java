package at.medunigraz.imi.bst.trec;

import at.medunigraz.imi.bst.trec.experiment.Experiment;
import at.medunigraz.imi.bst.trec.experiment.ExperimentsBuilder;

import java.io.File;
import java.util.Set;

public class SigirPubmedExperimenter {
    public static void main(String[] args) {
        final File baseline = new File(
                PubmedExperimenter.class.getResource("/templates/biomedical_articles/baseline.json").getFile());

        final Experiment.GoldStandard goldStandard = Experiment.GoldStandard.OFFICIAL;
        final Experiment.Task target = Experiment.Task.PUBMED;
        final int year = 2017;


        ExperimentsBuilder builder = new ExperimentsBuilder();


        builder.newExperiment().withName("baseline").withYear(year).withGoldStandard(goldStandard).withTarget(target)
                .withSubTemplate(baseline).withWordRemoval();

        builder.newExperiment().withName("baseline").withYear(year).withGoldStandard(goldStandard).withTarget(target)
                .withSubTemplate(baseline).withWordRemoval().withGeneSynonym().withGeneDescription();

        builder.newExperiment().withName("baseline").withYear(year).withGoldStandard(goldStandard).withTarget(target)
                .withSubTemplate(baseline).withWordRemoval().withDiseasePreferredTerm().withDiseaseSynonym();


        Set<Experiment> experiments = builder.build();

        for (Experiment exp : experiments) {
            exp.start();
            try {
                exp.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
