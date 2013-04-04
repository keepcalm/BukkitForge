package au.com.bytecode.opencsv.bean;

import au.com.bytecode.opencsv.CSVReader;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CsvToBean<T>
{
  private Map<Class<?>, PropertyEditor> editorMap = null;

  public List<T> parse(MappingStrategy<T> mapper, Reader reader)
  {
    return parse(mapper, new CSVReader(reader));
  }

  public List<T> parse(MappingStrategy<T> mapper, CSVReader csv) {
    try {
      mapper.captureHeader(csv);

      List list = new ArrayList();
      String[] line;
      while (null != (line = csv.readNext())) {
        Object obj = processLine(mapper, line);
        list.add(obj);
      }
      return list;
    } catch (Exception e) {
      throw new RuntimeException("Error parsing CSV!", e);
    }
  }

  protected T processLine(MappingStrategy<T> mapper, String[] line) throws IllegalAccessException, InvocationTargetException, InstantiationException, IntrospectionException {
    Object bean = mapper.createBean();
    for (int col = 0; col < line.length; col++) {
      PropertyDescriptor prop = mapper.findDescriptor(col);
      if (null != prop) {
        String value = checkForTrim(line[col], prop);
        Object obj = convertValue(value, prop);
        prop.getWriteMethod().invoke(bean, new Object[] { obj });
      }
    }
    return (T) bean;
  }

  private String checkForTrim(String s, PropertyDescriptor prop) {
    return trimmableProperty(prop) ? s.trim() : s;
  }

  private boolean trimmableProperty(PropertyDescriptor prop) {
    return !prop.getPropertyType().getName().contains("String");
  }

  protected Object convertValue(String value, PropertyDescriptor prop) throws InstantiationException, IllegalAccessException {
    PropertyEditor editor = getPropertyEditor(prop);
    Object obj = value;
    if (null != editor) {
      editor.setAsText(value);
      obj = editor.getValue();
    }
    return obj;
  }

  private PropertyEditor getPropertyEditorValue(Class<?> cls) {
    if (this.editorMap == null) {
      this.editorMap = new HashMap();
    }

    PropertyEditor editor = (PropertyEditor)this.editorMap.get(cls);

    if (editor == null) {
      editor = PropertyEditorManager.findEditor(cls);
      addEditorToMap(cls, editor);
    }

    return editor;
  }

  private void addEditorToMap(Class<?> cls, PropertyEditor editor) {
    if (editor != null)
      this.editorMap.put(cls, editor);
  }

  protected PropertyEditor getPropertyEditor(PropertyDescriptor desc)
    throws InstantiationException, IllegalAccessException
  {
    Class cls = desc.getPropertyEditorClass();
    if (null != cls) return (PropertyEditor)cls.newInstance();
    return getPropertyEditorValue(desc.getPropertyType());
  }
}

/* Location:           C:\Users\Alexander\Downloads\MCPC-Plus-master\MCPC-Plus-master\target\mcpc-plus-1.5.1-R0.1-SNAPSHOT\
 * Qualified Name:     au.com.bytecode.opencsv.bean.CsvToBean
 * JD-Core Version:    0.6.2
 */