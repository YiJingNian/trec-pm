package at.medunigraz.imi.bst.trec;

import java.io.File;
import java.util.Set;

import at.medunigraz.imi.bst.trec.experiment.Experiment;
import at.medunigraz.imi.bst.trec.experiment.ExperimentsBuilder;

public class ClinicalTrialsExperimenter {
	public static void main(String[] args) {
		final File improvedTemplate = new File(
				ClinicalTrialsExperimenter.class.getResource("/templates/clinical_trials/hpictboost.json").getFile());

		// XXX Change this to Experiment.GoldStandard.INTERNAL for submission
		final Experiment.GoldStandard goldStandard = Experiment.GoldStandard.OFFICIAL;
		final Experiment.Task target = Experiment.Task.CLINICAL_TRIALS;
		final int year = 2017;

		ExperimentsBuilder builder = new ExperimentsBuilder();

		// hpictboost
		builder.newExperiment().withYear(year).withGoldStandard(goldStandard).withTarget(target).withSubTemplate(improvedTemplate)
				.withWordRemoval().withSolidTumor().withDiseasePreferredTerm().withDiseaseSynonym().withGeneSynonym().withGeneFamily();

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
