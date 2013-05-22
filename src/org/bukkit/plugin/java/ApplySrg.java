/*
 * Copyright 2012 Frans-Willem Hardijzer
 * This file is part of SrgTools.
 *
 * SrgTools is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SrgTools is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SrgTools.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.bukkit.plugin.java;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.commons.RemappingClassAdapter;


class Method implements Comparable<Method> {
	public String name;
	public String desc;
	public Method(String strName, String strArguments) {
		this.name=strName;
		this.desc=strArguments;
	}

	@Override
	public int compareTo(Method b) {
		int cmpName=name.compareTo(b.name);
		int cmpDesc=desc.compareTo(b.desc);
		if (cmpName!=0) return cmpName;
		return cmpDesc;
	}
}

class MappedClass {
	public String strNewName;
	public Map<String,String> mapFields;
	public Map<Method,String> mapMethods;

	public MappedClass(String strNewName) {
		this.strNewName=strNewName;
		this.mapFields=new TreeMap<String,String>();
		this.mapMethods=new TreeMap<Method,String>();
	}
}

class ClassInfo {
	public Set<String> setFields;
	public Set<Method> setMethods;
	public Set<String> setInheritance;

	public ClassInfo() {
		setFields=new TreeSet<String>();
		setMethods=new TreeSet<Method>();
		setInheritance=new TreeSet<String>();
	}
}

class MyRemapper extends Remapper {
	public Map<String,MappedClass> mapClasses;
	public Map<String,ClassInfo> mapClassInfo;
	public Map<String,String> mapPackages;

	public MyRemapper(Map<String,MappedClass> mapClasses, Map<String,ClassInfo> mapInheritance, Map<String,String> mapPackages) {
		super();
		this.mapClasses=mapClasses;
		this.mapClassInfo=mapInheritance;
		this.mapPackages=mapPackages;
	}

	public String mapPackage(String strPackage) {
		String strMapped=mapPackages.get(strPackage);
		if (strMapped!=null)
			return strMapped;
		if (strPackage.equals("."))
			return strPackage;
		int nSplit=strPackage.lastIndexOf('/');
		String strUp=".";
		if (nSplit!=-1) {
			strUp=strPackage.substring(0,nSplit);
			strPackage=strPackage.substring(nSplit+1);
		}
		String strUpMapped=mapPackage(strUp);
		if (strUpMapped.equals("."))
			return strPackage;
		return strUpMapped+"/"+strPackage;
	}

	@Override
	public String map(String typeName) {
		MappedClass other=mapClasses.get(typeName);
		if (other!=null)
			return other.strNewName;
		int nSplit=typeName.lastIndexOf('$');
		if (nSplit!=-1) {
			return map(typeName.substring(0,nSplit))+typeName.substring(nSplit);
		}
		String strPackage=".";
		nSplit=typeName.lastIndexOf('/');
		if (nSplit!=-1) {
			strPackage=typeName.substring(0,nSplit);
			typeName=typeName.substring(nSplit+1);
		}
		String strPackageMapped=mapPackage(strPackage);
		if (strPackageMapped.equals("."))
			return typeName;
		return strPackageMapped+"/"+typeName;
	}

	public String mapMethodNameDirect(String owner, Method m) {
		MappedClass mappedClass=mapClasses.get(owner);
		if (mappedClass!=null) {
			String strMapped=mappedClass.mapMethods.get(m);
			if (strMapped!=null) {
				ApplySrg.debugLog("Mapping method "+owner+"/"+m.name+" to new name "+strMapped);
				return strMapped;
			}
		}
		return null;
	}

	public String mapFieldNameDirect(String owner, String f) {
		MappedClass mappedClass=mapClasses.get(owner);
		if (mappedClass!=null) {
			String strMapped=mappedClass.mapFields.get(f);
			if (strMapped!=null) {
				ApplySrg.debugLog("Mapping field "+owner+"/"+f+" to new name "+strMapped);
				return strMapped;
			}
		}
		return null;
	}

	//Method can point to a method up the chain, but also further up the chain, so returns the first owner with a mapping associated.
	public String locateMethod(String owner, Method m) {
		ApplySrg.debugLog("locating method in class "+owner+" method "+m.name+" "+m.desc);
		Queue<String> q=new LinkedList<String>();
		q.add(owner);
		while (!q.isEmpty()) {
			String strOwner=q.remove();
			ApplySrg.debugLog(" - checking owner "+strOwner);
			ClassInfo info=mapClassInfo.get(strOwner);
			ApplySrg.debugLog(" -- info="+info);
			if (info==null)
				continue;
			for (String inherit : info.setInheritance)
				q.add(inherit);
			if (mapClasses.containsKey(strOwner) && info.setMethods.contains(m)) {
				ApplySrg.debugLog(" found at "+strOwner);
				return strOwner;
			}
		}
		return null;
	}

	@Override
	public String mapMethodName(String owner, String name, String desc) {
		Method m=new Method(name,desc);
		String strActualOwner=locateMethod(owner,m);
		if (strActualOwner!=null)
			ApplySrg.debugLog("Tracked method "+owner+"/"+name+" "+desc+" to "+strActualOwner);
		String strMapped=mapMethodNameDirect((strActualOwner!=null)?strActualOwner:owner,m);
		return (strMapped==null)?name:strMapped;
	}

	//Field always maps to the first field found up the chain.
	public String locateField(String owner, String f) {
		Queue<String> q=new LinkedList<String>();
		q.add(owner);
		while (!q.isEmpty()) {
			String strOwner=q.remove();
			ClassInfo info=mapClassInfo.get(strOwner);
			if (info==null)
				continue;
			for (String inherit : info.setInheritance)
				q.add(inherit);
			if (info.setFields.contains(f))
				return strOwner;
		}
		return null;
	}

	@Override
	public String mapFieldName(String owner, String name, String desc) {
		String strActualOwner=locateField(owner,name);
		if (strActualOwner!=null)
			ApplySrg.debugLog("Tracked field "+owner+"/"+name+" "+desc+" to "+strActualOwner);
		String strMapped=mapFieldNameDirect((strActualOwner!=null)?strActualOwner:owner,name);
		return (strMapped==null)?name:strMapped;
	}
}

class InheritanceMapClassVisitor extends ClassVisitor {
	public String strName;
	public ClassInfo info;

	public InheritanceMapClassVisitor() {
		super(Opcodes.ASM4);
		strName="";
		info=new ClassInfo();
	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		this.strName=name;
		this.info.setInheritance.add(superName);
		for (int i=0; i<interfaces.length; i++) {
			ApplySrg.debugLog("inheritance for "+name+" super "+superName+" interface "+interfaces[i]);
			this.info.setInheritance.add(interfaces[i]);
		}
	}

	@Override
	public AnnotationVisitor visitAnnotation(String arg0, boolean arg1) {
		return null;
	}

	@Override
	public void visitAttribute(Attribute arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitEnd() {
		// TODO Auto-generated method stub

	}

	@Override
	public FieldVisitor visitField(int access, String name, String desc,
			String signature, Object value) {
		this.info.setFields.add(name);
		return null;
	}

	@Override
	public void visitInnerClass(String arg0, String arg1, String arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] exceptions) {
		this.info.setMethods.add(new Method(name,desc));
		return null;
	}

	@Override
	public void visitOuterClass(String arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
	}

	@Override
	public void visitSource(String arg0, String arg1) {
		// TODO Auto-generated method stub
	}

}

class ParseEntry {
	String strInputFilename;
	String strOutputFilename;

	public ParseEntry(String a, String b) {
		strInputFilename=a;
		strOutputFilename=b;
	}
};

public class ApplySrg {
	private static final boolean DEBUG_PLUGINREMAP = Boolean.parseBoolean(System.getProperty("mcpc.debugPluginRemap", "false"));
	private static final boolean INFO_PLUGINREMAP = Boolean.parseBoolean(System.getProperty("mcpc.infoPluginRemap", "true"));

	public static void debugLog(String message) {
		if (DEBUG_PLUGINREMAP) {
			System.out.println("ApplySrg: "+message);
		}
	}

	public static void infoLog(String message) {
		if (INFO_PLUGINREMAP) {
			System.out.println("ApplySrg: "+message);
		}
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(File file, InputStream srgStream) throws IOException {
		String strInputFilename=file.getAbsolutePath();
		String strOutputFilename=file.getParentFile().getAbsolutePath() + "/ported_" + file.getName();
		List<String> listInputSrg=new LinkedList<String>();
		List<String> listInputInheritance=new LinkedList<String>();
		listInputInheritance.add(strInputFilename);
		List<ParseEntry> listTranslate=new LinkedList<ParseEntry>();
		/*for (int i=0; i<args.length; i++) {
			if (args[i].equals("--srg"))
				listInputSrg.add(args[++i]);
			else if (args[i].equals("--inheritance") || args[i].equals("--inh"))
				listInputInheritance.add(args[++i]);
			else if (args[i].equals("--in"))
				strInputFilename=args[++i];
			else if (args[i].equals("--out"))
				strOutputFilename=args[++i];
			else if (args[i].equals("--translate")) {
				String a=args[++i];
				String b=args[++i];
				listTranslate.add(new ParseEntry(a,b));
				listInputInheritance.add(a);
			}
		}
		 */		if (strInputFilename!=null && strOutputFilename!=null) {
			 listTranslate.add(new ParseEntry(strInputFilename,strOutputFilename));
			 strInputFilename=strOutputFilename=null;
		 }
		 /*	if (strInputFilename!=null || strOutputFilename!=null || listTranslate.size()<1) {
			System.err.println("Usage: java -jar srgtool.jar apply [options]");
			System.err.println("Options:");
			System.err.println("--srg <srg file>\tLoads the SRG file");
			System.err.println("--inheritance <jar/zip>\tLoads inheritance map from jar");
			System.err.println("--in <jar/zip>");
			System.err.println("--out <jar/zip>");
			return;
		}
		  */
		 Map<String,MappedClass> mapClasses=new TreeMap<String,MappedClass>();
		 Map<String, String> mapPackages=new TreeMap<String,String>();
		 //for (String srg : listInputSrg) {
		 ApplySrg.infoLog("Reading SRG!");
		 
		 
		 BufferedReader brSrg = new BufferedReader(new InputStreamReader(srgStream));
		 String strLine;
		 while ((strLine=brSrg.readLine())!=null) {
			 String arrLine[]=strLine.split(" ");
			 if (arrLine[0].equals("PK:")) {
				 mapPackages.put(arrLine[1], arrLine[2]);
			 } else if (arrLine[0].equals("CL:")) {
				 String strFrom=arrLine[1];
				 String strTo=arrLine[2];
				 MappedClass mappedCurrent=mapClasses.get(strFrom);
				 if (mappedCurrent==null) {
					 mapClasses.put(strFrom,new MappedClass(strTo));
				 } else {
					 if (!mappedCurrent.strNewName.equals(strTo)) {
						 System.err.println("ERROR: Mismatching mappings found");
						 return;
					 }
				 }
			 } else if (arrLine[0].equals("FD:")) {
				 String strFrom=arrLine[1];
				 String strTo=arrLine[2];
				 int nSplitFrom=strFrom.lastIndexOf('/');
				 int nSplitTo=strTo.lastIndexOf('/');
				 if (nSplitFrom==-1 || nSplitTo==-1) {
					 System.err.println("ERROR: Invalid field specification: '" + strLine);
					 return;
				 }
				 String strFromClass=strFrom.substring(0,nSplitFrom);
				 strFrom=strFrom.substring(nSplitFrom+1);
				 String strToClass=strTo.substring(0,nSplitTo);
				 strTo=strTo.substring(nSplitTo+1);
				 MappedClass mappedCurrent=mapClasses.get(strFromClass);
				 if (strFromClass.equals(strToClass) && mappedCurrent==null) {
					 mapClasses.put(strFromClass,mappedCurrent=new MappedClass(strToClass));
				 }
				 if (mappedCurrent==null || !mappedCurrent.strNewName.equals(strToClass)) {
					 System.err.println("ERROR: Class mapping invalid or non-existant on field");
					 System.err.println("Line: "+strLine);
					 System.err.println(strFromClass+" -> "+strToClass+" should have been "+((mappedCurrent==null)?"null":mappedCurrent.strNewName));
					 return;
				 }
				 mappedCurrent.mapFields.put(strFrom,strTo);
			 } else if (arrLine[0].equals("MD:")) {
				 String strFrom=arrLine[1];
				 String strFromArguments=arrLine[2];
				 String strTo=arrLine[3];
				 String strToArguments=arrLine[4];
				 int nSplitFrom=strFrom.lastIndexOf('/');
				 int nSplitTo=strTo.lastIndexOf('/');
				 if (nSplitFrom==-1 || nSplitTo==-1) {
					 System.err.println("ERROR: Invalid method specification: '" + strLine);
					 return;
				 }
				 String strFromClass=strFrom.substring(0,nSplitFrom);
				 strFrom=strFrom.substring(nSplitFrom+1);
				 String strToClass=strTo.substring(0,nSplitTo);
				 strTo=strTo.substring(nSplitTo+1);
				 MappedClass mappedCurrent=mapClasses.get(strFromClass);
				 if (strFromClass.equals(strToClass) && mappedCurrent==null) {
					 mapClasses.put(strFromClass,mappedCurrent=new MappedClass(strToClass));
				 }
				 if (mappedCurrent==null || !mappedCurrent.strNewName.equals(strToClass)) {
					 System.err.println("ERROR: Class mapping invalid or non-existant on method");
					 System.err.println("Line: "+strLine);
					 System.err.println(strFromClass+" -> "+strToClass+" should have been "+((mappedCurrent==null)?"null":mappedCurrent.strNewName));
					 return;
				 }
				 //NOTE: arguments not saved, will be mapped automagically.
				 mappedCurrent.mapMethods.put(new Method(strFrom,strFromArguments),strTo);
			 }
		 }
		 //}
		ApplySrg.infoLog("Class map loaded of "+mapClasses.size()+" classes");
		if (DEBUG_PLUGINREMAP) {
			for (Map.Entry<String, MappedClass> entry : mapClasses.entrySet()) {
				ApplySrg.debugLog(" class "+entry.getKey() + " -> "+entry.getValue().strNewName);
				for (Map.Entry<String, String> fieldEntry : entry.getValue().mapFields.entrySet()) {
					ApplySrg.debugLog("  field "+fieldEntry.getKey()+" -> "+fieldEntry.getValue());
				}
				for (Map.Entry<Method, String> methodEntry : entry.getValue().mapMethods.entrySet()) {
					ApplySrg.debugLog("  method "+methodEntry.getKey().name+" "+methodEntry.getKey().desc+" -> "+methodEntry.getValue());
				}
			}
		}
		 Map<String,ClassInfo> mapClassInheritance=new HashMap<String,ClassInfo>();
		 for (String inherit : listInputInheritance) {
			 ApplySrg.infoLog("Parsing inheritance in "+inherit);
			 ZipFile zipInherit=new ZipFile(inherit);
			 Enumeration<? extends ZipEntry> entries=zipInherit.entries();
			 while (entries.hasMoreElements()) {
				 ZipEntry entry=entries.nextElement();
				 if (entry.isDirectory())
					 continue;
				 if (entry.getName().endsWith(".class")) {
					 ClassReader cr=new ClassReader(zipInherit.getInputStream(entry));
					 InheritanceMapClassVisitor cvInheritance=new InheritanceMapClassVisitor();
					 cr.accept(cvInheritance,0);
					 mapClassInheritance.put(cvInheritance.strName,cvInheritance.info);
				 }
			 }
			 zipInherit.close();
		 }
		 ApplySrg.infoLog("Inheritance map loaded of "+mapClassInheritance.size()+" classes");
		 if (DEBUG_PLUGINREMAP) {
			 for (String className : mapClassInheritance.keySet()) {
				 ApplySrg.debugLog(" inheritance map class "+className);
				 ClassInfo info = mapClassInheritance.get(className);
				 for (String inherit : info.setInheritance) {
					 ApplySrg.debugLog("  inherit "+inherit);
				 }
			 }
		 }

		 for (ParseEntry e : listTranslate) {
			 ApplySrg.infoLog("Translating "+e.strInputFilename+" -> "+e.strOutputFilename);

			 ZipFile zipInput=new ZipFile(e.strInputFilename);

			 ZipOutputStream zipOutput=new ZipOutputStream(new FileOutputStream(e.strOutputFilename));
			 Enumeration<? extends ZipEntry> entries=zipInput.entries();
			 while (entries.hasMoreElements()) {
				 ZipEntry entry=entries.nextElement();
				 if (entry.isDirectory())
					 continue;
				 if (entry.getName().endsWith(".class")) {
					 ClassReader cr=new ClassReader(zipInput.getInputStream(entry));
					 ClassWriter cw=new ClassWriter(0);
					 Remapper remapper=new MyRemapper(mapClasses,mapClassInheritance,mapPackages);
					 RemappingClassAdapter ca=new RemappingClassAdapter(cw,remapper);
					 cr.accept(ca,ClassReader.EXPAND_FRAMES);
					 byte[] bOutput=cw.toByteArray();
					 String strName=entry.getName();
					 strName=strName.substring(0,strName.lastIndexOf('.'));
					 strName=remapper.mapType(strName);

					 ZipEntry entryCopy=new ZipEntry(strName+".class");
					 entryCopy.setCompressedSize(-1);
					 entryCopy.setSize(bOutput.length);
					 zipOutput.putNextEntry(entryCopy);
					 zipOutput.write(bOutput);
					 zipOutput.closeEntry();
				 } else {
					 ZipEntry entryCopy=new ZipEntry(entry);
					 entryCopy.setCompressedSize(-1);
					 zipOutput.putNextEntry(entryCopy);
					 InputStream is=zipInput.getInputStream(entry);
					 byte[] buffer=new byte[1024];
					 int read=0;
					 while ((read=is.read(buffer))!=-1)
						 zipOutput.write(buffer,0,read);
					 zipOutput.closeEntry();
				 }
			 }
			 zipInput.close();
			 zipOutput.close();
		 }
		 ApplySrg.infoLog("Done!");
	}

}
