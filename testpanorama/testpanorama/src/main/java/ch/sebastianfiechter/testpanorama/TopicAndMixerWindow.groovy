package ch.sebastianfiechter.testpanorama

import groovy.util.logging.Slf4j;

import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JComboBox
import javax.swing.JDialog
import javax.swing.JOptionPane
import javax.swing.JTextField
import javax.swing.event.AncestorEvent
import javax.swing.event.AncestorListener

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class TopicAndMixerWindow {

	@Autowired
	AudioIO audioIO
	
	int selectedMixerIndex
	def topic
	def mixerName
	
	def show(def parentFrame) {
		
		
		def names = audioIO.getNamesOfMixersSupportingRecording()
		
		JComboBox comboTypesList = new JComboBox(names as String[]);
		if (selectedMixerIndex != null) comboTypesList.selectedIndex = selectedMixerIndex
		
		JTextField textField = new JTextField() 
		textField.addAncestorListener(new AncestorListener() {
			void ancestorMoved(AncestorEvent event) {
				textField.requestFocusInWindow();
			}
			void ancestorAdded(AncestorEvent event) {
				textField.requestFocusInWindow();
			}
			void ancestorRemoved(AncestorEvent event) {
				textField.requestFocusInWindow();
			}
		});
		
		Object[] complexMsg = ["What's the Topic of this session?", textField, "What's your audio device for mic recording?", 
			comboTypesList ];
	
		JOptionPane optionPane = new JOptionPane();
		optionPane.setMessage(complexMsg);
		optionPane.setMessageType(JOptionPane.QUESTION_MESSAGE);
		JDialog dialog = optionPane.createDialog(parentFrame, "Topic And Mic");
		def cancel = false
		dialog.addWindowListener(new WindowAdapter() {
			@Override
			void windowClosing(WindowEvent event) {
				cancel = true
			}
		});
		dialog.setVisible(true);
		
		if (cancel == false) {
			selectedMixerIndex = comboTypesList.selectedIndex
			mixerName = names[comboTypesList.selectedIndex]
			topic = textField.text
			log.info "Audio device selected: " + names[comboTypesList.selectedIndex]
			log.info "Topic entered: " + topic
			
			return true
		}
		
		
		return false
		
	}
}
