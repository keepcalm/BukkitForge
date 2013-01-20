package keepcalm.mods.bukkit.utils;

import java.util.ArrayList;
import java.util.Collection;

public class CaseInsensitiveArrayList extends ArrayList<String> {

	public CaseInsensitiveArrayList(String[] vals) {
		for (String i : vals) {
			super.add(i.toLowerCase());
		}
	}
	
	public CaseInsensitiveArrayList() {}

	
	@Override
	public boolean contains(Object i) {
		if (!(i instanceof String)) return false;
		return super.contains(((String)i).toLowerCase());
	}
	
	@Override
	public boolean add(String i) {
		
		return super.add(i.toLowerCase());
		
	}
	
	@Override
	public boolean remove(Object i) {
		if (!(i instanceof String)) return false;
		
		return super.remove(((String) i).toLowerCase());
	}
	
	@Override
	public boolean addAll(Collection<? extends String> b) {
		CaseInsensitiveArrayList x = new CaseInsensitiveArrayList(b.toArray(new String[0]));
		return super.addAll(x);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CaseInsensitiveArrayList)) return false;
		CaseInsensitiveArrayList cial = (CaseInsensitiveArrayList) obj;
		
		for (String i : cial) {
			if (!contains(i)) {
				return false;
			}
		}
		
		for (String i : this) {
			if (!cial.contains(i)) {
				return false;
			}
		}
		return true;
	}
}
