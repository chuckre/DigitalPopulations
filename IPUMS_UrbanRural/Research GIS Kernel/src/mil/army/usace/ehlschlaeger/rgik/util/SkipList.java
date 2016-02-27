package mil.army.usace.ehlschlaeger.rgik.util;

import java.util.Random;


public class SkipList {
	public static final long HEADER_KEY = -2;    // key=-2 means header element
	// all keys have to be smaller than this one:
	public static final long NIL_KEY    = Long.MAX_VALUE;
	public static final float OPT_PROB = 0.25f; // optimum probability

    /** Our source of random numbers. */
    protected Random random = new Random();

    // Constructor (1):
	//   Constructs a new skip list optimized for the given
	//   expected upper bound for the number of nodes.
	//   So if you expect to have 10000 nodes, call the
	//   constructor as SkipList(10000).
	public SkipList( long maxNodes) {
		// call the constructor (2) with a calculated maximum level
		// and probability set to OPT_PROP=0.25
		// Maximum level of list is depending on expected number of nodes
		// (see paper for mathematical background)
		this(OPT_PROB, (int) Math.ceil(
			Math.log(maxNodes) / Math.log( 1 / OPT_PROB)) - 1);
	}

	// Constructor (2):
	//   Constructs a new skip list, where you can directly set the
	//   probability to increase the level of a new node (often 0.25)
	//   and maximum level of node in the list.
	//   If you are not sure, take constructor (1)!
	public SkipList(float probability, int maxLevel) {
		myLevel = 0;                  // level of empty list
		myProbability = probability;
		myMaxLevel = maxLevel;
    
		// generate the header of the list:
		ActionObject aObject = new ActionObject( HEADER_KEY);
		myHeader = new SkipListElement( myMaxLevel, aObject);
    
		// append the "NIL" element to the header:
		aObject = new ActionObject( NIL_KEY);
		SkipListElement nilElement = 
			new SkipListElement( myMaxLevel, aObject);
		for (int i = 0; i <= myMaxLevel; i++) {
			myHeader.forward[i] = nilElement;
		}
	}

    /**
     * Change our source of random numbers.
     * 
     * @param source
     *            new random number generator
     */
    public void setRandomSource(Random source) {
        random = source;
    }

    /**
     * Return our source of random numbers.
     * 
     * @return current random number generator
     */
    public Random getRandomSource() {
        return random;
    }

    /**
     * Generates with help of randomizer the level of a new element. The higher
     * a level, the less probable it is (see paper). Levels begin at 0 (not at 1
     * like in the paper).
     */
	protected int generateRandomLevel() {
		int newLevel = 0;
		while (newLevel<myMaxLevel && random.nextDouble()<myProbability ) {
			newLevel++;
		}
		return newLevel;
	}

    /**
     * Inserts a new node into the list. If the key already exists, the object
     * is appended to the matching node.
     */
	public void insert( ActionObject actionObject) {
		long searchKey = actionObject.getKey();
		// update holds pointers to next elements on each level;
		// levels run from 0 up to myMaxLevel:
		SkipListElement[] update = new SkipListElement[ myMaxLevel + 1];
    
		// init "cursor" element to header:
		SkipListElement element = myHeader;
    
		// find place to insert the new node:
		for (int i=myLevel; i>=0; i--) {
			while( element.forward[i].getKey() < searchKey) {
				element = element.forward[i];
			}
			update[i] = element;
		}
		element = element.forward[0];
    
		// element with same key is overwritten:
		if( element.getKey() == searchKey) {
			element.addActionObject( actionObject);
		} else { // or an additional element is inserted:
			int newLevel = generateRandomLevel();
			// element has biggest level seen in this list: update list
			if (newLevel > myLevel) {
				for (int i=myLevel+1; i<=newLevel; i++) {
					update[i] = myHeader;
				}
				myLevel = newLevel;
			}
			// allocate new element:
			element = new SkipListElement( newLevel, actionObject);
			for( int i = 0; i <= newLevel; i++) {
				element.forward[i] = update[i].forward[i];
				update[i].forward[i] = element;
			}
		}
	}

    /**
     * Search for a given key in the list. 
     * @return the object with the given key
     */
	public ActionObject[] search( long searchKey) {
		// init "cursor"-element to header:
		SkipListElement element = myHeader;
    
		// find element in list:
		for (int i=myLevel; i>=0; i--) {
			SkipListElement nextElement = element.forward[i];
			while (nextElement.getKey() < searchKey) {
				element = nextElement;
				nextElement = element.forward[i];
			}
		}
		element = element.forward[0];
		if (element.getKey() == searchKey) {
			return element.getObjects();
		}
		return null;
	}

	/**
	 * Get first node in the list.
	 * @return first node in the list
	 */
	public ActionObject[] first() {
		// init "cursor"-element to header:
		SkipListElement element = myHeader;
    
		// find element in list:
		element = element.forward[ 0];
		if (element.getKey() == NIL_KEY)
			return null;
		return element.getObjects();
	}

	/**
	 * Get first node that follows the one with the given key.
	 * @return first node after key
	 */
	public ActionObject[] next( long searchKey) {
		SkipListElement element = myHeader;
    
		// find element in list:
		for (int i=myLevel; i>=0; i--) {
			SkipListElement nextElement = element.forward[i];
			while (nextElement.getKey() < searchKey) {
				element = nextElement;
				nextElement = element.forward[i];
			}
		}
		element = element.forward[0];
		if (element.getKey() > searchKey) {
			if (element.getKey() == NIL_KEY)
				return null;
			return element.getObjects();
		}
		// element.getKey() == searchKey
		element = element.forward[ 0]; 
		if (element.getKey() == NIL_KEY)
			return null;
		return element.getObjects();
	}

	/**
	 * Remove first node from list.
	 */
	public void deleteFirst() {
		// init "cursor"-element to header:
		SkipListElement firstElement = myHeader.forward[ 0];
		for( int i = 0; i <= myLevel; i++) {
			if( myHeader.forward[i] == firstElement) {
				myHeader.forward[i] = firstElement.forward[i];
			}
		}
		// maybe we have to downcorrect the level of the list: 
		while( myLevel > 0  &&  
			 myHeader.forward[ myLevel].getKey() == NIL_KEY) {
			myLevel--;
		}
	}

    /**
     * Remove object from list. If a node with the given key exists, it is
     * removed. If not, method quietly exits.
     */
	public void delete( long searchKey) {
		// update holds pointers to next elements of each level
		SkipListElement update[] = new SkipListElement[myMaxLevel+1];

		// init "cursor"-element to header:
		SkipListElement element = myHeader;
    
		// find element in list:
		for (int i = myLevel; i >= 0; i--) {
			SkipListElement nextElement = element.forward[i];
			while (nextElement.getKey() < searchKey) {
				element = nextElement;
				nextElement = element.forward[i];
			}
			update[i] = element;
		}
		element = element.forward[0];
    
		// element found, so rebuild list without node:
		if (element.getKey() == searchKey) {
			for (int i=0; i<=myLevel; i++) {
				if (update[i].forward[i] == element) {
					update[i].forward[i] = element.forward[i];
				}
			}
			// element can be freed now (would happen automatically):
			element = null;   // garbage collector does the rest...

			// maybe we have to downcorrect the level of the list: 
			while( myLevel > 0  &&  
				 myHeader.forward[ myLevel].getKey() == NIL_KEY) {
				myLevel--;
			}
		}
	}

    /**
     * Composes a multiline-string describing this list.
     */
	public String toString() {
		// inits:
		String result = "";

		result += "SkipList:\n";
		result += "  probability = " + myProbability + "\n";
		result += "  level       = " + myLevel + "\n";
		result += "  max. level  = " + myMaxLevel + "\n";

		// traverse the list and collect the levels:
		SkipListElement element = myHeader.forward[0];
		int[] countLevel = new int[myMaxLevel+1];
		while (element.getKey() != NIL_KEY) {
			countLevel[ element.getLevel()]++;
			element = element.forward[0];
		}
		for (int i = myMaxLevel; i >= 0; i--) {
			result += "    Number of Elements at level " + i + 
				" = " + countLevel[i] +"\n";
		}
		return result;
	}

    /**
     * Composes a multiline-string describing the elements of this list.
     */
	public String elementsToString() {
		// inits:
		String result = "Elements:\n";

		// all elements:
		SkipListElement element = myHeader;
		while (element.getKey() < NIL_KEY) {
			element = element.forward[0];
			result += element.toString() + "\n";
		}
		return result;
	}

	// /////////////////////////////////////////////////////////////////////////
    // Access to members:
    //

    /**
     * returns the current level of the list
     */
    public int getLevel() {
        return myLevel;
    }

    /**
     * returns the maximum level that can be reached
     */
    public int getMaxLevel() {
        return myMaxLevel;
    }

    /**
     * returns the probability
     */
    public float getProbability() {
        return myProbability;
    }

    /**
     * returns the header element
     */
    public SkipListElement getHeader() {
        return myHeader;
    }

    // /////////////////////////////////////////////////////////////////////////
    // private data members:
    //
    private float           myProbability; // probability to increase level
    private int             myMaxLevel;    // upper bound of levels
    private int             myLevel;       // greatest level so far
    private SkipListElement myHeader;      // the header element of list

}
