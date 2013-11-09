package ch.sebastianfiechter.testingowl

import groovy.util.logging.Slf4j;

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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.jcabi.manifests.Manifests

@Slf4j
@Component
class ExceptionWindow {
	
	def show(Exception e) {
		
		JTextArea textArea = new JTextArea(4, 10);
		
		def description = e.class.toString() + ": " + e.message + "\n"
		e.stackTrace.each {
			description += it.toString() + "\n"
		}
		
		textArea.text = description
		
		JScrollPane scrollPane = new JScrollPane(textArea);

		Object[] complexMsg = ["Ooops! An exception occurred. Logs are written. I'll have to quit, sorry.", 
			scrollPane];

		JOptionPane optionPane = new JOptionPane();
		optionPane.setMessage(complexMsg);
		optionPane.setMessageType(JOptionPane.ERROR_MESSAGE);

		JDialog dialog = optionPane.createDialog(null, "TestingOwl Exception occurred!");

		dialog.setVisible(true);
		
		System.exit(1);	
	}
	
}
