package ch.sebastianfiechter.testpanorama


import groovy.swing.SwingBuilder
import java.awt.BorderLayout
import javax.swing.BorderFactory
import javax.swing.event.ListSelectionListener
import javax.swing.ListSelectionModel

/**
 * Demonstrates the use of the Groovy TableModels for viewing tables of any List of objects
 */
class TableDemo {

	// properties
	def frame
	def swing

	static void main(args) {
		def demo = new TableDemo()
		demo.run()
	}

	void run() {
		swing = new SwingBuilder()

		frame = swing.frame(title:'Groovy TableModel Demo', location:[200, 200], 
			size:[300, 200], alwaysOnTop: true) {
			menuBar {
				menu(text:'Help') {
					menuItem() {
						action(name:'About', closure:{ showAbout() })
					}
				}
			}
			panel {
				borderLayout()
				scrollPane(constraints:CENTER) {
					table(rowSelectionAllowed: true, selectionMode: ListSelectionModel.SINGLE_SELECTION) {
						def model = [
							['name':'James', 'location':'London'],
							['name':'Bob', 'location':'Atlanta'],
							['name':'Geir', 'location':'New York']
						]

						tableModel(list:model) {
							closureColumn(header:'Name', read:{row -> return row.name})
							closureColumn(header:'Location', read:{row -> return row.location})
						}

						current.selectionModel.addListSelectionListener({e ->

							//only react on mouse click up
							if (!e.valueIsAdjusting) {
								
								println "selected: " + model[e.firstIndex]
							}

						} as ListSelectionListener)

					}
				}
			}
		}
		frame.show()
	}

	void showAbout() {
		def pane = swing.optionPane(message:'This demo shows how GroovySwing can use Groovy closures to create simple table models')
		def dialog = pane.createDialog(frame, 'About GroovySwing')
		dialog.show()
	}
}