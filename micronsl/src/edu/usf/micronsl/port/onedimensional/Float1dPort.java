package edu.usf.micronsl.port.onedimensional;

import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.Port;

/**
 * A port that holds a one dimensional set of floats.
 * 
 * @author Martin Llofriu
 *
 */
public abstract class Float1dPort extends Port {

	public Float1dPort(Module owner) {
		super(owner);
	}

	/**
	 * Get the value stored in a certain position
	 * 
	 * @param index
	 *            Relative position of the desired element in [0,getSize()-1]
	 * @return The element at position index.
	 */
	public abstract float get(int index);

	/**
	 * Set the value of one element
	 * 
	 * @param i
	 *            The index
	 * @param x
	 *            The value to be set
	 */
	public abstract void set(int i, float x);

	/**
	 * Get the data in the form of an array.
	 * 
	 * @return An array with the data in the port. The array might not be an
	 *         exclusive copy, but a reference to the internally used data.
	 */
	public abstract float[] getData();

	/**
	 * Get a copy of the data into the array passed by parameter.
	 * 
	 * @param data
	 *            The array into which the data is copied. The array should be
	 *            of size at least getSize(). The obtained data is of exclusive
	 *            access.
	 */
	public abstract void getData(float[] data);

}
