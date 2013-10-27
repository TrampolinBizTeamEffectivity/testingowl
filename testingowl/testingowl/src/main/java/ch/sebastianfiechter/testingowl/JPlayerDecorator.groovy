package ch.sebastianfiechter.testingowl

import ch.sebastianfiechter.testingowl.Issues.Issue
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
	IssuesWindow issuesFrame
	
	@Autowired
	AudioPlayer audioPlayer
	
	@Autowired
	Zip zip
	
	def listenForPlayStart = false
		
	def unzip(String fileNameWithDotCapZip) {
		zip.unzip(fileNameWithDotCapZip)
	}
	
	def open(String fileNameWithDotCap) {
		def fileNameWithoutEnding = fileNameWithDotCap[0..-5]
		issuesFrame.issues = issues.readFromExcelXlsx(fileNameWithoutEnding);
		issuesFrame.show()
		
		audioPlayer.readFromWav(fileNameWithoutEnding)
	}
	
	def play() {
		log.info "play()"
		listenForPlayStart = true
	}
	
	def newFrame(long frameNumber, double frameTime) {
		if (listenForPlayStart == true) {
			log.info "play from newFrame with time " + frameTime
			audioPlayer.playFromTime(frameTime)
			listenForPlayStart = false;
		}
		
	}
	
	def pause() {
		audioPlayer.stopPlaying();
	}
	
	def reset() {
		audioPlayer.stopPlaying();
	}
	
	def fastForwart() {
		audioPlayer.stopPlaying();
	}
	
	def close() {
		issuesFrame.dispose()
		audioPlayer.close()
	}
	
	def issueSelected(Issue issue) {
		log.info "go to ${issue.frameStart}"
		jPlayer.goToFrame(issue.frameStart)
		jPlayer.play();
	}
	
	def disposing() {
		issuesFrame.dispose()
		
		audioPlayer.stopPlaying()
	}
	
	


	
}
