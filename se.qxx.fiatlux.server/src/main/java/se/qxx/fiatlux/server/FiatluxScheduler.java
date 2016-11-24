package se.qxx.fiatlux.server;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;

import it.sauronsoftware.cron4j.CronParser;
import it.sauronsoftware.cron4j.Predictor;
import it.sauronsoftware.cron4j.Scheduler;
import it.sauronsoftware.cron4j.SchedulingPattern;
import it.sauronsoftware.cron4j.TaskTable;
import se.qxx.fiatlux.server.ExecutorTask.ExecutorType;

public class FiatluxScheduler {

    private static final Logger logger = LogManager.getLogger(FiatluxScheduler.class);

	public Scheduler getScheduler() {
		return scheduler;
	}

	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	private double latitude = 0.0d;
	private double longitude = 0.0d;
	private String timezone;

	private Scheduler scheduler;
	
	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		logger.info(String.format("Setting latitude to %s",  latitude));
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		logger.info(String.format("Setting longitude to %s",  longitude));
		this.longitude = longitude;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		logger.info(String.format("Setting timezone to %s",  timezone));
		this.timezone = timezone;
	}
	
	public FiatluxScheduler() {
		scheduler = new Scheduler();
		scheduler.addSchedulerListener(new ExecutorListener());
				
	}

	public void schedule(String line) {
		logger.debug(String.format("Parsing line :: %s", line));
		
		ExecutorTask t = new ExecutorTask(line, this);
		String cronPattern = t.getCronPattern();
		
		if (StringUtils.isNotEmpty(cronPattern)) {
			String id = scheduler.schedule(cronPattern, t);	
			t.setExecutingId(id);
		}
		
	}



	public void start() {
		scheduler.start();
	}
	
	public void stop() {
		scheduler.stop();
	}

}
