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

import org.springframework.stereotype.Component;

import javax.swing.*

@Component
class IssuesWindow {

	@Autowired
	JPlayerDecorator jPlayerDecorator
	
	@Autowired
	JPlayer player
	
	@Autowired
	OwlIcons owl

	def frame
	def swing
	def table

	def issues

	void show() {

		if (issues.size() == 0) {
			return
		}

		swing = new SwingBuilder()

		frame = swing.frame(title:'TestingOwl Issues', location:[0, 440],
		size:[700, 150], alwaysOnTop: true, iconImage:owl.welcomeIcon.image ) {
			panel {
				borderLayout()
				scrollPane(constraints:CENTER) {
					table = table(rowSelectionAllowed: true, selectionMode: ListSelectionModel.SINGLE_SELECTION) {

						tableModel(list:issues) {
							closureColumn(header:'ID', preferredWidth:40, read:{row -> return row.id})
							closureColumn(header:'IssueType', preferredWidth:60, read:{row -> return row.type})
							closureColumn(header:'Start Frame', preferredWidth:40, read:{row -> return row.frameStart})
							closureColumn(header:'End Frame', preferredWidth:40, read:{row -> return row.frameEnd})
							closureColumn(header:'Message', preferredWidth:700-180, cellRenderer: new MultiLineCellRenderer(), read:{row -> return row.message})
						}

//						current.selectionModel.addListSelectionListener(
//								new ListSelectionListener() {
//									public void valueChanged(ListSelectionEvent event) {
//										if (!event.valueIsAdjusting) {
//											jPlayerDecorator.issueSelected(issues[table.selectedRow])
//										}
//									}
//								}
//								)
						current.addMouseListener(new MouseAdapter() {
									public void mouseClicked(MouseEvent e) {
										jPlayerDecorator.issueSelected(issues[table.selectedRow])
									}
								})
						
						current.setRowHeight(current.getRowHeight() * 3)

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