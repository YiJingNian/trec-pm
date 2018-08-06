package at.medunigraz.imi.bst.trec;

import java.io.File;
import java.util.Set;

import at.medunigraz.imi.bst.trec.experiment.Experiment;
import at.medunigraz.imi.bst.trec.experiment.ExperimentsBuilder;

public class PubmedExperimenter {
	public static void main(String[] args) {
		final File negativeBoostKeywordsTemplate = new File(
				PubmedExperimenter.class.getResource("/templates/biomedical_articles/hpipubboost.json").getFile());

		final Experiment.GoldStandard goldStandard = Experiment.GoldStandard.INTERNAL;
		final Experiment.Task target = Experiment.Task.PUBMED;
		final int year = 2018;

		
		ExperimentsBuilder builder = new ExperimentsBuilder();

		// Judging order: 3
		builder.newExperiment().withName("hpipubboost").withYear(year).withGoldStandard(goldStandard).withTarget(target)
				.withSubTemplate(negativeBoostKeywordsTemplate).withWordRemoval().withGeneSynonym()
                .withDiseasePreferredTerm().withGeneDescription().withDiseaseSynonym();

		// Judging order: 5
		builder.newExperiment().withName("hpipuball").withYear(year).withGoldStandard(goldStandard).withTarget(target)
				.withSubTemplate(negativeBoostKeywordsTemplate).withWordRemoval().withSolidTumor().withGeneSynonym()
				.withDiseasePreferredTerm().withGeneDescription().withDiseaseSynonym().withGeneFamily();

		Set<Experiment> experiments = builder.build();

		for (Experiment exp : experiments) {
			exp.start();
			try {
				exp.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		for (Experiment exp : experiments) {

		}
	}

}
