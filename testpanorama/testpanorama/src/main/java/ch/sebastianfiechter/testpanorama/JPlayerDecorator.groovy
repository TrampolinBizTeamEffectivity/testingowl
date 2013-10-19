package ch.sebastianfiechter.testpanorama

import com.wet.wired.jsr.player.JPlayer
import com.wet.wired.jsr.recorder.JRecorder
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener

import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JOptionPane
import java.awt.Color

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import groovy.util.logging.*

@Slf4j
@Component
class JPlayerDecorator {

	@Autowired
	JPlayer jPlayer
	
	@Autowired
	Issues issues
	
	def openIssues(String fileNameWithDotCap) {
		def fileNameWithoutEnding = fileNameWithDotCap[0..-5]
		def issuesList = issues.readFromExcelCsv(fileNameWithoutEnding);
		
		
		
	}
	

	
}
