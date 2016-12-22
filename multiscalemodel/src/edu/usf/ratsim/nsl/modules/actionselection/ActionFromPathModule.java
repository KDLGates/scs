package edu.usf.ratsim.nsl.modules.actionselection;

import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.vecmath.Point4f;

import edu.usf.experiment.Globals;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.subject.affordance.Affordance;
import edu.usf.experiment.subject.affordance.EatAffordance;
import edu.usf.experiment.subject.affordance.ForwardAffordance;
import edu.usf.experiment.utils.CSVReader;
import edu.usf.experiment.utils.Debug;
import edu.usf.experiment.utils.RandomSingleton;
import edu.usf.micronsl.ModelAction.ModelAction;
import edu.usf.micronsl.ModelAction.SpecificActions.MoveToAction;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.ModelActionPort;
import edu.usf.micronsl.port.onedimensional.array.Float1dPortArray;
import edu.usf.micronsl.port.singlevalue.Int0dPort;

/**
 * 
 * @author biorob
 * 
 */
public class ActionFromPathModule extends Module {	
	
	//Port to hold output
	public ModelActionPort outport  = new ModelActionPort(this);
	
	//List of positions - x,y,z,theta
	private LinkedList<MoveToAction> positions = new LinkedList<MoveToAction>();
	
	//id of the next pos to be used
	int nextPos = 1; //skip the first position since the robot will already be in the starting position
	
	//to smooth the orientation, the orientation is set using positions[t+deltaId] - positions[t]
	static private int deltaId = 40;
	

	public ActionFromPathModule(String name, String pathFile) {
		super(name);

		addOutPort("action", outport);
		
		//System.out.println("FILE: "+pathFile);
		String[][] strPoints = CSVReader.loadCSV(pathFile, ",", "Place robot file not found");
		for (String[] s : strPoints){
			Float x = Float.parseFloat(s[0]);
			Float y = Float.parseFloat(s[1]);
			System.out.println("coord: "+x+ " "+y);
			positions.add(new MoveToAction(x,y,0f,0f));
		}
		
		
		for(int i=0;i<positions.size();i++)
		{
			if (i+deltaId < positions.size())
			{
				MoveToAction p1 = positions.get(i);
				MoveToAction p2 = positions.get(i+deltaId);
				p1.setW( (float)Math.atan2(p2.y()-p1.y(), p2.x()-p1.x()));
			}else{
				int minPos = positions.size()-1-deltaId < 0 ? 0 : positions.size()-1-deltaId;
				MoveToAction p1 = positions.get(minPos);
				MoveToAction p2 = positions.get(positions.size()-1);
				p2.setW((float)Math.atan2(p2.y()-p1.y(), p2.x()-p1.x()));
			}
		}		

	}

	public void run() {
		//System.out.println();
		MoveToAction p = positions.get(nextPos);
		//System.out.println("set "+ nextPos +": "+p.x()+" "+p.y()+ " "+p.w());
		//universe.setRobotPosition(new Point2D.Float(p.x, p.y), p.w);
		outport.set(p);
		if (nextPos+1 < positions.size()) nextPos++;
		else Globals.getInstance().put("done", true);
		

	}


	@Override
	public boolean usesRandom() {
		return false;
	}
}
