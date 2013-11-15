package ch.sebastianfiechter.testingowl

import groovy.util.logging.Slf4j;

import java.awt.Desktop
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.awt.image.BufferedImage
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JDialog
import javax.swing.JLabel
import javax.swing.JOptionPane
import javax.swing.JTextField
import javax.swing.event.AncestorEvent
import javax.swing.event.AncestorListener
import javax.imageio.*
import javax.swing.*
import org.springframework.web.util.*

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.jcabi.manifests.Manifests

@Slf4j
@Component
class ExceptionWindow {
	
	@Autowired
	OwlVersion version
	
	def show(Throwable e) {
		
		def description = "TestingOwl Version: ${version.version}\n" + e.class.toString() + ": " + e.message + "\n"
		e.stackTrace.each {
			description += it.toString() + "\n"
		}
		//remove last line break
		description = description[0..-2]
		
		JTextArea textArea = new JTextArea(4, 10);
		textArea.text = description
		JScrollPane scrollPane = new JScrollPane(textArea);

		JButton shareButton = new JButton("Send exception log to developer (thank you!)");

		Object[] complexMsg = ["Ooops! An Exception occurred. Logs are written. I'll have to quit, sorry.", 
			scrollPane, shareButton];

		JOptionPane optionPane = new JOptionPane("TestingOwl Exception occurred!",
			JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION,
			 null, new Object[0], null);

		optionPane.setMessage(complexMsg);

		JDialog dialog = optionPane.createDialog(null, "TestingOwl Exception occurred!");
		dialog.addWindowListener(new WindowAdapter() {
			@Override
			void windowClosing(WindowEvent event) {
				dialog.dispose()
			}
		});
		shareButton.addActionListener(new ActionListener() {
			void actionPerformed(ActionEvent event) {
				sendMail(description)
				dialog.dispose()
			}
		})
	
		dialog.pack()
		dialog.setAlwaysOnTop(true)
		dialog.setVisible(true)
		
		System.exit(1);	
	}
	
	def sendMail(String description) {
		Desktop desktop = Desktop.getDesktop();
		
		final String mailURIStr = String.format("mailto:%s?subject=%s&body=%s",
			"", 
			UriUtils.encodeFragment("TestingOwl ${version.version} Exception occurred", "UTF-8"), 
			UriUtils.encodeFragment(description, "UTF-8"));
		final URI mailURI = new URI(mailURIStr);
		desktop.mail(mailURI);
	}
	
}
