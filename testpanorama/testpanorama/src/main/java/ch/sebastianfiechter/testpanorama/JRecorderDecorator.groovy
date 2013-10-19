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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import groovy.util.logging.*

@Slf4j
@Component
class JRecorderDecorator implements ActionListener {

	@Autowired
	Issues issues

	@Autowired
	JRecorder jRecorder

	JButton bug
	JButton musthave
	JButton wish

	public boolean fetchTopic(JFrame parent) {
		def topic = JOptionPane.showInputDialog(parent, "What's the Topic of this session?",
				"Topic", JOptionPane.QUESTION_MESSAGE);

		if (topic == null) return false

		issues.setTopic(topic)

		return true
	}

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

	public void recordStarted() {
		bug.enabled = true
		musthave.enabled = true
		wish.enabled = true
	}

	@Override
	public void actionPerformed(ActionEvent event) {

		switch (event.actionCommand) {
			case "bug":
				def frame = jRecorder.@frameCount
				def value = JOptionPane.showInputDialog(jRecorder, "What's the Bug?",
				"Bug", JOptionPane.QUESTION_MESSAGE);

				if (value != null) {
					issues.addBug(frame, jRecorder.@frameCount, value)
				}
				break
			case "musthave":
				def frame = jRecorder.@frameCount
				def value = JOptionPane.showInputDialog(jRecorder, "What do you must have?",
				"Must have", JOptionPane.QUESTION_MESSAGE);

				if (value != null) {
					issues.addMusthave(frame, jRecorder.@frameCount, value)
				}
				break
			case "wish":
				def frame = jRecorder.@frameCount
				def value = JOptionPane.showInputDialog(jRecorder, "What do you wish?",
				"Wish", JOptionPane.QUESTION_MESSAGE);

				if (value != null) {
					issues.addWish(frame, jRecorder.@frameCount, value)
				}
				break
			default:
				log.error "Uups, Unknown ActionEvent fired"
				throw new Exception("Uups, Unknown ActionEvent fired");
		}
	}
	
	public void recordStopped() {
		bug.enabled = false
		musthave.enabled = false
		wish.enabled = false
	}
	
	public void saveFile(File fileNameCap) {
		issues.writeToExcelCsv(fileNameCap.absolutePath[0..-5]);
	}
}
