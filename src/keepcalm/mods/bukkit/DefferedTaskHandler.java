package keepcalm.mods.bukkit;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;


/**
 * Used for procrastination of events until after API load
 * @author keepcalm
 *
 */
public class DefferedTaskHandler implements ITickHandler {

	public static List<Runnable> tasks = new ArrayList<Runnable>();
	
	public static HashMap<Runnable, Integer> tasksToRun = new HashMap<Runnable, Integer>();
	
	public static HashMap<Runnable, Integer> repeatingTasksWait = new HashMap<Runnable, Integer>();
	
	public static HashMap<Runnable,Boolean> repeatingTasks = new HashMap<Runnable, Boolean>();
	private Thread runnerThread;
	
	public static ThreadDefferedTask tdtinst;
	
	public static int ticks = 0;

	public DefferedTaskHandler() {
		runnerThread = new Thread(new ThreadDefferedTask(), "B4VDefferedTaskRunner");
		runnerThread.setDaemon(true);
		runnerThread.start();
	}
	
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		ticks++;
		tdtinst.run();
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public EnumSet<TickType> ticks() {
		EnumSet<TickType> t = (EnumSet.allOf(TickType.class));
		t.remove(TickType.SERVER);
		return EnumSet.complementOf(t);
	}

	@Override
	public String getLabel() {
		return "B4VDefferedActionHandler";
	}
	
	public static void registerTask(Runnable run, boolean isRepeated, int schedule) {
		tasks.add(run);
		repeatingTasks.put(run, isRepeated);
		tasksToRun.put(run,schedule);
		if (isRepeated) {
			repeatingTasksWait.put(run, schedule);
		}
	}
	
	

}
