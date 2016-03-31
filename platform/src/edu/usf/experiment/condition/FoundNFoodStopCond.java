package edu.usf.experiment.condition;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Globals;
import edu.usf.experiment.utils.ElementWrapper;

public class FoundNFoodStopCond implements Condition {

	private int n;

	public FoundNFoodStopCond(ElementWrapper condParams) {
		Globals g = Globals.getInstace();
		if (g.get("nFoodStopCondition")==null)
			this.n = condParams.getChildInt("n");
		else
			this.n = Integer.parseInt((String)g.get("nFoodStopCondition"));
	}

	@Override
	public boolean holds(Episode e) {
		if (e.getSubject().hasEaten())
			n--;
		return n <= 0;
	}

}
