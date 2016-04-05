package mil.army.usace.ehlschlaeger.rgik.util;
// Fig. 22.16: TreeObject.java

// Class TreeObject definition
public interface TreeObject {
	/** compare should return a negative number if t is less than this, 0 if 
	 *  t == this, and a positive number if t is greater than this.
	 */
	public int compare( TreeObject t);

	/** returns a TreeObject with the minimum posible key value */
	public TreeObject minimumObject();

	/** returns a TreeObject with the maximum posible key value */
	public TreeObject maximumObject();
}