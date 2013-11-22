package ch.sebastianfiechter.testingowl

import java.awt.BorderLayout

import javax.swing.*

import java.awt.*
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener

import org.springframework.web.util.UriUtils
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import com.wet.wired.jsr.player.JPlayer
import com.wet.wired.jsr.recorder.JRecorder
import groovy.util.logging.*

@Slf4j
@Component
class SaveRecordingWindow implements ActionListener {

	@Autowired
	OwlIcons owl
	
	@Autowired
	JRecorder recorder

	JDialog dialog

	JButton shareButton
	JButton openPlayerButton
	JButton okButton

	def pathToFile


	def showAndWaitForConfirm(String filePath) {

		pathToFile = filePath
		
		JOptionPane optionPane = new JOptionPane("TestingOwl Please wait...",
				JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION,
				null, new Object[0], null);

		def message = "Saved recording to: <BR>${filePath}<BR>"
			
		JLabel label = new JLabel("<html><body><center>${message}</center></body></html>", 
			SwingConstants.CENTER);

		shareButton = new JButton("Share this Recording")
		shareButton.actionCommand = "share"
		shareButton.addActionListener(this)
		shareButton.enabled = true
		
		openPlayerButton = new JButton("Open Recording in Player")
		openPlayerButton.actionCommand = "play"
		openPlayerButton.addActionListener(this)
		openPlayerButton.enabled = true
		
		okButton = new JButton("Close")
		okButton.actionCommand = "close"
		okButton.addActionListener(this)
		okButton.enabled = true

		Object[] complexMsg = [label, shareButton, openPlayerButton, okButton];
		optionPane.setMessage(complexMsg);

		dialog = new JDialog(recorder, true)
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

	@Override
	synchronized void actionPerformed(ActionEvent ae) {
		if (ae.actionCommand == "play") {
			openPlayer()
			hide();
		} else if (ae.actionCommand == "share") {
			dialog.setAlwaysOnTop(false)
			sendMail()
		} else if (ae.actionCommand == "close") {
			hide();
		}	
	}
	
	def openPlayer() {
		recorder.closeRecorder();
		String[] record = new String[1]
		record[0] = pathToFile[0..-9]
		Main.getPlayer().init(record);
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
