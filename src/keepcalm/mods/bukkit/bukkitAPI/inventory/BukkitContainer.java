package keepcalm.mods.bukkit.bukkitAPI.inventory;

//import org.bukkit.craftbukkit.entity.BukkitPlayer;
import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import keepcalm.mods.bukkit.bukkitAPI.entity.BukkitEntity;
import keepcalm.mods.bukkit.bukkitAPI.entity.BukkitEntityHuman;
import keepcalm.mods.bukkit.bukkitAPI.entity.BukkitPlayer;
import keepcalm.mods.bukkit.forgeHandler.ConnectionHandler;
import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.IInventory;
import net.minecraft.src.Packet100OpenWindow;
import net.minecraft.src.Slot;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

public class BukkitContainer extends Container {
    private final InventoryView view;
    private InventoryType cachedType;
    private String cachedTitle;
    private final int cachedSize;

    public BukkitContainer(InventoryView view, int id) {
        this.view = view;
        this.windowId = id;
        // TODO: Do we need to check that it really is a BukkitInventory?
        IInventory top = ((BukkitInventory)view.getTopInventory()).getInventory();
        IInventory bottom = ((BukkitInventory)view.getBottomInventory()).getInventory();
        cachedType = view.getType();
        cachedTitle = view.getTitle();
        cachedSize = getSize();
        setupSlots(top, bottom);
    }

    public BukkitContainer(final Inventory inventory, final HumanEntity player, int id) {
        this(new InventoryView() {
            @Override
            public Inventory getTopInventory() {
                return inventory;
            }

            @Override
            public Inventory getBottomInventory() {
                return player.getInventory();
            }

            @Override
            public HumanEntity getPlayer() {
                return player;
            }

            @Override
            public InventoryType getType() {
                return inventory.getType();
            }
        }, id);
    }

	public InventoryView getBukkitView() {
        return view;
    }

    private int getSize() {
        return view.getTopInventory().getSize();
    }

    public boolean b(EntityPlayerMP entityhuman) {
        if (cachedType == view.getType() && cachedSize == getSize() && cachedTitle.equals(view.getTitle())) {
            return true;
        }
        // If the window type has changed for some reason, update the player
        // This method will be called every tick or something, so it's
        // as good a place as any to put something like this.
        boolean typeChanged = (cachedType != view.getType());
        cachedType = view.getType();
        cachedTitle = view.getTitle();
        if (view.getPlayer() instanceof BukkitPlayer) {
            BukkitPlayer player = (BukkitPlayer) view.getPlayer();
            int type = getNotchInventoryType(cachedType);
            IInventory top = ((BukkitInventory)view.getTopInventory()).getInventory();
            IInventory bottom = ((BukkitInventory)view.getBottomInventory()).getInventory();
            // FIXME: is this all the slots?
            this.inventorySlots.clear();
            //this.slot.clear();
            if (typeChanged) {
                setupSlots(top, bottom);
            }
            int size = getSize();
            ConnectionHandler.serverHandlers.get(player.getHandle().username).unexpectedPacket(new Packet100OpenWindow(this.windowId, type, cachedTitle, size));
            player.updateInventory();
        }
        return true;
    }

    public static int getNotchInventoryType(InventoryType type) {
        int typeID;
        switch(type) {
        case WORKBENCH:
            typeID = 1;
            break;
        case FURNACE:
            typeID = 2;
            break;
        case DISPENSER:
            typeID = 3;
            break;
        case ENCHANTING:
            typeID = 4;
            break;
        case BREWING:
            typeID = 5;
            break;
        default:
            typeID = 0;
            break;
        }
        return typeID;
    }

    private void setupSlots(IInventory top, IInventory bottom) {
        switch(cachedType) {
        case CREATIVE:
            break; // TODO: This should be an error?
        case PLAYER:
        case CHEST:
            setupChest(top, bottom);
            break;
        case DISPENSER:
            setupDispenser(top, bottom);
            break;
        case FURNACE:
            setupFurnace(top, bottom);
            break;
        case CRAFTING: // TODO: This should be an error?
        case WORKBENCH:
            setupWorkbench(top, bottom);
            break;
        case ENCHANTING:
            setupEnchanting(top, bottom);
            break;
        case BREWING:
            setupBrewing(top, bottom);
            break;
        }
    }

    private void setupChest(IInventory top, IInventory bottom) {
        int rows = top.getSizeInventory() / 9;
        int row;
        int col;
        // This code copied from ContainerChest
        int i = (rows - 4) * 18;
        for (row = 0; row < rows; ++row) {
            for (col = 0; col < 9; ++col) {
                this.addSlotToContainer(new Slot(top, col + row * 9, 8 + col * 18, 18 + row * 18));
            }
        }

        for (row = 0; row < 3; ++row) {
            for (col = 0; col < 9; ++col) {
                this.addSlotToContainer(new Slot(bottom, col + row * 9 + 9, 8 + col * 18, 103 + row * 18 + i));
            }
        }

        for (col = 0; col < 9; ++col) {
            this.addSlotToContainer(new Slot(bottom, col, 8 + col * 18, 161 + i));
        }
        // End copy from ContainerChest
    }

    private void setupWorkbench(IInventory top, IInventory bottom) {
        // This code copied from ContainerWorkbench
        this.addSlotToContainer(new Slot(top, 0, 124, 35));

        int row;
        int col;

        for (row = 0; row < 3; ++row) {
            for (col = 0; col < 3; ++col) {
                this.addSlotToContainer(new Slot(top, 1 + col + row * 3, 30 + col * 18, 17 + row * 18));
            }
        }

        for (row = 0; row < 3; ++row) {
            for (col = 0; col < 9; ++col) {
                this.addSlotToContainer(new Slot(bottom, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        for (col = 0; col < 9; ++col) {
            this.addSlotToContainer(new Slot(bottom, col, 8 + col * 18, 142));
        }
        // End copy from ContainerWorkbench
    }

    private void setupFurnace(IInventory top, IInventory bottom) {
        // This code copied from ContainerFurnace
        this.addSlotToContainer(new Slot(top, 0, 56, 17));
        this.addSlotToContainer(new Slot(top, 1, 56, 53));
        this.addSlotToContainer(new Slot(top, 2, 116, 35));

        int row;
        int col;

        for (row = 0; row < 3; ++row) {
            for (col = 0; col < 9; ++col) {
                this.addSlotToContainer(new Slot(bottom, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        for (col = 0; col < 9; ++col) {
            this.addSlotToContainer(new Slot(bottom, col, 8 + col * 18, 142));
        }
        // End copy from ContainerFurnace
    }

    private void setupDispenser(IInventory top, IInventory bottom) {
        // This code copied from ContainerDispenser
        int row;
        int col;

        for (row = 0; row < 3; ++row) {
            for (col = 0; col < 3; ++col) {
                this.addSlotToContainer(new Slot(top, col + row * 3, 61 + col * 18, 17 + row * 18));
            }
        }

        for (row = 0; row < 3; ++row) {
            for (col = 0; col < 9; ++col) {
                this.addSlotToContainer(new Slot(bottom, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        for (col = 0; col < 9; ++col) {
            this.addSlotToContainer(new Slot(bottom, col, 8 + col * 18, 142));
        }
        // End copy from ContainerDispenser
    }

    private void setupEnchanting(IInventory top, IInventory bottom) {
        // This code copied from ContainerEnchantTable
        this.addSlotToContainer((new Slot(top, 0, 25, 47)));

        int row;

        for (row = 0; row < 3; ++row) {
            for (int i1 = 0; i1 < 9; ++i1) {
                this.addSlotToContainer(new Slot(bottom, i1 + row * 9 + 9, 8 + i1 * 18, 84 + row * 18));
            }
        }

        for (row = 0; row < 9; ++row) {
            this.addSlotToContainer(new Slot(bottom, row, 8 + row * 18, 142));
        }
        // End copy from ContainerEnchantTable
    }

    private void setupBrewing(IInventory top, IInventory bottom) {
        // This code copied from ContainerBrewingStand
        this.addSlotToContainer(new Slot(top, 0, 56, 46));
        this.addSlotToContainer(new Slot(top, 1, 79, 53));
        this.addSlotToContainer(new Slot(top, 2, 102, 46));
        this.addSlotToContainer(new Slot(top, 3, 79, 17));

        int i;

        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(bottom, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (i = 0; i < 9; ++i) {
            this.addSlotToContainer(new Slot(bottom, i, 8 + i * 18, 142));
        }
        // End copy from ContainerBrewingStand
    }

    public boolean c(EntityPlayerMP entity) {
        return true;
    }

	@Override
	public boolean canInteractWith(EntityPlayer var1) {
		return true;
	}
}
