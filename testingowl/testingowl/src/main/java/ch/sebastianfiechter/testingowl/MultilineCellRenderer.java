package ch.sebastianfiechter.testingowl;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

class MultiLineCellRenderer implements TableCellRenderer {

	JScrollPane pane;
	JTextArea area;
	

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		
		area = new JTextArea();
		//pane = new JScrollPane(area);
		//pane.setPreferredSize(new Dimension(pane.getPreferredSize().width, 30));
		
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		area.setOpaque(true);
		
		if (isSelected) {
			area.setForeground(table.getSelectionForeground());
			area.setBackground(table.getSelectionBackground());
		} else {
			area.setForeground(table.getForeground());
			area.setBackground(table.getBackground());
		}
		area.setFont(table.getFont());
		if (hasFocus) {
			area.setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
			if (table.isCellEditable(row, column)) {
				area.setForeground(UIManager.getColor("Table.focusCellForeground"));
				area.setBackground(UIManager.getColor("Table.focusCellBackground"));
			}
		} else {
			//area.setBorder(new EmptyBorder(1, 2, 1, 2));
		}
		area.setText((value == null) ? "" : value.toString());
		
		//updated row height
		table.setRowHeight(row, area.getPreferredSize().height);
		
		return area;
	}
}
