package ch.sebastianfiechter.testpanorama

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

@Slf4j
@Component
class WelcomeWindow {

	enum Module {Player, Recorder, Converter, Cancel}
	
	def show() {
		
		def result = null
		
		JOptionPane optionPane = new JOptionPane("TestingOwl Welcome!",
			JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION,
			 null, new Object[0], null);
		 //optionPane.setMessageType(JOptionPane.INFORMATION_MESSAGE);
		 
		 
		BufferedImage img = ImageIO.read(this.class.classLoader.getResource("testingowl_welcome.png"))
		def icon = new ImageIcon(img)
		
		JLabel label = new JLabel("TestingOwl " + this.class.package.specificationVersion, SwingConstants.CENTER)
		JButton recorder = new JButton("Recorder")
		JButton player = new JButton("Player")
		
		Object[] complexMsg = [icon, label, recorder, player ];
		JDialog dialog = optionPane.createDialog(null, "TestingOwl Welcome!");
	
		//JOptionPane optionPane = new JOptionPane();
		optionPane.setMessage(complexMsg);
		
		recorder.addActionListener(new ActionListener() {
			void actionPerformed(ActionEvent event) {
				result = Module.Recorder
				dialog.dispose()
			}
		})
		player.addActionListener(new ActionListener() {
			void actionPerformed(ActionEvent event) {
				result = Module.Player
				dialog.dispose()
			}
		})

		dialog.addWindowListener(new WindowAdapter() {
			@Override
			void windowClosing(WindowEvent event) {
				result = Module.Cancel
				dialog.dispose()
			}
		});
		dialog.pack();
		dialog.setBounds(dialog.getBounds().x as int, 
				50, dialog.getBounds().width as int, dialog.getBounds().height as int)
		dialog.setVisible(true);
		
		return result
		
	}
}
