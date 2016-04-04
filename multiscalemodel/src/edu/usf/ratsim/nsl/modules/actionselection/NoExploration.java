package edu.usf.ratsim.nsl.modules.actionselection;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.subject.affordance.Affordance;
import edu.usf.experiment.utils.Debug;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.Float1dPort;
import edu.usf.micronsl.port.singlevalue.Int0dPort;

public class NoExploration extends Module {

	private static final float FORWARD_EPS = 0.00001f;
	private static final float ANGLE_EPS = 1e-6f;

	private Robot robot;

	private boolean lastRot;
	private Subject sub;
	private Int0dPort takenAction;

	public NoExploration(String name, Subject sub) {
		super(name);

		takenAction = new Int0dPort(this);
		addOutPort("takenAction", takenAction);

		lastRot = false;
		robot = sub.getRobot();

		this.sub = sub;
	}

	public void run() {
		Float1dPort votes = (Float1dPort) getInPort("votes");

		Affordance selectedAction;
		List<Affordance> aff = robot.checkAffordances(sub
				.getPossibleAffordances());

		for (int action = 0; action < aff.size(); action++){
			aff.get(action).setValue(votes.get(action));
			if (Debug.printSelectedValues)
				System.out.println("votes for aff " + action + ": " + votes.get(action));
		}

		// Select best action
		List<Affordance> sortedAff = new LinkedList<Affordance>(aff);
		Collections.sort(sortedAff);
		selectedAction = sortedAff.get(aff.size() - 1);

		// Publish the taken action
//		if (selectedAction.getValue() > 0) {
			takenAction.set(aff.indexOf(selectedAction));
			if (Debug.printSelectedValues)
				System.out.println(selectedAction.toString());

			robot.executeAffordance(selectedAction, sub);
//		} else {
//			takenAction.set(-1);
//		}

		// TODO: get the rotation -> forward back
		// // System.out.println(takenAction.get());
		// lastRot = selectedAction == sub.getActionLeft()
		// || selectedAction == sub.getActionRight();
	}

	@Override
	public boolean usesRandom() {
		// Executes robot movements, which uses random
		return true;
	}
}
