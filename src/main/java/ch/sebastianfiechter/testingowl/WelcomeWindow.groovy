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
class WelcomeWindow {
	
	@Autowired
	OwlIcons owl
	
	@Autowired
	OwlVersion version
	
	@Autowired
	I18N i18n	

	enum Module {Player, Recorder, Converter, Cancel}
	
	def show() {
		
		def result = null
		
		JOptionPane optionPane = new JOptionPane("TestingOwl "+i18n.v("welcome"),
			JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION,
			 null, new Object[0], null);

		LinkLabel github = new LinkLabel("<br>http://testingowl on github.com", "https://github.com/sebastianfiechter/testingowl")
		JLabel label = new JLabel("TestingOwl " + version.version, SwingConstants.CENTER)
		JButton recorder = new JButton("Shoot! (Record)")
		JButton player = new JButton("Review. (Play)")
		//LinkLabel mail = new LinkLabel("se@sebastianfiechter.ch", "mailto:se@sebastianfiechter.ch")
		JLabel license = new JLabel("(c) Sebastian Fiechter, MIT License", SwingConstants.CENTER)
		
		Object[] complexMsg = [owl.welcomeIcon, label, recorder, player, github, license ];
		JDialog dialog = optionPane.createDialog(null, "TestingOwl "+i18n.v("welcome"));
	
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
