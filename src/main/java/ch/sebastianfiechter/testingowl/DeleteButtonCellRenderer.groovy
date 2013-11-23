package ch.sebastianfiechter.testingowl;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wet.wired.jsr.recorder.JRecorder;
import groovy.util.logging.*

@Slf4j
class DeleteButtonCellRenderer implements TableCellRenderer {


	JTable table;
	JButton button;


	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		if (row == 0) {
			return new JLabel("");
			
		}
		
		button = new JButton("Delete");
		if (isSelected) {
			button.setForeground(table.getSelectionForeground());
			button.setBackground(table.getSelectionBackground());
		} else {
			button.setForeground(table.getForeground());
			button.setBackground(UIManager.getColor("Button.background"));
		}
		return button;
	}
			
	public void deleteRow(CommitIssuesWindow window) {
				
		if (JOptionPane.showConfirmDialog(window.dialog,
			"Delete Issue with ID " + window.issues.issues[window.table.selectedRow].id + "?",
			"Delete?", JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION) {
				log.info("will delete row ${window.table.selectedRow}")				
				window.issues.issues.remove(window.table.selectedRow)
				
				window.table.model.fireTableRowsDeleted(window.table.selectedRow, window.table.selectedRow)
				window.table.model.fireTableDataChanged()			
			}		
	}


}
