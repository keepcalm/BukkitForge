package au.com.bytecode.opencsv;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract interface ResultSetHelper
{
  public abstract String[] getColumnNames(ResultSet paramResultSet)
    throws SQLException;

  public abstract String[] getColumnValues(ResultSet paramResultSet)
    throws SQLException, IOException;
}

/* Location:           C:\Users\Alexander\Downloads\MCPC-Plus-master\MCPC-Plus-master\target\mcpc-plus-1.5.1-R0.1-SNAPSHOT\
 * Qualified Name:     au.com.bytecode.opencsv.ResultSetHelper
 * JD-Core Version:    0.6.2
 */