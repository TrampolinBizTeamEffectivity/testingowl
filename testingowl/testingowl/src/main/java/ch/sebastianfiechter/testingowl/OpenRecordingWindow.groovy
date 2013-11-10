package ch.sebastianfiechter.testingowl

import java.awt.BorderLayout

import javax.swing.*

import java.awt.*
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener
import org.springframework.web.util.UriUtils

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import groovy.util.logging.*

@Slf4j
@Component
class OpenRecordingWindow {

	@Autowired
	OwlIcons owl

	JDialog dialog

	JProgressBar progressBar

	def setProgressValue(int val) {
		progressBar.setValue(val)
	}

	def show(int progressValue=0, int progressMaxValue=100, String filePath) {
		JOptionPane optionPane = new JOptionPane("TestingOwl Please wait...",
				JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION,
				null, new Object[0], null);

		def message = "Opening recording from: <BR>${filePath}<BR>"
			
		JLabel label = new JLabel("<html><body><center>${message}</center></body></html>", 
			SwingConstants.CENTER);
		progressBar = new JProgressBar(0, progressMaxValue)
		progressBar.setValue(progressValue)
		progressBar.setStringPainted(true)
		

		Object[] complexMsg = [label, progressBar];
		optionPane.setMessage(complexMsg);

		dialog = new JDialog((JFrame) null, false)
		dialog.setAlwaysOnTop(true)
		dialog.setUndecorated(true)

		dialog.getContentPane().setBorder(BorderFactory.createRaisedBevelBorder())
		dialog.setLayout(new BorderLayout());
		dialog.add(optionPane);

		dialog.pack();
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true)
	}

	def hide() {
		//ensure user sees 100% value
		progressBar.value = progressBar.maximum
		sleep 1000
		
		dialog.setVisible(false)
	}

}
