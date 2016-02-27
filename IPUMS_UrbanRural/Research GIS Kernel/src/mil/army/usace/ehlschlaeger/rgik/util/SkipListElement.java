package mil.army.usace.ehlschlaeger.rgik.util;


public class SkipListElement {
	private ActionObject[] objects;
	SkipListElement forward[];      // array of forward pointers
  
	public SkipListElement(int level, ActionObject object) {
		objects = new ActionObject[ 1];
		objects[ 0] = object;
		forward = new SkipListElement[level+1];
	}

	public void addActionObject( ActionObject actionObject) {
		ActionObject[] newObjects = new ActionObject[ objects.length + 1];
		for( int i = objects.length - 1; i >= 0; i--)
			newObjects[ i] = objects[ i];
		newObjects[ objects.length] = actionObject;
		objects = newObjects;
	}
 
	public String toString() {
		String result = "SkipListElement has " + objects.length + " actions:";
		for( int i = objects.length - 1; i >= 0; i--) {
			result += "\nk:" + objects[ i].getKey() + ", v:" + 
				objects[ i].toString() + ", l:" + getLevel() + ". fwd k: ";
		}
		// key of forward pointed nodes:
		for (int i = 0; i <= getLevel(); i++) {
			if (forward[i] != null) {
				result += i + ": " + forward[i].getKey() + ", ";
			} else {
				result += i + ": nil, ";
			}
		}
		return result;
	}

	public long getKey() {
		return( objects[ 0].getKey());
	}

	public ActionObject[] getObjects() {
		return objects;
	}
 
	// getLevel():  returns the level of this node (count starting at 0)
	int getLevel() { 
		return( forward.length - 1);
	}
}
