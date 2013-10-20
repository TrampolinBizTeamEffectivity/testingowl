package ch.sebastianfiechter.testpanorama

import javax.swing.JSlider
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*

@Component
class FramesSlider extends JSlider implements ChangeListener {

	@Autowired
	JPlayerDecorator decorator;
	
	def avoidEvent = true
	
	
	@PostConstruct
	public void init() {
		orientation =  SwingConstants.HORIZONTAL;
		addChangeListener(this)
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
			decorator.pause()
		} else if (!avoidEvent && !valueIsAdjusting) {
			decorator.sliderFrameSet(value)
		}
	}

}
