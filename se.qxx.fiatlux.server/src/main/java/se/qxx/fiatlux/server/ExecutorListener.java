package se.qxx.fiatlux.server;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.sauronsoftware.cron4j.SchedulerListener;
import it.sauronsoftware.cron4j.TaskExecutor;

public class ExecutorListener implements SchedulerListener {
    private static final Logger logger = LogManager.getLogger(ExecutorListener.class);

	@Override
	public void taskFailed(TaskExecutor excecutor, Throwable e) {
		logger.error("Error caught by ExecutorListener", e);
	}

	@Override
	public void taskLaunching(TaskExecutor executor) {
		ExecutorTask task = (ExecutorTask)executor.getTask();
		logger.debug(String.format("Launching task %s", task.getExecutingId()));
	}
	

	@Override
	public void taskSucceeded(TaskExecutor executor) {
		ExecutorTask task = (ExecutorTask)executor.getTask();
		
		String reschedulePattern = task.getCronPattern();
		if (StringUtils.isNotEmpty(reschedulePattern)) {
			executor
				.getScheduler()
				.reschedule(task.getExecutingId(), reschedulePattern);
		}
	}

}
