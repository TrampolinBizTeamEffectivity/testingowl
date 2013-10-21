package ch.sebastianfiechter.testpanorama

import com.wet.wired.jsr.recorder.JRecorder

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener

import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JOptionPane

import java.awt.Color
import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import groovy.util.logging.*
import ch.sebastianfiechter.testpanorama.*

@Slf4j
@Component
class JRecorderDecorator implements ActionListener {

	@Autowired
	Issues issues

	@Autowired
	JRecorder jRecorder
	
	@Autowired
	AudioMixerSelectionWindow audioMixerWindow
	
	@Autowired
	AudioRecorder audioRecorder

	JButton bug
	JButton musthave
	JButton wish
	
	String topic

	public JPanel getButtons() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1, 3));

		bug = new JButton(text: "Bug", actionCommand: "bug",
		enabled:false, background: Color.RED);
		bug.addActionListener(this);

		musthave = new JButton(text: "Must Have!", actionCommand: "musthave",
		enabled:false, background: Color.ORANGE);
		musthave.addActionListener(this);


		wish = new JButton(text: "Wish", actionCommand: "wish",
		enabled:false, background: Color.GREEN);
		wish.addActionListener(this);


		panel.add(bug)
		panel.add(musthave)
		panel.add(wish)

		panel.doLayout()

		return panel
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {

		def frame = jRecorder.@frameCount
		def question
		def title
		def type
		
		switch (event.actionCommand) {
			case "bug":
				title = "Bug"
				question = "What's the Bug?"
				type = IssueType.Bug
				break
			case "musthave":
				title = "Must have"
				question = "What do you must have?"
				type = IssueType.Musthave
				break
			case "wish":
				title = "Wish"
				question = "What do you wish?"
				type = IssueType.Wish
				break
			default:
				log.error "Uups, Unknown ActionEvent fired"
				throw new Exception("Uups, Unknown ActionEvent fired");
		}
		
		def value = JOptionPane.showInputDialog(jRecorder, question,
			title, JOptionPane.QUESTION_MESSAGE);

			if (value != null) {
				issues.addIssue(type, frame, jRecorder.@frameCount, value)
			}
	}
	
	public boolean fetchTopic(JFrame parent) {
		def top = JOptionPane.showInputDialog(parent, "What's the Topic of this session?",
				"Topic", JOptionPane.QUESTION_MESSAGE);

		if (top == null) return false

		issues.setTopic(top)
		
		topic = top

		return true
	}
	
	def startup() {
		audioRecorder.mixerName = audioMixerWindow.getMixerName(jRecorder)
	}


	public void recordStarted() {
		bug.enabled = true
		musthave.enabled = true
		wish.enabled = true
		
		audioRecorder.startRecording()
	}
	
	public void recordStopped() {
		bug.enabled = false
		musthave.enabled = false
		wish.enabled = false
		
		audioRecorder.stopRecording()
	}
	
	public File prepareSuggestedFileName() {
		def dateTime = new Date().format('yyyy-MM-dd-H_m_s')
		new File("${topic}-${dateTime}.cap");
	}
	
	public void saveFile(File fileNameCap) {
		def fileNameWithoutEnding = fileNameCap.absolutePath[0..-5];
		
		issues.writeToExcelCsv(fileNameWithoutEnding);
		audioRecorder.writeToWavFile(fileNameWithoutEnding)
	}
	
	def cancelSave() {
		audioRecorder.cancelSave()
	}
}
