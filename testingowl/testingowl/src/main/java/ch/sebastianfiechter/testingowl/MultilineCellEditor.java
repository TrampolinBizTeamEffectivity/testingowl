package ch.sebastianfiechter.testingowl;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.EventObject;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

class MultiLineCellEditor implements TableCellEditor, DocumentListener {

	JTextArea area;
	JTable table;

	ArrayList<CellEditorListener> listeners = new ArrayList<CellEditorListener>();

	@Override
	public Component getTableCellEditorComponent(JTable tabl, Object value,
			boolean isSelected, int row, int column) {

		table = tabl;

		area = new JTextArea();
		area.getDocument().addDocumentListener(this);

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

		// updated row height
		area.setText((value == null) ? "" : value.toString());
		
		return area;
	}
	
	@Override
	public void insertUpdate(DocumentEvent e) {
		updateRowHeight();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		updateRowHeight();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		updateRowHeight();
	}

	private void updateRowHeight() {
						
		System.out.println("cellEditor updateRowHeight: " + area.getPreferredSize().height);
		
		if (table.getRowHeight(table.getEditingRow()) <  area.getPreferredSize().height) {
			table.setRowHeight(table.getEditingRow(),   area.getPreferredSize().height);
		}
	}
		
	@Override
	public Object getCellEditorValue() {
		return area.getText();
	}

	@Override
	public boolean isCellEditable(EventObject arg0) {
		return true;
	}

	@Override
	public boolean shouldSelectCell(EventObject arg0) {
		return true;
	}

	@Override
	public void addCellEditorListener(CellEditorListener l) {
		listeners.add(l);
	}

	@Override
	public void removeCellEditorListener(CellEditorListener l) {
		listeners.remove(l);
	}

	@Override
	public boolean stopCellEditing() {
		ChangeEvent event = new ChangeEvent(this);
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).editingStopped(event);
		}         
		return true;
	}

	@Override
	public void cancelCellEditing() {
		ChangeEvent event = new ChangeEvent(this);
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).editingCanceled(event);
		}
	}

}
