package ch.sebastianfiechter.testingowl

import org.springframework.beans.factory.annotation.Autowired;

import com.wet.wired.jsr.player.JPlayer
import groovy.swing.SwingBuilder

import java.awt.BorderLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent

import javax.swing.BorderFactory
import javax.swing.event.ListSelectionEvent
import javax.swing.event.ListSelectionListener
import javax.swing.ListSelectionModel
import javax.swing.table.*

import org.springframework.stereotype.Component;
import groovy.util.logging.*
import javax.swing.*

@Slf4j
@Component
class ReviewIssuesWindow {

	@Autowired
	JPlayerDecorator jPlayerDecorator
	
	@Autowired
	JPlayer player
	
	@Autowired
	OwlIcons owl
	
	@Autowired
	Issues issues

	def frame
	def swing
	JTable table


	void show() {

		if (issues.issues.size() == 0) {
			return
		}

		swing = new SwingBuilder()

		frame = swing.frame(title:'TestingOwl Issues: Click on ID to review this issue, insert text in Review-Comment', location:[0, 440],
		size:[700, 150], alwaysOnTop: true, iconImage:owl.welcomeIcon.image ) {
			panel {
				borderLayout()
				scrollPane(constraints:CENTER) {
					table = table(rowSelectionAllowed: false, columnSelectionAllowed: false) {
						tableModel(list:issues.issues) {
							closureColumn(header:'ID', preferredWidth:40, read:{row -> return row.id})
							closureColumn(header:'IssueType', preferredWidth:60, read:{row -> return row.type})
							closureColumn(header:'Start Frame', preferredWidth:40, read:{row -> return row.frameStart})
							closureColumn(header:'End Frame', preferredWidth:40, read:{row -> return row.frameEnd})
							closureColumn(header:'Message', preferredWidth:350-180, cellRenderer: new MultiLineCellRenderer(), 
								read:{row -> return row.message})
							closureColumn(header:'Review-Comment', preferredWidth:350-180, 
								cellRenderer: new MultiLineCellRenderer(), 
								cellEditor: new MultiLineCellEditor(),
								read:{row -> return row.comment}, 
								write: {row, newValue -> 
									row.comment = newValue
									})
						}

						current.addMouseListener(new MouseAdapter() {
							public void mouseClicked(MouseEvent e) {
								
								int colIndex = table.getSelectedColumn();
								if (colIndex != 5) {
									jPlayerDecorator.issueSelected(issues.issues[table.selectedRow])
								}
							}
						})
					}
				}
			}
		}
			
		
		frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					player.close();
				}
			});
		frame.show()
	}

	def dispose() {
		if (frame != null) {
			frame.dispose()
		}
	}
}