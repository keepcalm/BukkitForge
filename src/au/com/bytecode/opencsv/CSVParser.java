package au.com.bytecode.opencsv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVParser
{
  private final char separator;
  private final char quotechar;
  private final char escape;
  private final boolean strictQuotes;
  private String pending;
  private boolean inField = false;
  private final boolean ignoreLeadingWhiteSpace;
  public static final char DEFAULT_SEPARATOR = ',';
  public static final int INITIAL_READ_SIZE = 128;
  public static final char DEFAULT_QUOTE_CHARACTER = '"';
  public static final char DEFAULT_ESCAPE_CHARACTER = '\\';
  public static final boolean DEFAULT_STRICT_QUOTES = false;
  public static final boolean DEFAULT_IGNORE_LEADING_WHITESPACE = true;
  public static final char NULL_CHARACTER = '\000';

  public CSVParser()
  {
    this(',', '"', '\\');
  }

  public CSVParser(char separator)
  {
    this(separator, '"', '\\');
  }

  public CSVParser(char separator, char quotechar)
  {
    this(separator, quotechar, '\\');
  }

  public CSVParser(char separator, char quotechar, char escape)
  {
    this(separator, quotechar, escape, false);
  }

  public CSVParser(char separator, char quotechar, char escape, boolean strictQuotes)
  {
    this(separator, quotechar, escape, strictQuotes, true);
  }

  public CSVParser(char separator, char quotechar, char escape, boolean strictQuotes, boolean ignoreLeadingWhiteSpace)
  {
    if (anyCharactersAreTheSame(separator, quotechar, escape)) {
      throw new UnsupportedOperationException("The separator, quote, and escape characters must be different!");
    }
    if (separator == 0) {
      throw new UnsupportedOperationException("The separator character must be defined!");
    }
    this.separator = separator;
    this.quotechar = quotechar;
    this.escape = escape;
    this.strictQuotes = strictQuotes;
    this.ignoreLeadingWhiteSpace = ignoreLeadingWhiteSpace;
  }

  private boolean anyCharactersAreTheSame(char separator, char quotechar, char escape) {
    return (isSameCharacter(separator, quotechar)) || (isSameCharacter(separator, escape)) || (isSameCharacter(quotechar, escape));
  }

  private boolean isSameCharacter(char c1, char c2) {
    return (c1 != 0) && (c1 == c2);
  }

  public boolean isPending()
  {
    return this.pending != null;
  }

  public String[] parseLineMulti(String nextLine) throws IOException {
    return parseLine(nextLine, true);
  }

  public String[] parseLine(String nextLine) throws IOException {
    return parseLine(nextLine, false);
  }

  private String[] parseLine(String nextLine, boolean multi)
    throws IOException
  {
    if ((!multi) && (this.pending != null)) {
      this.pending = null;
    }

    if (nextLine == null) {
      if (this.pending != null) {
        String s = this.pending;
        this.pending = null;
        return new String[] { s };
      }
      return null;
    }

    List tokensOnThisLine = new ArrayList();
    StringBuilder sb = new StringBuilder(128);
    boolean inQuotes = false;
    if (this.pending != null) {
      sb.append(this.pending);
      this.pending = null;
      inQuotes = true;
    }
    for (int i = 0; i < nextLine.length(); i++)
    {
      char c = nextLine.charAt(i);
      if (c == this.escape) {
        if (isNextCharacterEscapable(nextLine, (inQuotes) || (this.inField), i)) {
          sb.append(nextLine.charAt(i + 1));
          i++;
        }
      } else if (c == this.quotechar) {
        if (isNextCharacterEscapedQuote(nextLine, (inQuotes) || (this.inField), i)) {
          sb.append(nextLine.charAt(i + 1));
          i++;
        }
        else
        {
          if ((!this.strictQuotes) && 
            (i > 2) && (nextLine.charAt(i - 1) != this.separator) && (nextLine.length() > i + 1) && (nextLine.charAt(i + 1) != this.separator))
          {
            if ((this.ignoreLeadingWhiteSpace) && (sb.length() > 0) && (isAllWhiteSpace(sb)))
              sb.setLength(0);
            else {
              sb.append(c);
            }

          }

          inQuotes = !inQuotes;
        }
        this.inField = (!this.inField);
      } else if ((c == this.separator) && (!inQuotes)) {
        tokensOnThisLine.add(sb.toString());
        sb.setLength(0);
        this.inField = false;
      }
      else if ((!this.strictQuotes) || (inQuotes)) {
        sb.append(c);
        this.inField = true;
      }

    }

    if (inQuotes) {
      if (multi)
      {
        sb.append("\n");
        this.pending = sb.toString();
        sb = null;
      } else {
        throw new IOException("Un-terminated quoted field at end of CSV line");
      }
    }
    if (sb != null) {
      tokensOnThisLine.add(sb.toString());
    }
    return (String[])tokensOnThisLine.toArray(new String[tokensOnThisLine.size()]);
  }

  private boolean isNextCharacterEscapedQuote(String nextLine, boolean inQuotes, int i)
  {
    return (inQuotes) && (nextLine.length() > i + 1) && (nextLine.charAt(i + 1) == this.quotechar);
  }

  protected boolean isNextCharacterEscapable(String nextLine, boolean inQuotes, int i)
  {
    return (inQuotes) && (nextLine.length() > i + 1) && ((nextLine.charAt(i + 1) == this.quotechar) || (nextLine.charAt(i + 1) == this.escape));
  }

  protected boolean isAllWhiteSpace(CharSequence sb)
  {
    boolean result = true;
    for (int i = 0; i < sb.length(); i++) {
      char c = sb.charAt(i);

      if (!Character.isWhitespace(c)) {
        return false;
      }
    }
    return result;
  }
}

/* Location:           C:\Users\Alexander\Downloads\MCPC-Plus-master\MCPC-Plus-master\target\mcpc-plus-1.5.1-R0.1-SNAPSHOT\
 * Qualified Name:     au.com.bytecode.opencsv.CSVParser
 * JD-Core Version:    0.6.2
 */