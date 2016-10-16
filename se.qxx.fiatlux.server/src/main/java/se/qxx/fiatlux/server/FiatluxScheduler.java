package se.qxx.fiatlux.server;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
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
		
		String[] splits = line.split("\\s+");
		if (splits.length >= 8) {
			String first = splits[0];
	
			String cronPattern = getCronPattern(splits);
			
			// the sunrise senset actually needs the next day it will be
			// run according to cron settings. And it actually needs to 
			// accommodate for this the next time it will run.
			// can be done by the reschedule method if we keep track of the ID's
			// we probably need to implement a listener to handle rescheduling
			if (StringUtils.startsWithIgnoreCase(line, "R") || StringUtils.startsWithIgnoreCase(line, "S")) {
				Date d = getNextExecutingDate(cronPattern);
				logger.debug(String.format("Next expected run time is at %s", d.toString()));
				
				Date time = getSunriseSunsetDate(first, d);
				Calendar c = Calendar.getInstance();
				c.setTime(time);
				
				// now we have the time
				// reconstruct the cron pattern with this info
				splits[1] = Integer.toString(c.get(Calendar.MINUTE));
				splits[2] = Integer.toString(c.get(Calendar.HOUR));
				cronPattern = getCronPattern(splits);
			}
			
			//Parse if we have a turn on or turn off event
			
			//everything else we parse the crontab accordingly
			
			ExecutorTask t = getExecutorTask(splits[6], splits[7]);
			scheduler.schedule(cronPattern, t);
		}
	}

	private ExecutorTask getExecutorTask(String command, String device) {
		ExecutorType type = ExecutorType.TURN_OFF;
		
		if (StringUtils.equalsIgnoreCase(command, "turnon"))
			type = ExecutorType.TURN_ON;
			
		int deviceId = Integer.parseInt(device);
		return new ExecutorTask(type, deviceId);
		
	}

	private Date getSunriseSunsetDate(String first, Date d) {
		Date sunriseSunset = Calendar.getInstance().getTime();
	
		if (StringUtils.equalsIgnoreCase(first, "R")) {
			sunriseSunset = getSunrise(d);
			logger.debug(String.format("Official sunrise at %s", sunriseSunset));
			
		}
		else if (StringUtils.equalsIgnoreCase(first, "S")) {
			sunriseSunset = getSunset(d);
			logger.debug(String.format("Official sunset at %s", sunriseSunset));
		}
		
		return sunriseSunset;
	}

	private String getCronPattern(String[] splits) {
		StringBuilder sb = new StringBuilder();
		sb.append(splits[1]);
		sb.append(" ");
		sb.append(splits[2]);
		sb.append(" ");
		sb.append(splits[3]);
		sb.append(" ");
		sb.append(splits[4]);
		sb.append(" ");
		sb.append(splits[5]);
		
		return sb.toString();
	}

	private Date getNextExecutingDate(String line) {
		Predictor predictor = new Predictor(line);
		return predictor.nextMatchingDate();
	}

	private Date getSunrise(Date date) {
		//this should occur at sunrise
		SunriseSunsetCalculator calculator = getCalculator();
		Calendar c = getCalendarForDate(date);
		
		return calculator.getOfficialSunriseCalendarForDate(c).getTime();
	}
	
	private Date getSunset(Date date) {
		//this should occur at sunrise
		SunriseSunsetCalculator calculator = getCalculator();
		Calendar c = getCalendarForDate(date);

		return calculator.getOfficialSunsetCalendarForDate(c).getTime();
	}
	
	private Calendar getCalendarForDate(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		
		return c;
	}

	private SunriseSunsetCalculator getCalculator() {
		Location location = new Location(this.getLatitude(), this.getLongitude());
		return new SunriseSunsetCalculator(location, timezone);
	}


}
