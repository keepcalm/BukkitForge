package keepcalm.mods.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public class BukkitEventRouter<T extends Event> {
    public interface BukkitEventPrep<T> {
        public void prepareEvent(T event);
    }

    protected <T extends Event> T callEvent( T event, boolean initiallyCancelled, BukkitEventPrep<T> prep )
    {
        if( event instanceof Cancellable)
        {
            ((Cancellable)event).setCancelled(initiallyCancelled);
        }

        if( prep != null )
        {
            prep.prepareEvent(event);
        }

        Bukkit.getPluginManager().callEvent(event);
        return event;
    }
}
