package mil.army.usace.ehlschlaeger.rgik.test;

import mil.army.usace.ehlschlaeger.rgik.util.Tree;
import mil.army.usace.ehlschlaeger.rgik.util.TreeObject;

public class TreeObjectTest implements TreeObject {
	private int number;

	public TreeObjectTest( int data) {
		super();
		number = data;
	}

	public int compare( TreeObject t) {
		TreeObjectTest o = (TreeObjectTest) t;
		if( o.number < number)
			return -1;
		else if( o.number > number)
			return 1;
		return 0;
	}

	public TreeObject minimumObject() {
		TreeObjectTest o = new TreeObjectTest( Integer.MIN_VALUE);
		return( (TreeObject) o);
	}

	public TreeObject maximumObject() {
		TreeObjectTest o = new TreeObjectTest( Integer.MAX_VALUE);
		return( (TreeObject) o);
	}

	public String toString() {
		String s = number + "";
		return s;
	}

   public static void main( String args[] ) {
      Tree tree = new Tree();
      TreeObjectTest tto;

      System.out.println( "Inserting the following values: " );

      for ( int i = 1; i <= 10; i++ ) {
		int intVal = ( int ) ( Math.random() * 100 );
		System.out.print( intVal + " " );
		tto = new TreeObjectTest( intVal);
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
		TreeObjectTest tot = (TreeObjectTest) t;
		System.out.print( tot.toString() + " ");
		t = tree.inorderNext( t);
	}
	System.out.println( "\nRemoval test");
	TreeObjectTest first = (TreeObjectTest) tree.inorderFirst();
	while( first != null) {
		System.out.println( "\nfirst: " + first.toString());
		tree.inorderTraversal();
		tree.removeFirstInOrderNode();
		first = (TreeObjectTest) tree.inorderFirst();
	}
	System.out.println( "\nEnd of Tree");
   }
}