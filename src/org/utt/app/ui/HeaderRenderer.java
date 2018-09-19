package org.utt.app.ui;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;


public class HeaderRenderer implements TableCellRenderer {
    private final JCheckBox check = new JCheckBox("");
    private final JLabel label = new JLabel("");
    @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof Status) {
            switch ((Status) value) {
              case SELECTED:
                check.setSelected(true);
                check.setEnabled(true);
                break;
              case DESELECTED:
                check.setSelected(false);
                check.setEnabled(true);
                break;
              case INDETERMINATE:
                check.setSelected(true);
                check.setEnabled(false);
                break;
              default:
                throw new AssertionError("Unknown Status");
            }
        } else {
            check.setSelected(true);
            check.setEnabled(false);
        }
        check.setOpaque(false);
        check.setFont(table.getFont());
        TableCellRenderer r = table.getTableHeader().getDefaultRenderer();
        JLabel l = (JLabel) r.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        label.setIcon(new ComponentIcon(check));
        l.setIcon(new ComponentIcon(label));
        l.setText(null); //XXX: Nimbus???
//         System.out.println("getHeaderRect: " + table.getTableHeader().getHeaderRect(column));
//         System.out.println("getPreferredSize: " + l.getPreferredSize());
//         System.out.println("getMaximunSize: " + l.getMaximumSize());
//         System.out.println("----");
//         if (l.getPreferredSize().height > 1000) { //XXX: Nimbus???
//             System.out.println(l.getPreferredSize().height);
//             Rectangle rect = table.getTableHeader().getHeaderRect(column);
//             l.setPreferredSize(new Dimension(0, rect.height));
//         }
        return l;
    }
}
