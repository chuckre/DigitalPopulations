package mil.army.usace.ehlschlaeger.rgik.util;


/**
 * Tree payload that stores a double-precision floating-point number.
 */
public class TreeDouble implements TreeObject {
	private double number;

	public TreeDouble( double data) {
		super();
		number = data;
	}

	public double getValue() {
		return number;
	}

	public int compare( TreeObject t) {
		TreeDouble o = (TreeDouble) t;
		if( o.number < number)
			return -1;
		else if( o.number > number)
			return 1;
		return 0;
	}

	public TreeObject minimumObject() {
		TreeDouble o = new TreeDouble( Double.NEGATIVE_INFINITY);
		return( (TreeObject) o);
	}

	public TreeObject maximumObject() {
		TreeDouble o = new TreeDouble( Double.POSITIVE_INFINITY);
		return( (TreeObject) o);
	}

	public String toString() {
		String s = number + "";
		return s;
	}

   public static void main( String args[] ) {
      Tree tree = new Tree();
      TreeDouble tto;

      System.out.println( "Inserting the following values: " );

      for ( int i = 1; i <= 10; i++ ) {
		double intVal = ( Math.random() * 100 );
		System.out.print( intVal + " " );
		tto = new TreeDouble( intVal);
		tree.insertNode( tto );
      }
      System.out.println ( "\nPreorder traversal" );
      tree.preorderTraversal();
      System.out.println ( "\nPostorder traversal" );
      tree.postorderTraversal();
      System.out.println ( "\nInorder traversal" );
      tree.inorderTraversal();
      System.out.println( "\nInOrder by Object");
	TreeObject t = tree.inorderFirst();
	while( t != null) {
		TreeDouble tot = (TreeDouble) t;
		System.out.print( tot.toString() + " ");
		t = tree.inorderNext( t);
	}
	System.out.println( "\nRemoval test");
	TreeDouble first = (TreeDouble) tree.inorderFirst();
	while( first != null) {
		System.out.println( "\nfirst: " + first.toString());
		tree.inorderTraversal();
		tree.removeFirstInOrderNode();
		first = (TreeDouble) tree.inorderFirst();
	}
	System.out.println( "\nEnd of Tree");
   }
}