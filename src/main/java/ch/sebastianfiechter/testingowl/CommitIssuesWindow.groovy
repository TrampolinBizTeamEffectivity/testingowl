package ch.sebastianfiechter.testingowl

import groovy.swing.SwingBuilder
import groovy.util.logging.Slf4j;

import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JOptionPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wet.wired.jsr.recorder.JRecorder;
import ch.sebastianfiechter.testingowl.Issues.IssueType

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

		dialog = new JDialog(recorder, "TestingOwl Issues: Do you wanna correct some entries? "+
			"To finish press Okay", true)
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.addWindowListener(this)
		dialog.setAlwaysOnTop(true)
		dialog.setSize(700, 300)

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
		okButton.enabled = true
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		dialog.add(okButton, gbc)

		dialog.getRootPane().setDefaultButton(okButton)

		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true)
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
							def col = table.selectedColumn

							if (rowToDelete != 0 && col == 5) {
								if (JOptionPane.showConfirmDialog(dialog,
								"Delete Issue with ID " + issues.issues[rowToDelete].id + "?",
								"Delete?", JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION) {
									issues.issues.minus(issues.issues[rowToDelete]);
									table.model.fireTableRowsDeleted(rowToDelete,rowToDelete)
								} 
							} 
							
							if (col == 5) {
								//remove focus from button
								table.changeSelection(rowToDelete, 0, false, false);
							}
							
						}
					});
		}


		return new JScrollPane(table)
	}


	def hide() {
		dialog.setVisible(false)
	}

	@Override
	synchronized void actionPerformed(ActionEvent ae) {
		if (ae.actionCommand == "okay") {
			if (table.cellEditor != null) {
				 table.cellEditor.stopCellEditing()
			}
			hide()
		}
	}

	@Override
	synchronized void windowClosing(WindowEvent e) {
		if (table.cellEditor != null) {
			table.cellEditor.stopCellEditing()
		}
		hide()
	}
}