package edu.usf.micronsl.port.onedimensional.sum;

import java.util.List;

import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.Int1dPort;

/**
 * A port that ouputs the sum of a set of source ports
 * 
 * @author Martin Llofriu
 *
 */
public class Int1dPortSum extends Int1dPort {

	/**
	 * The source ports to be added
	 */
	private List<Int1dPort> sources;

	/**
	 * Create the port
	 * 
	 * @param owner
	 *            The owner module
	 * @param sources
	 *            The set of source ports. This set should be non-empty and
	 *            contain ports with the same size
	 */
	public Int1dPortSum(Module owner, List<Int1dPort> sources) {
		super(owner);

		if (sources.isEmpty())
			throw new IllegalArgumentException("Cannot use an empty list of sources");

		boolean allSameSize = true;
		int sizeFirst = sources.get(0).getSize();
		for (Int1dPort source : sources)
			allSameSize = sizeFirst == source.getSize();
		if (!allSameSize)
			throw new IllegalArgumentException("All sources should be the same size");

		this.sources = sources;
	}

	@Override
	public int getSize() {
		return sources.get(0).getSize();
	}

	@Override
	public int get(int index) {
		int sum = 0;
		for (Int1dPort source : sources)
			sum += source.get(index);

		return sum;
	}

	@Override
	public void set(int i, int x) {
		throw new RuntimeException("Cannot set the value of a sum port");
	}

	@Override
	public void clear() {
		for (Int1dPort source : sources)
			source.clear();
	}

	@Override
	public int[] getData() {
		int[] data = new int[getSize()];
		for (int i = 0; i < getSize(); i++)
			data[i] = get(i);
		return data;
	}

	@Override
	public void getData(int[] data) {
		for (int i = 0; i < getSize(); i++)
			data[i] = get(i);
	}

}
