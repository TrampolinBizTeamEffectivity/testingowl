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
	IssuesWindow issuesWindow

	@Autowired
	AudioPlayer audioPlayer

	@Autowired
	FilePacker filePacker

	@Autowired
	ProcessRecordingWindow openRecordingWindow;

	def listenForPlayStart = false

	def fileNameWithoutEnding

	def open = false


	def unpack() {
		assert fileNameWithoutEnding != null

		filePacker.fileNameWithoutEnding = fileNameWithoutEnding
		filePacker.unpack()
	}

	def open() {
		assert fileNameWithoutEnding != null

		issues.fileNameWithoutEnding = fileNameWithoutEnding
		issues.readFromExcelXlsx();
		issuesWindow.show()
		openRecordingWindow.setProgressValue(3)

		audioPlayer.readFromWav(fileNameWithoutEnding)
		openRecordingWindow.setProgressValue(4)

		open = true
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
		if (open) {
			issuesWindow.dispose()
			openRecordingWindow.setProgressValue(1)
			audioPlayer.close()
			openRecordingWindow.setProgressValue(2)

			issues.writeToExcelXlsx()
			openRecordingWindow.setProgressValue(3)
			filePacker.pack()
			openRecordingWindow.setProgressValue(4)

			//ensure no linking to files
			issues.fileNameWithoutEnding = null
			filePacker.fileNameWithoutEnding = null
			open = false
		}
	}

	def issueSelected(Issue issue) {
		log.info "go to ${issue.frameStart}"
		jPlayer.goToFrame(issue.frameStart)
		jPlayer.play();
	}

	def dispose() {
		issuesWindow.dispose()
		audioPlayer.stopPlaying()
	}
}
