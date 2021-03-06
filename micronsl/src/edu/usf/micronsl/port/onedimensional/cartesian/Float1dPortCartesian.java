package edu.usf.micronsl.port.onedimensional.cartesian;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import edu.usf.micronsl.exec.Debug;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.Float1dPort;

/**
 * A port that computes the cartesian product of a set of source ports. The
 * values of the source ports are multiplied using all possible combinations to
 * obtain the values of this port. It is optimized to break multiplication upon
 * reaching zero, and remembering the minimum and maximum index in which the
 * value will return zero as well.
 * 
 * @author Martin Llofriu
 *
 */
public class Float1dPortCartesian extends Float1dPort {

	/**
	 * The total size of the port
	 */
	private int size;
	/**
	 * The minimum value upon which the number is considered to be zero
	 */
	private float eps;
	/**
	 * The set of source ports
	 */
	private List<Float1dPort> sources;
	/**
	 * The minimum index that will return 0, remembered from the last
	 * calculation
	 */
	private int minZeroVal;
	/**
	 * The maximum index that will return 0, remembered from the last
	 * calculation
	 */
	private int maxZeroVal;
	/**
	 * Flag specifying whether minZeroVal and maxZeroVal can be used
	 */
	private boolean validOptimization;
	/**
	 * Whether to apply 0 optimizations or not
	 */
	private boolean optimize;

	/**
	 * 
	 * @param owner
	 *            The owner module
	 * @param sources
	 *            The set of source ports. The sources that are more likely to
	 *            be 0 should be placed at the end of the list to improve
	 *            running time.
	 * @param eps
	 *            The minimum value upon which the number is considered to be
	 *            zero
	 * @param optimize Whether to apply optimizations or not
	 */
	public Float1dPortCartesian(Module owner, List<Float1dPort> sources, float eps, boolean optimize) {
		super(owner);

		this.eps = eps;

		// Invert the list to process it backwards in the get method
		this.sources = new LinkedList<Float1dPort>();
		for (int i = sources.size() - 1; i >= 0; i--)
			this.sources.add(sources.get(i));

		// The size is the product of the source ports sizes
		if (sources.isEmpty()) {
			size = 0;
		} else {
			size = 1;
			for (Float1dPort source : sources) {
				size *= source.getSize();
			}
		}

		if (size == 0)
			throw new RuntimeException("Empty input to Cartesian port");

		// No initial optimization
		validOptimization = false;
		this.optimize = optimize;
	}
	
	/**
	 * Constructor that sets optimizations to true by default
	 * @param owner
	 *            The owner module
	 * @param sources
	 *            The set of source ports. The sources that are more likely to
	 *            be 0 should be placed at the end of the list to improve
	 *            running time.
	 * @param eps
	 *            The minimum value upon which the number is considered to be
	 *            zero
	 */
	public Float1dPortCartesian(Module owner, List<Float1dPort> sources, float eps){
		this(owner, sources, eps, true);
	}

	@Override
	public int getSize() {
		return size;
	}

	@Override
	/**
	 * Get the value at position index. The i-th value is the product of the
	 * j-th values of each set s. For each set s, the index j is (i /
	 * prod_t=0..s-1(t.getSize()) % s.getSize().
	 * 
	 * The value at 0 is the multiplication of all values at 0 in the sources.
	 * The value at 1 is the value at 1 in the first source times the value at 0
	 * in the others. The value at firstSource.getSize() is the value at 0 in
	 * the first source times the value at 1 in the second times the value at 0
	 * in the rest.
	 */
	public float get(int index) {
		if (optimize && validOptimization && index >= minZeroVal && index <= maxZeroVal)
			return 0;

		int remainingSize = size;
		float jointActivation = 1;
		// Track the current scope of the source statetimize
		int currentsource = 0;

		for (Float1dPort source : sources) {
			remainingSize /= source.getSize();
			int sourceIndex = (index / remainingSize) % source.getSize();
			currentsource += remainingSize * sourceIndex;
			jointActivation *= source.get(sourceIndex);
			if (jointActivation <= eps) {
				jointActivation = 0;
				minZeroVal = currentsource;
				maxZeroVal = currentsource + remainingSize - 1;
				validOptimization = true;
				break;
			}
		}

		if (Float.isNaN(jointActivation))
			System.out.println("Numeric Error");

		return jointActivation;
	}

	/**
	 * Invalidate the optimization
	 */
	public void clearOptimizationCache() {
		validOptimization = false;
	}

	@Override
	public float[] getData() {
		float[] data = new float[size];
		getData(data);
		return data;
	}

	@Override
	/**
	 * Returns the data from the port. This method is also optimized to skip
	 * large 0 areas.
	 */
	public void getData(float[] data) {
		Stack<Integer> listPointer = new Stack<Integer>();
		Stack<Integer> indexStack = new Stack<Integer>();
		Stack<Float> currValStack = new Stack<Float>();

		for (int i = sources.get(0).getSize() - 1; i >= 0; i--) {
			listPointer.push(0);
			indexStack.push(i);
			currValStack.push(1f);
		}

		int element = 0;
		do {
			int listNum = listPointer.pop();
			int index = indexStack.pop();
			float currVal = currValStack.pop();

			currVal *= sources.get(listNum).get(index);
			if (Float.isNaN(currVal)) {
				System.out.println("Numeric Error");
				System.exit(1);
			}
			if (optimize && currVal <= eps) {
				int elemsToSkip = 1;
				for (int s = listNum + 1; s < sources.size(); s++)
					elemsToSkip *= sources.get(s).getSize();
				element += elemsToSkip;
			} else {
				if (listNum < sources.size() - 1) {
					for (int i = sources.get(listNum + 1).getSize() - 1; i >= 0; i--) {
						listPointer.push(listNum + 1);
						indexStack.push(i);
						currValStack.push(currVal);
					}
				} else {
					data[element] = currVal;

					element++;
				}
			}
		} while (!listPointer.isEmpty());

		if (Debug.printConjCells)
			if (sources.get(0).get(0) == 1) {
				System.out.println("Conjuntive cells output " + data.length);
				for (int i = 0; i < data.length; i++)
					if (data[i] != 0)
						System.out.print(data[i] + " ");
				System.out.println();
			}

	}

	@Override
	public void set(int i, float x) {
		throw new RuntimeException("Cannot set the value of a Cartesian port");
	}

	@Override
	public void clear() {
		for (Float1dPort source : sources)
			source.clear();
	}

}
