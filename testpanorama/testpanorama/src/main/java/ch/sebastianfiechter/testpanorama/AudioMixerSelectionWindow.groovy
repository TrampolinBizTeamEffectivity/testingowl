package ch.sebastianfiechter.testpanorama

import groovy.util.logging.Slf4j;

import javax.swing.JComboBox
import javax.swing.JDialog
import javax.swing.JOptionPane

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class AudioMixerSelectionWindow {

	@Autowired
	
	AudioIO audioIO
	
	def getMixerName(def parentFrame) {
		
		def names = audioIO.getNamesOfMixersSupportingRecording()
		
		JComboBox comboTypesList = new JComboBox(names as String[]);
		
		Object[] complexMsg = [ "What's your audio device for mic recording?", 
			comboTypesList ];
	
		JOptionPane optionPane = new JOptionPane();
		optionPane.setMessage(complexMsg);
		optionPane.setMessageType(JOptionPane.QUESTION_MESSAGE);
		JDialog dialog = optionPane.createDialog(parentFrame, "Mic Recording Device");
		dialog.setVisible(true);
		
		log.info "Audio device selected: " + names[comboTypesList.selectedIndex]
		
		return  names[comboTypesList.selectedIndex]
		
	}
}
