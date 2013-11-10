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
class SaveRecordingWindow implements ActionListener {

	@Autowired
	OwlIcons owl

	JDialog dialog

	JProgressBar progressBar

	JButton okButton
	JButton shareButton

	def pathToFile

	def clicked = false

	def setProgressValue(int val) {
		progressBar.setValue(val)
	}

	def show(int progressValue=0, int progressMaxValue=100, String filePath) {

		pathToFile = filePath
		
		JOptionPane optionPane = new JOptionPane("TestingOwl Please wait...",
				JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION,
				null, new Object[0], null);

		def message = "Saving recording to: <BR>${filePath}<BR>"
			
		JLabel label = new JLabel("<html><body><center>${message}</center></body></html>", 
			SwingConstants.CENTER);
		progressBar = new JProgressBar(0, progressMaxValue)
		progressBar.setValue(progressValue)
		progressBar.setStringPainted(true)
		
		shareButton = new JButton("Share this Recording")
		shareButton.actionCommand = "share"
		shareButton.addActionListener(this)
		shareButton.enabled = false
		
		okButton = new JButton("Close")
		okButton.actionCommand = "close"
		okButton.addActionListener(this)
		okButton.enabled = false

		Object[] complexMsg = [label, progressBar, shareButton, okButton];
		optionPane.setMessage(complexMsg);

		dialog = new JDialog((JFrame) null, false)
		//dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
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
		dialog.setVisible(false)
	}

	def waitForConfirm() {
		clicked = false
		okButton.enabled = true
		shareButton.enabled = true
		dialog.modal = true
		def run = true

		while (run) {
			if (clicked == true) {
				run = false
				hide()
			}
			sleep 20
		}
	}

	@Override
	synchronized void actionPerformed(ActionEvent ae) {
		if (ae.actionCommand == "close") {
			clicked = true
		} else if (ae.actionCommand == "share") {
			sendMail()
			clicked = true
		}
	}
	
	def sendMail() {
		Desktop desktop = Desktop.getDesktop();
		
		final String mailURIStr = String.format("mailto:%s?subject=%s&body=%s",
			"",
			UriUtils.encodeFragment("TestingOwl Recording created: ${pathToFile}", "UTF-8"),
			UriUtils.encodeFragment(pathToFile, "UTF-8"));
		final URI mailURI = new URI(mailURIStr);
		desktop.mail(mailURI);
	}
	
}
