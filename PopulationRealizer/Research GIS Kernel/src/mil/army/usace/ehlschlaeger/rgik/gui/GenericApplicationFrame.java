package mil.army.usace.ehlschlaeger.rgik.gui;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
/**
 *  Copyright Charles R. Ehlschlaeger,
 *  work: 309-298-1841, fax: 309-298-3003,
 *	<http://faculty.wiu.edu/CR-Ehlschlaeger2/>
 *  This software is freely usable for research and educational purposes. Contact C. R. Ehlschlaeger
 *  for permission for other purposes.
 *  Use of this software requires appropriate citation in all published and unpublished documentation.
 */
public class GenericApplicationFrame extends JFrame  {
	static private int dropHeight = 0;
	static private int dropWidth = 0;
	private int wOffset = 4;
	private int hOffset = 4;
	private int menuPixels = 20;

	public GenericApplicationFrame( String title, int horzPixels, int vertPixels)  {
		super( title);
		start( horzPixels, vertPixels, true);
	}

	public GenericApplicationFrame(String title, int horzPixels, int vertPixels, boolean centerWindow)  {
		super( title);
		start( horzPixels, vertPixels, centerWindow);
	}

	public int getMinDrawPixelWidth() {
		return wOffset;
	}

	public int getMaxDrawPixelWidth() {
		int width = getSize().width;
		return( width - wOffset);
	}

	public int getMinDrawPixelHeight() {
		return( hOffset + menuPixels);
	}

	public int getMaxDrawPixelHeight() {
		int height = getSize().height;
		return( height - hOffset);
	}

	public void center()  {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = getSize();
		//System.out.println( "GenericApplicationFrame.center() w " + frameSize.width + ", h " + frameSize.height);
		int x = (screenSize.width - frameSize.width) / 2;
		int y = (screenSize.height - frameSize.height) /2;
		setLocation( x, y);
	}

	protected void createUI( boolean centerWindow)  {
		if( centerWindow == false) {
			setLocation( dropWidth, dropHeight);
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			Dimension frameSize = getSize();
			if( screenSize.height < frameSize.height + dropHeight) {
				setSize( frameSize.width, screenSize.height - dropHeight);
			}
			dropWidth += 30;
			if( dropWidth + 300 > screenSize.width) {
				dropWidth = 0;
			}
			dropHeight += 30;
			if( dropHeight + 300 > screenSize.height) {
				dropHeight = 0;
			}
		} else {
			center();
		}
		addWindowListener (new WindowAdapter()  {
			public void windowClosing(WindowEvent e) {
				dispose();
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
