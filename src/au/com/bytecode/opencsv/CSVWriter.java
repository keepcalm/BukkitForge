package au.com.bytecode.opencsv;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CSVWriter
  implements Closeable
{
  public static final int INITIAL_STRING_SIZE = 128;
  private Writer rawWriter;
  private PrintWriter pw;
  private char separator;
  private char quotechar;
  private char escapechar;
  private String lineEnd;
  public static final char DEFAULT_ESCAPE_CHARACTER = '"';
  public static final char DEFAULT_SEPARATOR = ',';
  public static final char DEFAULT_QUOTE_CHARACTER = '"';
  public static final char NO_QUOTE_CHARACTER = '\000';
  public static final char NO_ESCAPE_CHARACTER = '\000';
  public static final String DEFAULT_LINE_END = "\n";
  private ResultSetHelper resultService = new ResultSetHelperService();

  public CSVWriter(Writer writer)
  {
    this(writer, ',');
  }

  public CSVWriter(Writer writer, char separator)
  {
    this(writer, separator, '"');
  }

  public CSVWriter(Writer writer, char separator, char quotechar)
  {
    this(writer, separator, quotechar, '"');
  }

  public CSVWriter(Writer writer, char separator, char quotechar, char escapechar)
  {
    this(writer, separator, quotechar, escapechar, "\n");
  }

  public CSVWriter(Writer writer, char separator, char quotechar, String lineEnd)
  {
    this(writer, separator, quotechar, '"', lineEnd);
  }

  public CSVWriter(Writer writer, char separator, char quotechar, char escapechar, String lineEnd)
  {
    this.rawWriter = writer;
    this.pw = new PrintWriter(writer);
    this.separator = separator;
    this.quotechar = quotechar;
    this.escapechar = escapechar;
    this.lineEnd = lineEnd;
  }

  public void writeAll(List<String[]> allLines)
  {
    for (String[] line : allLines)
      writeNext(line);
  }

  protected void writeColumnNames(ResultSet rs)
    throws SQLException
  {
    writeNext(this.resultService.getColumnNames(rs));
  }

  public void writeAll(ResultSet rs, boolean includeColumnNames)
    throws SQLException, IOException
  {
    if (includeColumnNames) {
      writeColumnNames(rs);
    }

    while (rs.next())
    {
      writeNext(this.resultService.getColumnValues(rs));
    }
  }

  public void writeNext(String[] nextLine)
  {
    if (nextLine == null) {
      return;
    }
    StringBuilder sb = new StringBuilder(128);
    for (int i = 0; i < nextLine.length; i++)
    {
      if (i != 0) {
        sb.append(this.separator);
      }

      String nextElement = nextLine[i];
      if (nextElement != null)
      {
        if (this.quotechar != 0) {
          sb.append(this.quotechar);
        }
        sb.append(stringContainsSpecialCharacters(nextElement) ? processLine(nextElement) : nextElement);

        if (this.quotechar != 0)
          sb.append(this.quotechar);
      }
    }
    sb.append(this.lineEnd);
    this.pw.write(sb.toString());
  }

  private boolean stringContainsSpecialCharacters(String line)
  {
    return (line.indexOf(this.quotechar) != -1) || (line.indexOf(this.escapechar) != -1);
  }

  protected StringBuilder processLine(String nextElement)
  {
    StringBuilder sb = new StringBuilder(128);
    for (int j = 0; j < nextElement.length(); j++) {
      char nextChar = nextElement.charAt(j);
      if ((this.escapechar != 0) && (nextChar == this.quotechar))
        sb.append(this.escapechar).append(nextChar);
      else if ((this.escapechar != 0) && (nextChar == this.escapechar))
        sb.append(this.escapechar).append(nextChar);
      else {
        sb.append(nextChar);
      }
    }

    return sb;
  }

  public void flush()
    throws IOException
  {
    this.pw.flush();
  }

  public void close()
    throws IOException
  {
    flush();
    this.pw.close();
    this.rawWriter.close();
  }

  public boolean checkError()
  {
    return this.pw.checkError();
  }

  public void setResultService(ResultSetHelper resultService) {
    this.resultService = resultService;
  }
}

/* Location:           C:\Users\Alexander\Downloads\MCPC-Plus-master\MCPC-Plus-master\target\mcpc-plus-1.5.1-R0.1-SNAPSHOT\
 * Qualified Name:     au.com.bytecode.opencsv.CSVWriter
 * JD-Core Version:    0.6.2
 */