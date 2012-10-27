package keepcalm.mods.bukkit.asm;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.eventbus.EventBus;

import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.MetadataCollection;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.InvalidVersionSpecificationException;
import cpw.mods.fml.common.versioning.VersionRange;

public abstract class BukkitModContainerBase implements ModContainer {
	protected boolean isModEnabled = true;
	protected VersionRange mcVerRange;
	protected MetadataCollection meta;
	protected ModMetadata modMeta;
	protected List<ArtifactVersion> depends = new ArrayList();
	protected List<ArtifactVersion> dependants = new ArrayList();
	private Object myMod;
	
	public BukkitModContainerBase(Object mod) {
		try {
			mcVerRange = VersionRange.createFromVersionSpec("[1.3,1.3.2]");
		} catch (InvalidVersionSpecificationException e) {
			e.printStackTrace();
		}
		myMod = mod;
		//depends.add(new ArtifactVersion());
	}
	@Override
	public VersionRange acceptableMinecraftVersionRange() {
		return mcVerRange;
	}
	/**
	 * {@inheritDoc}
	 * @param mc
	 */
	@Override
	public void bindMetadata(MetadataCollection mc) {
		this.meta = mc;
	}

	@Override
	public List<ArtifactVersion> getDependants() {
		return dependants;
	}

	@Override
	public List<ArtifactVersion> getDependencies() {
		return depends;
	}

	@Override
	public String getDisplayVersion() {
		return modMeta.version.isEmpty() ? "1.0.0" : modMeta.version;
	}

	@Override
	public ModMetadata getMetadata() {
		return modMeta;
	}

	@Override
	public Object getMod() {
		return myMod;
	}

	@Override
	public String getModId() {
		if (myMod instanceof Mod) {
			return ((Mod) myMod).modid();
		}
		return modMeta.modId;
	}

	@Override
	public String getName() {
		if (myMod instanceof Mod) {
			return ((Mod) myMod).name();
		}
		return modMeta.name;
	}

	@Override
	public ArtifactVersion getProcessedVersion() {
		return null;
	}

	@Override
	public Set<ArtifactVersion> getRequirements() {
		return Collections.EMPTY_SET;
	}

	@Override
	public String getSortingRules() {
		return "";
	}

	@Override
	public File getSource() {
		return null;
	}

	@Override
	public String getVersion() {
		if (myMod instanceof Mod) {
			return ((Mod) myMod).version();
		}
		return modMeta.version;
	}

	@Override
	public boolean isImmutable() {
		return false;
	}

	@Override
	public boolean isNetworkMod() {
		if (myMod instanceof Mod) {
			return true;
		}
		return true;
	}

	@Override
	public boolean matches(Object mod) {
		return this.myMod.equals(mod);
	}

	
	public abstract boolean registerBus(EventBus bus, LoadController controller);

	@Override
	public void setEnabledState(boolean enabled) {
		this.isModEnabled = enabled;
	}

}
