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

	enum Module {Player, Recorder, Converter, Cancel}
	
	def show() {
		
		def result = null
		
		JOptionPane optionPane = new JOptionPane("TestingOwl Welcome!",
			JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION,
			 null, new Object[0], null);
		 //optionPane.setMessageType(JOptionPane.INFORMATION_MESSAGE);

		LinkLabel trampolin = new LinkLabel("testingowl on github.com", "https://github.com/TrampolinBizTeamEffectivity/testingowl")
		LinkLabel mail = new LinkLabel("sebastianfiechter@trampolin.biz", "mailto:sebastianfiechter@trampolin.biz")
		JLabel label = new JLabel("TestingOwl " + version.version, SwingConstants.CENTER)
		JButton recorder = new JButton("Shoot! (Record)")
		JButton player = new JButton("Review. (Play)")
		
		Object[] complexMsg = [trampolin, mail, owl.welcomeIcon, label, recorder, player ];
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
