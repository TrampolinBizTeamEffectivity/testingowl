package ch.sebastianfiechter.testingowl;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.EventObject;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableCellEditor;

class MultilineCellEditor implements TableCellEditor, DocumentListener {

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

		// hack to show new line that is empty
		String text = area.getText();
		int rows = 1;
		while (text.indexOf("\n") != -1) {
			text = text.substring(text.indexOf("\n") + 1);
			rows++;
		}

		area.setPreferredSize(new Dimension(area.getPreferredSize().width, rows
				* area.getFontMetrics(area.getFont()).getHeight()));

		// if (area.getText().endsWith("\n")) {
		// area.setPreferredSize(new Dimension(
		// area.getPreferredSize().width,
		// area.getPreferredSize().height+area.getFontMetrics(area.getFont()).getHeight()));
		// }

		if (table.getRowHeight(table.getEditingRow()) < area.getPreferredSize().height) {
			table.setRowHeight(table.getEditingRow(),
					area.getPreferredSize().height);
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

		updateAllRowHeights();

		return true;
	}

	private void updateAllRowHeights() {
		for (int row = 0; row < table.getRowCount(); row++) {
			int rowHeight = 0;

			for (int column = 0; column < table.getColumnCount(); column++) {
				Component comp = table.prepareRenderer(
						table.getCellRenderer(row, column), row, column);
				if (comp == null) {
					comp = table.prepareEditor(
							table.getCellEditor(row, column), row, column);
				}
				rowHeight = Math.max(rowHeight, comp.getPreferredSize().height);
			}

			table.setRowHeight(row, rowHeight);
		}
	}

	@Override
	public void cancelCellEditing() {
		ChangeEvent event = new ChangeEvent(this);
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).editingCanceled(event);
		}
	}

}
