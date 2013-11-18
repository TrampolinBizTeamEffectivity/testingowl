package ch.sebastianfiechter.testingowl

import javax.swing.JSlider
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wet.wired.jsr.player.JPlayer;

import javax.annotation.PostConstruct;
import javax.swing.*

@Component
class FramesSlider extends JSlider implements ChangeListener, MouseListener {

	@Autowired
	JPlayer jPlayer
	
	def avoidEvent = true
	
	@PostConstruct
	public void init() {
		orientation =  SwingConstants.HORIZONTAL;
		addChangeListener(this)
		addMouseListener(this)
	}
	
	public void setMax(int max) {
		maximum = max
	}
	
	public void setMin(int min) {
		minimum = min
	}
	
	public void setValueProgrammatically(int val) {
		avoidEvent = true
		value = val
		avoidEvent = false
	}
	

	@Override
	public void stateChanged(ChangeEvent event) {
		if (!avoidEvent && valueIsAdjusting) {
			playerPause()
		} else if (!avoidEvent && !valueIsAdjusting) {
			jPlayer.goToFrame(value)
			jPlayer.play();
		}
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		playerPause()
	}
	
	def playerPause() {
		
		def val = value;
		
		Thread pauseThread = new Thread() {
			void run() {
				jPlayer.pause()
				jPlayer.setFrameLabelText(val, jPlayer.totalTime);
			}
		}
		pauseThread.start()

	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		//do nothing
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		//do nothing	
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		//do nothing		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		//do nothing
	}
}
