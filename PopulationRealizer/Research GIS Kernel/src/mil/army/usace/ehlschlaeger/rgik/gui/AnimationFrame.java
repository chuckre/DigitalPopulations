package mil.army.usace.ehlschlaeger.rgik.gui;
import java.awt.BorderLayout;
import java.awt.Label;
import java.text.NumberFormat;

/**
 * AnimationFrame class 
 * in alpha testing.
 *  Copyright Charles R. Ehlschlaeger,
 *  work: 309-298-1841, fax: 309-298-3003,
 *	<http://faculty.wiu.edu/CR-Ehlschlaeger2/>
 *	Version 0.4, last modified 10/23/2003.
 *  This software is freely usable for research and educational purposes. Contact C. R. Ehlschlaeger
 *  for permission for other purposes.
 *  Use of this software requires appropriate citation in all published and unpublished documentation.
 */
public class AnimationFrame extends ApplicationFrame {
	private static int numAnimationFrames = 0;
	private int thisAnimationFrameNumber;
	private Label mStatusLabel;
	private NumberFormat mFormat;
  
	public AnimationFrame( AnimationComponent ac, int horzPixels, int vertPixels, boolean centerWindow) {
		super( "RGISAnimatedView #" + ++numAnimationFrames, horzPixels, vertPixels, centerWindow, ac);
		thisAnimationFrameNumber = numAnimationFrames;
		getContentPane().setLayout( new BorderLayout());
		getContentPane().add( ac, BorderLayout.CENTER);
		getContentPane().add( mStatusLabel = new Label(), BorderLayout.SOUTH);
		// Create a number formatter.
		mFormat = NumberFormat.getInstance();
		mFormat.setMaximumFractionDigits( 2);
		// Listen for the frame rate changes.
		ac.setRateListener(this);
		// Kick off the animation.
		Thread t = new Thread(ac);
		t. setPriority( Thread.MIN_PRIORITY);
		t.start();
	}
  
	public void rateChanged(double frameRate) {
		mStatusLabel.setText( mFormat.format( frameRate) + " fps");
	}

	public int getAnimationComponentNumber() {
		return( thisAnimationFrameNumber);
	}
}