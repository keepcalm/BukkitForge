package keepcalm.mods.bukkit.forgeHandler;

import net.minecraft.command.ICommandManager;
import net.minecraft.command.ICommandSender;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: jtdossett
 * Date: 2/14/13
 * Time: 9:42 AM
 * To change this template use File | Settings | File Templates.
 */
public class CommandManagerWrapper implements ICommandManager {

    static protected CommandManagerWrapper wrapper;

    static public CommandManagerWrapper getInstance()
    {
       return wrapper;
    }

    static public void createInstance( ICommandManager mgrToWrap )
    {
       wrapper = new CommandManagerWrapper(mgrToWrap);
    }

    protected ICommandManager wrappedMgr;

    protected CommandManagerWrapper( ICommandManager mgrToWrap )
    {
        wrappedMgr = mgrToWrap;
    }

    public void executeCommand(ICommandSender paramICommandSender, String paramString)
    {
        System.out.println( "!!! Overriding execute command woot woto!" );
        wrappedMgr.executeCommand(paramICommandSender, paramString);
    }

    public List getPossibleCommands(ICommandSender paramICommandSender, String paramString)
    {
        return wrappedMgr.getPossibleCommands(paramICommandSender, paramString);
    }

    public List getPossibleCommands(ICommandSender paramICommandSender)
    {
        return wrappedMgr.getPossibleCommands(paramICommandSender);
    }

    public Map getCommands()
    {
        return wrappedMgr.getCommands();
    }
}
