package au.com.bytecode.opencsv.bean;

import au.com.bytecode.opencsv.CSVReader;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;

public abstract interface MappingStrategy<T>
{
  public abstract PropertyDescriptor findDescriptor(int paramInt)
    throws IntrospectionException;

  public abstract T createBean()
    throws InstantiationException, IllegalAccessException;

  public abstract void captureHeader(CSVReader paramCSVReader)
    throws IOException;
}

/* Location:           C:\Users\Alexander\Downloads\MCPC-Plus-master\MCPC-Plus-master\target\mcpc-plus-1.5.1-R0.1-SNAPSHOT\
 * Qualified Name:     au.com.bytecode.opencsv.bean.MappingStrategy
 * JD-Core Version:    0.6.2
 */