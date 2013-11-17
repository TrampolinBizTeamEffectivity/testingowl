package ch.sebastianfiechter.testingowl

import org.springframework.beans.factory.annotation.Autowired;

import ch.sebastianfiechter.testingowl.Issues.IssueType;

import com.wet.wired.jsr.player.JPlayer
import com.wet.wired.jsr.recorder.JRecorder;

import groovy.swing.SwingBuilder

import java.awt.BorderLayout
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent

import javax.swing.BorderFactory
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.event.ListSelectionEvent
import javax.swing.event.ListSelectionListener
import javax.swing.ListSelectionModel
import javax.swing.table.*
import javax.swing.*

import org.springframework.stereotype.Component;

import groovy.util.logging.*

import javax.swing.*

@Slf4j
@Component
class CommitIssuesWindow extends WindowAdapter implements ActionListener {

	@Autowired
	OwlIcons owl

	@Autowired
	Issues issues

	@Autowired
	JRecorder recorder

	JDialog dialog

	JScrollPane tablePane
	JTable table
	JButton okButton

	def clicked = false

	def showAndWaitForConfirm() {

		dialog = new JDialog(recorder, "TestingOwl Issues: Do you wanna correct some entries? To finish press Okay", false)
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.addWindowListener(this)
		dialog.setAlwaysOnTop(true)
		dialog.setSize(700, 300)

		//dialog.getContentPane().setBorder(BorderFactory.createRaisedBevelBorder())
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		dialog.setLayout(gbl);

		tablePane = createTable()
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		dialog.add(tablePane, gbc);

		okButton = new JButton("Okay")
		okButton.actionCommand = "okay"
		okButton.addActionListener(this)
		okButton.enabled = false
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		dialog.add(okButton, gbc)

		dialog.getRootPane().setDefaultButton(okButton)

		//dialog.pack();
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true)

		waitForConfirm()
	}

	def createTable() {
		def swing = new SwingBuilder()

		table = swing.table(rowSelectionAllowed: false, columnSelectionAllowed: false) {
			tableModel(list: issues.issues) {
				closureColumn(header:'ID', preferredWidth:40, read:{row -> return row.id})
				closureColumn(header:'IssueType', preferredWidth:60, read:{row -> return row.type})
				closureColumn(header:'Start Frame', preferredWidth:40, read:{row -> return row.frameStart})
				closureColumn(header:'End Frame', preferredWidth:40, read:{row -> return row.frameEnd})
				closureColumn(header:'Message', preferredWidth:600-180,
				cellRenderer: new MultilineCellRenderer(),
				cellEditor: new MultilineCellEditor(),
				read:{row -> return row.message},
				write: {row, newValue ->
					row.message = newValue
					if (row.type == IssueType.Topic) {
						issues.topic = newValue
					}
				})
				closureColumn(header:'Delete', preferredWidth:80, read:{row -> return row.id},
					cellRenderer: new DeleteButtonCellRenderer())
			}
			current.addMouseListener(new MouseAdapter() {
						public void mouseClicked(MouseEvent e) {
							
							def rowToDelete = table.selectedRow;
							
							if (rowToDelete != 0) {				
								if (JOptionPane.showConfirmDialog(dialog,
								"Delete Issue with ID " + issues.issues[rowToDelete].id + "?",
								"Delete?", JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION) {
									issues.issues.minus(issues.issues[rowToDelete]);
									table.model.fireTableRowsDeleted(rowToDelete,rowToDelete)
								}
							}
						}
					});
		}


		return new JScrollPane(table)
	}


	def waitForConfirm() {
		clicked = false
		okButton.enabled = true
		dialog.modal = true
		def run = true
		while (run) {
			if (clicked == true) {
				run = false
				hide()
			}
			sleep 20
		}
	}

	def hide() {
		dialog.setVisible(false)
	}

	@Override
	synchronized void actionPerformed(ActionEvent ae) {
		if (ae.actionCommand == "okay") {
			if (table.cellEditor != null) table.cellEditor.stopCellEditing()
			clicked = true
		}
	}

	@Override
	synchronized void windowClosing(WindowEvent e) {
		if (table.cellEditor != null) table.cellEditor.stopCellEditing()
		clicked = true
	}
}