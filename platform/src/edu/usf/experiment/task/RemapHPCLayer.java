package edu.usf.experiment.task;

import java.util.LinkedList;
import java.util.StringTokenizer;

import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;

/**
 * Task to deactivate a random feeder from the set of enabled ones
 * 
 * @author ludo
 *
 */
public class RemapHPCLayer extends Task {

	private LinkedList<Integer> indexList;
	private String group;

	public RemapHPCLayer(ElementWrapper params) {
		super(params);

		group = params.getChildText("group");
		String layers = params.getChildText("layers");
		StringTokenizer tok = new StringTokenizer(layers,",");
		indexList = new LinkedList<Integer>();
		while (tok.hasMoreTokens())
			indexList.add(Integer.parseInt(tok.nextToken()));
	}

	public void perform(Universe u, Subject s){
		if (s.getGroup().equals(group))
			s.remapLayers(indexList);
	}


}
