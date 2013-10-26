package ch.sebastianfiechter.testingowl

import java.awt.BorderLayout
import javax.swing.JDialog
import javax.swing.JLabel
import javax.swing.JOptionPane
import javax.swing.JProgressBar
import javax.swing.JScrollBar
import javax.swing.JWindow
import javax.swing.*
import java.awt.*

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import groovy.util.logging.*

@Slf4j
@Component
class InProgressWindow {
	
	@Autowired
	Owl owl
	
	JWindow dialog
	
	JProgressBar progressBar
	
	def setProgressValue(int val) {
		progressBar.setValue(val)
	}

	def show(def message, def progressValue=0, def progressMaxValue=100) {
		
		JOptionPane optionPane = new JOptionPane("TestingOwl Please wait...",
			JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION,
			 null, new Object[0], null);
		
		JLabel label = new JLabel(message, SwingConstants.CENTER);
		progressBar = new JProgressBar(0, progressMaxValue)
		progressBar.setValue(progressValue);
		progressBar.setStringPainted(true);

		Object[] complexMsg = [owl.waiting, label, progressBar ];
		optionPane.setMessage(complexMsg);

		dialog = new JWindow()
		//dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.setAlwaysOnTop(true)
		//dialog.setUndecorated(true)
	
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
}
