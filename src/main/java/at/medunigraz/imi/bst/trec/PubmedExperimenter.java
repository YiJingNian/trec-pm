package at.medunigraz.imi.bst.trec;

import java.io.File;
import java.util.Set;

import at.medunigraz.imi.bst.trec.experiment.Experiment;
import at.medunigraz.imi.bst.trec.experiment.ExperimentsBuilder;

public class PubmedExperimenter {
	public static void main(String[] args) {
		final File negativeBoostKeywordsTemplate = new File(
				PubmedExperimenter.class.getResource("/templates/biomedical_articles/hpipubboost.json").getFile());

		final Experiment.GoldStandard goldStandard = Experiment.GoldStandard.OFFICIAL;
		final Experiment.Task target = Experiment.Task.PUBMED;
		final int year = 2017;

		
		ExperimentsBuilder builder = new ExperimentsBuilder();

		// mugpubboost
		builder.newExperiment().withYear(year).withGoldStandard(goldStandard).withTarget(target)
				.withSubTemplate(negativeBoostKeywordsTemplate).withWordRemoval();

		// hpipubboost
		builder.newExperiment().withYear(year).withGoldStandard(goldStandard).withTarget(target)
				.withSubTemplate(negativeBoostKeywordsTemplate).withWordRemoval().withGeneSynonym();

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
