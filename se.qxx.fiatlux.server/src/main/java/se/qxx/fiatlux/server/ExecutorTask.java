package se.qxx.fiatlux.server;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;

import it.sauronsoftware.cron4j.Predictor;
import it.sauronsoftware.cron4j.Scheduler;
import it.sauronsoftware.cron4j.SchedulingPattern;
import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskExecutionContext;
import se.qxx.fiatlux.domain.FiatluxComm;
import se.qxx.fiatlux.server.ExecutorTask.ExecutorType;

public class ExecutorTask extends Task{

    private static final Logger logger = LogManager.getLogger(ExecutorTask.class);

    public String updateNextSchedulingTime() {
        String pattern = this.getCronPattern();
        Predictor p = new Predictor(pattern);
        this.setNextSchedulingTime(p.nextMatchingTime());

        return pattern;
    }

    public enum ExecutorType {
		TURN_ON,
		TURN_OFF
	}
	
	private ExecutorType type;
	private ExecutorType getType() {
		return type;
	}
	public FiatluxComm.Action getAction() {
	    if (this.getType() == ExecutorType.TURN_ON)
	        return FiatluxComm.Action.On;
	    else
	        return FiatluxComm.Action.Off;
    }
	
	private void setType(ExecutorType type) {
		this.type = type;
	}
	
	private int deviceId;
	public int getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}
	
	private String executingId;
	public String getExecutingId() {
		return executingId;
	}

	public void setExecutingId(String executingId) {
		this.executingId = executingId;
	}
	
	private String schedulingPattern;
	public String getSchedulingPattern() {
		return schedulingPattern;
	}

	public void setSchedulingPattern(String schedulingPattern) {
		this.schedulingPattern = schedulingPattern;
	}
	
	private FiatluxScheduler fiatluxScheduler;
	public FiatluxScheduler getFiatluxScheduler() {
		return fiatluxScheduler;
	}

	public void setFiatluxScheduler(FiatluxScheduler fiatluxScheduler) {
		this.fiatluxScheduler = fiatluxScheduler;
	}
	
	private long nextSchedulingTime;
	public long getNextSchedulingTime() {
		return nextSchedulingTime;
	}

	public void setNextSchedulingTime(long nextSchedulingTime) {
		this.nextSchedulingTime = nextSchedulingTime;
	}


	public ExecutorTask(String schedulingPattern, FiatluxScheduler fiatluxScheduler) {
		this.setFiatluxScheduler(fiatluxScheduler);
		this.setSchedulingPattern(schedulingPattern);
	}

	/***
	 * Parses a line in and returns the cron pattern adjusted by 
	 * sunrise and sunset if specifed
	 * @return
	 */
	public String getCronPattern() {
		String[] splits = this.getSchedulingPattern().split("\\s+");
		String cronPattern = StringUtils.EMPTY;
		if (splits.length >= 8) {
			String first = splits[0];
	
			cronPattern = constructCronPattern(splits);
			
			// the sunrise senset actually needs the next day it will be
			// run according to cron settings. And it actually needs to 
			// accommodate for this the next time it will run.
			// can be done by the reschedule method if we keep track of the ID's
			// we probably need to implement a listener to handle rescheduling
			if (StringUtils.startsWithIgnoreCase(this.getSchedulingPattern(), "R") || StringUtils.startsWithIgnoreCase(this.getSchedulingPattern(), "S")) {
				cronPattern = getSunriseSunsetCronPattern(splits, first, cronPattern);
			}
			//Parse if we have a turn on or turn off event
			
			//everything else we parse the crontab accordingly
			
			//ExecutorTask t = getExecutorTask(this.getSchedulingPattern(), splits[6], splits[7]);
			setup(splits[6], splits[7]);
			
			Predictor p = new Predictor(cronPattern);
			this.setNextSchedulingTime(p.nextMatchingTime());
			
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("Scheduling task at %s", p.nextMatchingDate().toString()));
			}	
		}
		
		
		return cronPattern;
	}
	
	private String getSunriseSunsetCronPattern(String[] splits, String first, String cronPattern) {
		Date time = getSunriseSunsetDate(first);
		Calendar c = adjustSunriseSunset(splits, time);
		
		logger.debug(String.format("Next expected run time is at %s", c.getTime().toString()));
			
		// now we have the time
		// reconstruct the cron pattern with this info
		splits[1] = Integer.toString(c.get(Calendar.MINUTE));
		splits[2] = Integer.toString(c.get(Calendar.HOUR_OF_DAY));
		cronPattern = constructCronPattern(splits);
		return cronPattern;
	}

	private Calendar adjustSunriseSunset(String[] splits, Date time) {
		String first = splits[0];
		Calendar c = Calendar.getInstance();
		c.setTime(time);
		
		int minutes = 0;
		int hours = 0;
		if (NumberUtils.isNumber(splits[1])) 
			minutes = Integer.parseInt(splits[1]);
		
		if (NumberUtils.isNumber(splits[2]))
			hours = Integer.parseInt(splits[2]);
		
		if (StringUtils.endsWithIgnoreCase(first, "+")) {
			logger.info(String.format("Adjusting by %s hours and %s minutes _later_", hours, minutes));
			c.add(Calendar.MINUTE, minutes);
			c.add(Calendar.HOUR, hours);
		}
		
		if (StringUtils.endsWithIgnoreCase(first, "-")) {
			logger.info(String.format("Adjusting by %s hours and %s minutes _earlier_", hours, minutes));
			c.add(Calendar.MINUTE, -1 * minutes);
			c.add(Calendar.HOUR, -1 * hours);
		}
		return c;
	}

	private Date getSunriseSunsetDate(String first) {
		Date now = Calendar.getInstance().getTime();
		Date tomorrow = DateUtils.addDays(now, 1);
		
		if (StringUtils.startsWithIgnoreCase(first, "R")) {
			Date sunRise = getSunrise(now);
			logger.debug(String.format("Official sunrise at %s", sunRise));
			
			if (sunRise.after(now))
				return getSunrise(tomorrow);
			else
				return sunRise;
			
		}
		else if (StringUtils.startsWithIgnoreCase(first, "S")) {
			Date sunSet = getSunset(now);
			logger.debug(String.format("Official sunset at %s", sunSet));
			
			if (sunSet.before(now))
				return getSunset(tomorrow);
			else
				return sunSet;
		}
		
		return now;
	}

	private String constructCronPattern(String[] splits) {
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
		Location location = new Location(this.getFiatluxScheduler().getLatitude(), this.getFiatluxScheduler().getLongitude());
		return new SunriseSunsetCalculator(location, this.getFiatluxScheduler().getTimezone());
	}

	private void setup(String command, String device) {
		ExecutorType type = ExecutorType.TURN_OFF;
		
		if (StringUtils.equalsIgnoreCase(command, "turnon"))
			type = ExecutorType.TURN_ON;
			
		int deviceId = Integer.parseInt(device);
		
		this.setType(type);
		this.setDeviceId(deviceId);
	}
	
	@Override
	public void execute(TaskExecutionContext context) throws RuntimeException {
		logger.debug("Starting executor task ....");

		try {
			if (this.getType() == ExecutorType.TURN_ON) {
				logger.debug(String.format("Turning on device %s", this.getDeviceId()));
				FiatLuxServer.getNative().tdTurnOn(this.getDeviceId());
			}
			else {
				logger.debug(String.format("Turning off device %s", this.getDeviceId()));
				FiatLuxServer.getNative().tdTurnOff(this.getDeviceId());
			}
		}
		catch (Exception e) {
			logger.error("Error occured while executing command", e);
		}

	}

 	
}
