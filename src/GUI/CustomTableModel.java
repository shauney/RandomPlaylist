package GUI;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.util.ArrayList;

public class CustomTableModel extends AbstractTableModel {

    private ArrayList<DirectoryRow> rows = new ArrayList<DirectoryRow>();
    private String[] columnNames = {"Directory", "Songs Found"};

    @Override
    public String getColumnName(int column) {
        return columnNames[column].toString();
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return rows.get(rowIndex).getColumn(columnIndex);
    }

    public void setRowSongs(int rowIndex, String numberOfSongs) {
        rows.get(rowIndex).setNumOfSongs(numberOfSongs);
        fireTableDataChanged();
    }

    public void addRow(DirectoryRow row) {
        rows.add(row);
        fireTableDataChanged();
    }

    public void setColumnWidths(TableColumnModel columnModel) {
        TableColumn column = null;
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            column = columnModel.getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(250);
            } else {
                column.setPreferredWidth(20);
            }
        }
    }
}
