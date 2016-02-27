package mil.army.usace.ehlschlaeger.rgik.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;




/**
 * Copyright Charles R. Ehlschlaeger, work: 309-298-1841, fax: 309-298-3003,
 * <http://faculty.wiu.edu/CR-Ehlschlaeger2/>
 * 
 * @author Chuck Ehlschlaeger
 */
public class ApplicationFrame extends JFrame {
    private int dropHeight = 150;

	public ApplicationFrame(String title, int horzPixels, int vertPixels)  {
		super( title);
		start( horzPixels, vertPixels, true);
	}

	public ApplicationFrame(String title, int horzPixels, int vertPixels, boolean centerWindow, AnimationComponent ac)  {
		super( title);
		RGISAnimatedView rav = (RGISAnimatedView) ac;
		dropHeight = rav.getControlFrameVerticalSize();
		start( horzPixels, vertPixels, centerWindow);
	}

	public void center()  {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = getSize();
		//System.out.println( "ApplicationFrame.center() w " + frameSize.width + ", h " + frameSize.height);
		int x = (screenSize.width - frameSize.width) / 2;
		int y = (screenSize.height - frameSize.height) /2;
		setLocation( x, y);
	}

	protected void createUI( boolean centerWindow)  {
		if( centerWindow == false) {
			setLocation( 0, dropHeight);
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			Dimension frameSize = getSize();
			if( screenSize.height < frameSize.height + dropHeight) {
				setSize( frameSize.width, screenSize.height - dropHeight);
			}
		} else {
			center();
		}
		addWindowListener (new WindowAdapter()  {
			public void windowClosing(WindowEvent e) {
				dispose();
				System.exit(0);
			}
		});
	}

	public float getFrameHeight()  {
		Dimension frameSize = getSize();
		float windowHeight = frameSize.height;
		return(windowHeight);
	}

	public float getFrameWidth()  {
		Dimension frameSize = getSize();
		float windowWidth = frameSize.width; 
		
		return(windowWidth);
	}

	private void start( int horzPixels, int vertPixels, boolean centerWindow) {
		setSize( horzPixels, vertPixels);
		createUI( centerWindow);
	}
}
