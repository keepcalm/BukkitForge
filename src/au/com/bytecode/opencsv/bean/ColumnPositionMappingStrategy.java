package au.com.bytecode.opencsv.bean;

import au.com.bytecode.opencsv.CSVReader;
import java.io.IOException;

public class ColumnPositionMappingStrategy<T> extends HeaderColumnNameMappingStrategy<T>
{
  private String[] columnMapping = new String[0];

  public void captureHeader(CSVReader reader) throws IOException {
  }
  protected String getColumnName(int col) {
    return (null != this.columnMapping) && (col < this.columnMapping.length) ? this.columnMapping[col] : null;
  }
  public String[] getColumnMapping() {
    return this.columnMapping != null ? (String[])this.columnMapping.clone() : null;
  }
  public void setColumnMapping(String[] columnMapping) {
    this.columnMapping = (columnMapping != null ? (String[])columnMapping.clone() : null);
  }
}

/* Location:           C:\Users\Alexander\Downloads\MCPC-Plus-master\MCPC-Plus-master\target\mcpc-plus-1.5.1-R0.1-SNAPSHOT\
 * Qualified Name:     au.com.bytecode.opencsv.bean.ColumnPositionMappingStrategy
 * JD-Core Version:    0.6.2
 */