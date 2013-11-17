package ch.sebastianfiechter.testingowl;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

import org.springframework.beans.factory.annotation.Autowired;

class DeleteButtonCellRenderer implements TableCellRenderer {

	@Autowired
	Issues issues;
	
	JTable table;
		
	public Component getTableCellRendererComponent(JTable tabl, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		
		
		if (row == 0) {
			//topic row
			return null;
		}
		
		table = tabl;
		
		JButton button = new JButton("Delete");
		
		if (hasFocus) {
			button.setBackground(Color.GRAY);
		}
		button.setOpaque(true);
		
		return button;
		
	}
	
}
