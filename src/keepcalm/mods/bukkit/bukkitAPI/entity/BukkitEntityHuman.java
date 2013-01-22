package keepcalm.mods.bukkit.bukkitAPI.entity;

import java.util.Set;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import keepcalm.mods.bukkit.bukkitAPI.event.BukkitEventFactory;
import keepcalm.mods.bukkit.bukkitAPI.inventory.BukkitContainer;
import keepcalm.mods.bukkit.bukkitAPI.inventory.BukkitInventory;
import keepcalm.mods.bukkit.bukkitAPI.inventory.BukkitInventoryCrafting;
import keepcalm.mods.bukkit.bukkitAPI.inventory.BukkitInventoryEnchanting;
import keepcalm.mods.bukkit.bukkitAPI.inventory.BukkitInventoryPlayer;
import keepcalm.mods.bukkit.bukkitAPI.inventory.BukkitInventoryView;
import keepcalm.mods.bukkit.bukkitAPI.inventory.BukkitItemStack;
import keepcalm.mods.bukkit.forgeHandler.ConnectionHandler;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.network.packet.Packet100OpenWindow;
import net.minecraft.network.packet.Packet101CloseWindow;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityFurnace;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

public class BukkitEntityHuman extends BukkitLivingEntity implements HumanEntity  {
	private BukkitInventoryPlayer inventory;
	private final BukkitInventory enderChest;
	protected final PermissibleBase perm = new PermissibleBase(this);
	private boolean op;
	private GameMode mode;

	public BukkitEntityHuman(BukkitServer server, EntityLiving entity) {
		super(server, entity);
		this.inventory = new BukkitInventoryPlayer(((EntityPlayer) entity).inventory);
		this.enderChest = new BukkitInventory(((EntityPlayer) entity).getInventoryEnderChest());
	}
	public String getName() {
		return getHandle().username;
	}

	public PlayerInventory getInventory() {
		return (PlayerInventory) inventory;
	}

	public Inventory getEnderChest() {
		return enderChest;
	}

	public ItemStack getItemInHand() {
		return getInventory().getItemInHand();
	}

	public void setItemInHand(ItemStack item) {
		getInventory().setItemInHand(item);
	}

	public ItemStack getItemOnCursor() {
		return new BukkitItemStack(getHandle().inventory.getCurrentItem());
	}
	/**
	 * UNIMPLEMENTED - TODO
	 */
	public void setItemOnCursor(ItemStack item) {
		/*
		net.minecraft.src.ItemStack stack = BukkitItemStack.createNMSItemStack(item);
		getHandle().inventory.setItemStack(stack);
		if (this instanceof BukkitPlayer) {
			((EntityPlayer) getHandle()).send(); // Send set slot for cursor
		}*/
	}

	public boolean isSleeping() {
		return getHandle().isPlayerSleeping();
	}

	public int getSleepTicks() {
		return getHandle().getSleepTimer();
	}

	public boolean isOp() {
		return op;
	}

	public boolean isPermissionSet(String name) {
		return perm.isPermissionSet(name);
	}

	public boolean isPermissionSet(Permission perm) {
		return this.perm.isPermissionSet(perm);
	}

	public boolean hasPermission(String name) {
		return perm.hasPermission(name);
	}

	public boolean hasPermission(Permission perm) {
		return this.perm.hasPermission(perm);
	}

	public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
		return perm.addAttachment(plugin, name, value);
	}

	public PermissionAttachment addAttachment(Plugin plugin) {
		return perm.addAttachment(plugin);
	}

	public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
		return perm.addAttachment(plugin, name, value, ticks);
	}

	public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
		return perm.addAttachment(plugin, ticks);
	}

	public void removeAttachment(PermissionAttachment attachment) {
		perm.removeAttachment(attachment);
	}

	public void recalculatePermissions() {
		perm.recalculatePermissions();
	}

	public void setOp(boolean value) {
		this.op = value;
		perm.recalculatePermissions();
	}

	public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		return perm.getEffectivePermissions();
	}

	public GameMode getGameMode() {
		return mode;
	}

	public void setGameMode(GameMode mode) {
		if (mode == null) {
			throw new IllegalArgumentException("Mode cannot be null");
		}

		this.mode = mode;
	}

	@Override
	public EntityPlayer getHandle() {
		return (EntityPlayer) entity;
	}

	public void setHandle(final EntityPlayer entity) {
		super.setHandle(entity);
		this.inventory = new BukkitInventoryPlayer(entity.inventory);
	}

	@Override
	public String toString() {
		return "BukkitHumanEntity{" + "id=" + getEntityId() + "name=" + getName() + '}';
	}

	public InventoryView getOpenInventory() {
		InventoryBasic nms = new InventoryBasic("", getHandle().openContainer.inventoryItemStacks.size());
		for (int i = 0; i < nms.getSizeInventory(); i++) {
			nms.setInventorySlotContents(i, (net.minecraft.item.ItemStack) getHandle().openContainer.inventoryItemStacks.get(i));
		}
		BukkitInventoryView inv = new BukkitInventoryView(this, new BukkitInventory(nms), getHandle().openContainer);
		
		return inv;
	}

	public InventoryView openInventory(Inventory inventory) {
		if(!(getHandle() instanceof EntityPlayer)) return null;
		EntityPlayerMP player = (EntityPlayerMP) getHandle();
		InventoryType type = inventory.getType();
		Container formerContainer = getHandle().openContainer;
		if (!(inventory instanceof BukkitInventory)) {
			BukkitServer.instance().getLogger().warning("The inventory " + inventory + " is not an instance of BukkitInventory, ignoring open attempt...");
			return null;
		}
		BukkitInventory craftinv = (BukkitInventory) inventory;
		switch(type) {
		case PLAYER:
		case CHEST:
		case ENDER_CHEST:
			getHandle().displayGUIChest(craftinv.getInventory());
			break;
		case DISPENSER:
			if (craftinv.getInventory() instanceof TileEntityDispenser) {
				getHandle().displayGUIDispenser((TileEntityDispenser)craftinv.getInventory());
			} else {
				openCustomInventory(inventory, player, 3);
			}
			break;
		case FURNACE:
			if (craftinv.getInventory() instanceof TileEntityFurnace) {
				getHandle().displayGUIFurnace((TileEntityFurnace)craftinv.getInventory());
			} else {
				openCustomInventory(inventory, player, 2);
			}
			break;
		case WORKBENCH:
			openCustomInventory(inventory, player, 1);
			break;
		case BREWING:
			if (craftinv.getInventory() instanceof TileEntityBrewingStand) {
				getHandle().displayGUIBrewingStand((TileEntityBrewingStand)craftinv.getInventory());
			} else {
				openCustomInventory(inventory, player, 5);
			}
			break;
		case ENCHANTING:
			openCustomInventory(inventory, player, 4);
			break;
		case ANVIL:
			openCustomInventory(inventory, player, 8);
			break;
		case BEACON:
			if (craftinv.getInventory() instanceof TileEntityBeacon) {
				getHandle().displayGUIBeacon((TileEntityBeacon)craftinv.getInventory());
			} else {
				openCustomInventory(craftinv, player, 7);
			}
			break;
		case MERCHANT:
			if (craftinv.getInventory() instanceof IMerchant) {
				getHandle().displayGUIMerchant((IMerchant) craftinv.getInventory());
			}
			else {
				openCustomInventory(craftinv, player, 6);
			}
		case CREATIVE:
		case CRAFTING:
		default:
			throw new IllegalArgumentException("Can't open a " + type + " inventory!");
		}
		if (getHandle().openContainer == formerContainer) {
			return null;
		}
		//getHandle().openContainer. = false;
		return new BukkitInventoryView(this, craftinv, getHandle().openContainer);
	}

	private void openCustomInventory(Inventory inventory, EntityPlayerMP player, int windowType) {
		if (!ConnectionHandler.serverHandlers.containsKey(player.username)) return;
		Container container = new BukkitContainer(inventory, this, (((EntityPlayerMP) player).currentWindowId + 1));

		container = BukkitEventFactory.callInventoryOpenEvent(player, container);
		if(container == null) return;
		BukkitContainer cont = new BukkitContainer(inventory, (HumanEntity) player, windowType);
		String title = cont.getBukkitView().getTitle();// container.();
		int size = cont.getBukkitView().getTopInventory().getSize();

		player.playerNetServerHandler.handleOpenWindow(new Packet100OpenWindow(container.windowId, windowType, title, size));
		getHandle().openContainer = container;
		getHandle().openContainer.setPlayerIsPresent(player, true);
	}

	@SuppressWarnings("unused")
	public InventoryView openWorkbench(Location location, boolean force) {
		Block block = location.getBlock();
		if (!force) {
			
			if (block.getType() != Material.WORKBENCH) {
				return null;
			}
		}
		if (location == null) {
			location = getLocation();
		}
		Inventory workbench = new BukkitInventoryCrafting(new InventoryCrafting(getHandle().openContainer, 3, 3), getHandle().inventory);
		getHandle().displayGUIWorkbench(location.getBlockX(), location.getBlockY(), location.getBlockZ());
		if (force) {
			//getHandle().openContainer. = false;
		}
		return new BukkitInventoryView(this, workbench, getHandle().openContainer);
	}

	public InventoryView openEnchanting(Location location, boolean force) {
		if (!force) {
			Block block = location.getBlock();
			if (block.getType() != Material.ENCHANTMENT_TABLE) {
				return null;
			}
		}
		if (location == null) {
			location = getLocation();
		}
		getHandle().displayGUIEnchantment(location.getBlockX(), location.getBlockY(), location.getBlockZ());
		/*if (force) {
			getHandle().openContainer. = false;
		}*/
		Inventory enchtable = new BukkitInventoryEnchanting(new InventoryBasic("Enchanting", 1));
		return new BukkitInventoryView(this, enchtable, getHandle().openContainer);
	}

	public void openInventory(InventoryView inventory) {
		if (!(getHandle() instanceof EntityPlayer)) return; // TODO: NPC support?
				if (((EntityPlayerMP) getHandle()).playerNetServerHandler == null) return;
		if (getHandle().openContainer != getHandle().inventoryContainer) {
			// fire INVENTORY_CLOSE if one already open
			((EntityPlayerMP)getHandle()).playerNetServerHandler.handleCloseWindow(new Packet101CloseWindow(getHandle().openContainer.windowId));
		}
		EntityPlayerMP player = (EntityPlayerMP) getHandle();
		Container container;
		if (inventory instanceof BukkitInventoryView) {
			container = ((BukkitInventoryView) inventory).getHandle();
		} else {
			container = new BukkitContainer(inventory, player.currentWindowId + 1);
		}

		// Trigger an INVENTORY_OPEN event
		container = BukkitEventFactory.callInventoryOpenEvent(player, container);
		if (container == null) {
			return;
		}

		// Now open the window
		InventoryType type = inventory.getType();
		int windowType = BukkitContainer.getNotchInventoryType(type);
		String title = inventory.getTitle();
		int size = inventory.getTopInventory().getSize();
		player.playerNetServerHandler.sendPacketToPlayer(new Packet100OpenWindow(container.windowId, windowType, title, size));
		player.openContainer = container;
		player.openContainer.setPlayerIsPresent(player, true);
	}

	public void closeInventory() {
		getHandle().closeScreen();
	}

	public boolean isBlocking() {
		return getHandle().isBlocking();
	}

	public boolean setWindowProperty(InventoryView.Property prop, int value) {
		return false;
	}

	public int getExpToLevel() {
		return Math.round( getHandle().xpBarCap() - getHandle().experience);
	}

}
