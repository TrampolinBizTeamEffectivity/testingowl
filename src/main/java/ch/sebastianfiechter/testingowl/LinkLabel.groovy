package ch.sebastianfiechter.testingowl

import java.awt.Desktop
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener
import java.awt.Cursor

import javax.swing.JLabel
import javax.swing.SwingConstants

class LinkLabel extends JLabel implements MouseListener {
	
	URI uri
	
	public LinkLabel(String text, String url) {
		super("<html><font color=\"#333333\">${text}</font></html>", 
			SwingConstants.CENTER)
		
		uri = new URI(url)
		
		addMouseListener(this)
		
		cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
		enableEvents(MouseEvent.MOUSE_EVENT_MASK);
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		Desktop desktop = Desktop.getDesktop();
		desktop.browse(uri)
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		//do Nothing
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		//do Nothing
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		//do Nothing	
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		//do Nothing
	}
}
