package ch.sebastianfiechter.testpanorama

import ch.sebastianfiechter.testpanorama.Issues.Issue
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
	
	@Autowired
	IssuesFrame issuesFrame
	
	@Autowired
	FramesSlider slider
	
	def openIssues(String fileNameWithDotCap) {
		def fileNameWithoutEnding = fileNameWithDotCap[0..-5]
		issuesFrame.issues = issues.readFromExcelCsv(fileNameWithoutEnding);
		
		issuesFrame.show()
	}
	
	def issueSelected(Issue issue) {
		log.info "go to ${issue.frameStart}"
		jPlayer.goToFrame(issue.frameStart)
	}
	
	def pause() {
		jPlayer.pause()
	}
	def sliderFrameSet(int frameStart) {
		jPlayer.goToFrame(frameStart)
	}
	
	def closeFile() {
		issuesFrame.dispose()
	}
	
	def dispose() {
		issuesFrame.dispose()
	}
	


	
}
