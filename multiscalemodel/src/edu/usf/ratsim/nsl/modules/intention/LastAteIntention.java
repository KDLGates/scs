package edu.usf.ratsim.nsl.modules.intention;

import edu.usf.experiment.utils.Debug;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.array.Float1dPortArray;
import edu.usf.micronsl.port.singlevalue.Int0dPort;

public class LastAteIntention extends Module implements Intention {

	public float[] intention;

	public LastAteIntention(String name, int numIntentions) {
		super(name);
		intention = new float[numIntentions];
		addOutPort("intention", new Float1dPortArray(this, intention));
	}

	public void run() {
		Int0dPort goalFeeder = (Int0dPort) getInPort("goalFeeder");

		for (int i = 0; i < intention.length; i++)
			intention[i] = 0;

		// System.out.println(goalFeeder.get());
		if (goalFeeder.get() != -1)
			intention[goalFeeder.get()] = 1;
		if (Debug.printIntention) {
			for (int i = 0; i < intention.length; i++)
				System.out.print(intention[i] + " ");
			System.out.println();
		}

	}

	public void simRun(int inte) {
		for (int i = 0; i < intention.length; i++)
			intention[i] = 0;
		intention[inte] = 1;
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

}