package au.com.bytecode.opencsv;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class CSVReader
  implements Closeable
{
  private BufferedReader br;
  private boolean hasNext = true;
  private CSVParser parser;
  private int skipLines;
  private boolean linesSkiped;
  public static final int DEFAULT_SKIP_LINES = 0;

  public CSVReader(Reader reader)
  {
    this(reader, ',', '"', '\\');
  }

  public CSVReader(Reader reader, char separator)
  {
    this(reader, separator, '"', '\\');
  }

  public CSVReader(Reader reader, char separator, char quotechar)
  {
    this(reader, separator, quotechar, '\\', 0, false);
  }

  public CSVReader(Reader reader, char separator, char quotechar, boolean strictQuotes)
  {
    this(reader, separator, quotechar, '\\', 0, strictQuotes);
  }

  public CSVReader(Reader reader, char separator, char quotechar, char escape)
  {
    this(reader, separator, quotechar, escape, 0, false);
  }

  public CSVReader(Reader reader, char separator, char quotechar, int line)
  {
    this(reader, separator, quotechar, '\\', line, false);
  }

  public CSVReader(Reader reader, char separator, char quotechar, char escape, int line)
  {
    this(reader, separator, quotechar, escape, line, false);
  }

  public CSVReader(Reader reader, char separator, char quotechar, char escape, int line, boolean strictQuotes)
  {
    this(reader, separator, quotechar, escape, line, strictQuotes, true);
  }

  public CSVReader(Reader reader, char separator, char quotechar, char escape, int line, boolean strictQuotes, boolean ignoreLeadingWhiteSpace)
  {
    this.br = new BufferedReader(reader);
    this.parser = new CSVParser(separator, quotechar, escape, strictQuotes, ignoreLeadingWhiteSpace);
    this.skipLines = line;
  }

  public List<String[]> readAll()
    throws IOException
  {
    List allElements = new ArrayList();
    while (this.hasNext) {
      String[] nextLineAsTokens = readNext();
      if (nextLineAsTokens != null)
        allElements.add(nextLineAsTokens);
    }
    return allElements;
  }

  public String[] readNext()
    throws IOException
  {
    String[] result = null;
    do {
      String nextLine = getNextLine();
      if (!this.hasNext) {
        return result;
      }
      String[] r = this.parser.parseLineMulti(nextLine);
      if (r.length > 0)
        if (result == null) {
          result = r;
        } else {
          String[] t = new String[result.length + r.length];
          System.arraycopy(result, 0, t, 0, result.length);
          System.arraycopy(r, 0, t, result.length, r.length);
          result = t;
        }
    }
    while (this.parser.isPending());
    return result;
  }

  private String getNextLine()
    throws IOException
  {
    if (!this.linesSkiped) {
      for (int i = 0; i < this.skipLines; i++) {
        this.br.readLine();
      }
      this.linesSkiped = true;
    }
    String nextLine = this.br.readLine();
    if (nextLine == null) {
      this.hasNext = false;
    }
    return this.hasNext ? nextLine : null;
  }

  public void close()
    throws IOException
  {
    this.br.close();
  }
}

/* Location:           C:\Users\Alexander\Downloads\MCPC-Plus-master\MCPC-Plus-master\target\mcpc-plus-1.5.1-R0.1-SNAPSHOT\
 * Qualified Name:     au.com.bytecode.opencsv.CSVReader
 * JD-Core Version:    0.6.2
 */