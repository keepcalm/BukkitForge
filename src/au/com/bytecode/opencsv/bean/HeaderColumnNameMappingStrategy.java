package au.com.bytecode.opencsv.bean;

import au.com.bytecode.opencsv.CSVReader;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HeaderColumnNameMappingStrategy<T>
  implements MappingStrategy<T>
{
  protected String[] header;
  protected Map<String, PropertyDescriptor> descriptorMap = null;
  protected Class<T> type;

  public void captureHeader(CSVReader reader)
    throws IOException
  {
    this.header = reader.readNext();
  }

  public PropertyDescriptor findDescriptor(int col) throws IntrospectionException {
    String columnName = getColumnName(col);
    return (null != columnName) && (columnName.trim().length() > 0) ? findDescriptor(columnName) : null;
  }

  protected String getColumnName(int col) {
    return (null != this.header) && (col < this.header.length) ? this.header[col] : null;
  }

  protected PropertyDescriptor findDescriptor(String name) throws IntrospectionException {
    if (null == this.descriptorMap) this.descriptorMap = loadDescriptorMap(getType());
    return (PropertyDescriptor)this.descriptorMap.get(name.toUpperCase().trim());
  }

  protected boolean matches(String name, PropertyDescriptor desc) {
    return desc.getName().equals(name.trim());
  }

  protected Map<String, PropertyDescriptor> loadDescriptorMap(Class<T> cls) throws IntrospectionException {
    Map map = new HashMap();

    PropertyDescriptor[] descriptors = loadDescriptors(getType());
    for (PropertyDescriptor descriptor : descriptors) {
      map.put(descriptor.getName().toUpperCase().trim(), descriptor);
    }

    return map;
  }

  private PropertyDescriptor[] loadDescriptors(Class<T> cls) throws IntrospectionException {
    BeanInfo beanInfo = Introspector.getBeanInfo(cls);
    return beanInfo.getPropertyDescriptors();
  }

  public T createBean() throws InstantiationException, IllegalAccessException {
    return this.type.newInstance();
  }

  public Class<T> getType() {
    return this.type;
  }

  public void setType(Class<T> type) {
    this.type = type;
  }
}

/* Location:           C:\Users\Alexander\Downloads\MCPC-Plus-master\MCPC-Plus-master\target\mcpc-plus-1.5.1-R0.1-SNAPSHOT\
 * Qualified Name:     au.com.bytecode.opencsv.bean.HeaderColumnNameMappingStrategy
 * JD-Core Version:    0.6.2
 */