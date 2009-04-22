import javax.swing.table.DefaultTableModel;


public class NonEditTableModel extends DefaultTableModel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public boolean isCellEditable(int rowIndex,int columnIndex){
		return false;
	}
}
