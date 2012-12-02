package keepcalm.mods.bukkit;

import static keepcalm.mods.bukkit.DefferedTaskHandler.*;

public class ThreadDefferedTask implements Runnable {

	private static ThreadDefferedTask instance;
	
	public ThreadDefferedTask() {
		instance = this;
		DefferedTaskHandler.tdtinst = this;
	}
	
	@Override
	public void run() {
		for (Runnable i : tasks) {
				
				int timeLeft =  tasksToRun.get(i);
				
				if (timeLeft <= 0) {
					if (repeatingTasks.get(i) == true) {
						timeLeft = repeatingTasksWait.get(i);
						tasksToRun.put(i, timeLeft);
					}
					else {
						// clean up
						repeatingTasks.remove(i);
						tasks.remove(i);
						tasksToRun.remove(i);
					}
					
					i.run();
				}
				else {
					tasksToRun.put(i, timeLeft - 1);
				}
		}
	}
	
	public static void tick() {
		instance.run();
	}

}
