package au.com.bytecode.opencsv;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ResultSetHelperService
  implements ResultSetHelper
{
  public static final int CLOBBUFFERSIZE = 2048;
  private static final int NVARCHAR = -9;
  private static final int NCHAR = -15;
  private static final int LONGNVARCHAR = -16;
  private static final int NCLOB = 2011;

  public String[] getColumnNames(ResultSet rs)
    throws SQLException
  {
    List names = new ArrayList();
    ResultSetMetaData metadata = rs.getMetaData();

    for (int i = 0; i < metadata.getColumnCount(); i++) {
      names.add(metadata.getColumnName(i + 1));
    }

    String[] nameArray = new String[names.size()];
    return (String[])names.toArray(nameArray);
  }

  public String[] getColumnValues(ResultSet rs) throws SQLException, IOException
  {
    List values = new ArrayList();
    ResultSetMetaData metadata = rs.getMetaData();

    for (int i = 0; i < metadata.getColumnCount(); i++) {
      values.add(getColumnValue(rs, metadata.getColumnType(i + 1), i + 1));
    }

    String[] valueArray = new String[values.size()];
    return (String[])values.toArray(valueArray);
  }

  private String handleObject(Object obj) {
    return obj == null ? "" : String.valueOf(obj);
  }

  private String handleBigDecimal(BigDecimal decimal) {
    return decimal == null ? "" : decimal.toString();
  }

  private String handleLong(ResultSet rs, int columnIndex) throws SQLException {
    long lv = rs.getLong(columnIndex);
    return rs.wasNull() ? "" : Long.toString(lv);
  }

  private String handleInteger(ResultSet rs, int columnIndex) throws SQLException {
    int i = rs.getInt(columnIndex);
    return rs.wasNull() ? "" : Integer.toString(i);
  }

  private String handleDate(ResultSet rs, int columnIndex) throws SQLException {
    Date date = rs.getDate(columnIndex);
    String value = null;
    if (date != null) {
      SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
      value = dateFormat.format(date);
    }
    return value;
  }

  private String handleTime(Time time) {
    return time == null ? null : time.toString();
  }

  private String handleTimestamp(Timestamp timestamp) {
    SimpleDateFormat timeFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
    return timestamp == null ? null : timeFormat.format(timestamp);
  }

  private String getColumnValue(ResultSet rs, int colType, int colIndex)
    throws SQLException, IOException
  {
    String value = "";

    switch (colType)
    {
    case -7:
    case 2000:
      value = handleObject(rs.getObject(colIndex));
      break;
    case 16:
      boolean b = rs.getBoolean(colIndex);
      value = Boolean.valueOf(b).toString();
      break;
    case 2005:
    case 2011:
      Clob c = rs.getClob(colIndex);
      if (c != null)
        value = read(c); break;
    case -5:
      value = handleLong(rs, colIndex);
      break;
    case 2:
    case 3:
    case 6:
    case 7:
    case 8:
      value = handleBigDecimal(rs.getBigDecimal(colIndex));
      break;
    case -6:
    case 4:
    case 5:
      value = handleInteger(rs, colIndex);
      break;
    case 91:
      value = handleDate(rs, colIndex);
      break;
    case 92:
      value = handleTime(rs.getTime(colIndex));
      break;
    case 93:
      value = handleTimestamp(rs.getTimestamp(colIndex));
      break;
    case -16:
    case -15:
    case -9:
    case -1:
    case 1:
    case 12:
      value = rs.getString(colIndex);
      break;
    default:
      value = "";
    }

    if (value == null)
    {
      value = "";
    }

    return value;
  }

  private static String read(Clob c)
    throws SQLException, IOException
  {
    StringBuilder sb = new StringBuilder((int)c.length());
    Reader r = c.getCharacterStream();
    char[] cbuf = new char[2048];
    int n;
    while ((n = r.read(cbuf, 0, cbuf.length)) != -1) {
      sb.append(cbuf, 0, n);
    }
    return sb.toString();
  }
}

/* Location:           C:\Users\Alexander\Downloads\MCPC-Plus-master\MCPC-Plus-master\target\mcpc-plus-1.5.1-R0.1-SNAPSHOT\
 * Qualified Name:     au.com.bytecode.opencsv.ResultSetHelperService
 * JD-Core Version:    0.6.2
 */