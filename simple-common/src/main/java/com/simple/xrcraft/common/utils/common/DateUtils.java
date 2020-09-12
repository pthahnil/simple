package com.simple.xrcraft.common.utils.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * 
 * The class DateUtils.
 *
 * Description:
 *
 * @author: lixiaorong
 * @since: 2017年1月20日
 * @version: $Revision$ $Date$ $LastChangedBy$
 *
 */
@Slf4j
public class DateUtils {

	private static ZoneId sysZone = ZoneId.systemDefault();
	private static ZoneOffset zoneOffset = OffsetDateTime.now().getOffset();

	/**
	 * 接下来n天
	 * @param original
	 * @param gap
	 * @return
	 */
	public static Date getNextNDay(Date original, int gap){
		return getNext(original, gap, ChronoUnit.DAYS);
	}

	/**
	 * 日期加减
	 * @param original
	 * @param gap
	 * @param unit
	 * @return
	 */
	public static Date getNext(Date original, int gap, ChronoUnit unit){
		Instant instant = original.toInstant();
		LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, sysZone);
		LocalDateTime newTime = localDateTime.plus(gap, unit);
		Instant transfered = newTime.toInstant(zoneOffset);
		return Date.from(transfered);
	}

	/**
	 * date->string
	 * @param original
	 * @param formatter
	 * @return
	 */
	public static String date2String(Date original, String formatter){
		Instant instant = original.toInstant();
		LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, sysZone);

		return localDateTime.format(DateTimeFormatter.ofPattern(formatter));
	}

	/**
	 * string->date
	 * @param dateString
	 * @param formatter
	 * @return
	 */
	public static Date string2Date(String dateString, String formatter) throws Exception {
		LocalDateTime localDateTime = null;
		dateString = organizeDateString(dateString);
		formatter = organizePatternString(formatter);

		if(formatter.length() > dateString.length()){
			throw new Exception("format exception");
		} else {
			dateString = dateString.substring(0, formatter.length());
		}
		if(dateString.length() > 8){
			localDateTime = LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern(formatter));
		} else {
			LocalDate localDate = LocalDate.parse(dateString, DateTimeFormatter.ofPattern(formatter));
			LocalTime localTime = LocalTime.MIN;

			localDateTime = LocalDateTime.of(localDate, localTime);
		}
		Instant transfered = localDateTime.toInstant(zoneOffset);
		return Date.from(transfered);
	}

	/**
	 * 日期间的天数
	 * @param from
	 * @param to
	 * @return
	 */
	public static Long daysBetween(Date from, Date to){
		return between(from, to, ChronoUnit.DAYS);
	}

	/**
	 * 日期间的天数
	 * @param from
	 * @param to
	 * @return
	 */
	public static Long hoursBetween(Date from, Date to){
		return between(from, to, ChronoUnit.HOURS);
	}

	private static Long between(Date from, Date to, ChronoUnit unit) {
		if(null == from){
			return null;
		}
		if(null == to){
			return null;
		}
		if(null == unit){
			return null;
		}
		Instant insFrom = from.toInstant();
		Instant insTo = to.toInstant();

		LocalDateTime ldtFrom = LocalDateTime.ofInstant(insFrom, sysZone);
		LocalDateTime ldtTo = LocalDateTime.ofInstant(insTo, sysZone);

		return ldtFrom.until(ldtTo, unit);
	}

	/**
	 * 距今天多久
	 * @param from
	 * @return
	 */
	public static Long daysUntilNow(Date from){
		Date to = new Date();
		return daysBetween(from, to);
	}

	/**
	 * 凌晨零点
	 * @param date
	 * @return
	 */
	public static Date getStartOfDate(Date date){
		return getCertainTimeOfDay(date, LocalTime.MIN);
	}

	/**
	 * 获取当天最大时间点
	 * 数据库回转成第二天0点0分
	 * @param date
	 * @return
	 */
	public static Date getEndOfDate(Date date){
		return getCertainTimeOfDay(date, LocalTime.MAX);
	}

	public static Date getEndOfDateV2(Date date){
		LocalTime end = LocalTime.parse("23:59:59");
		return getCertainTimeOfDay(date, end);
	}

	/**
	 * 更改日期的时间
	 * @param date
	 * @param localTime
	 * @return
	 */
	private static Date getCertainTimeOfDay(Date date, LocalTime localTime){
		Instant instant = date.toInstant();
		LocalDateTime dateInstant = LocalDateTime.ofInstant(instant, sysZone);

		LocalDateTime dest = LocalDateTime.of(dateInstant.toLocalDate(), localTime);
		Instant transfered = dest.toInstant(zoneOffset);
		return Date.from(transfered);
	}

	/**
	 * 日期格式处理
	 * @param date
	 * @return
	 */
	public static String organizeDateString(String date){
		if(StringUtils.isBlank(date)){
			return null;
		}
		date = date.replaceAll("\\D", "");

		return StringUtils.trimToEmpty(date);
	}

	/**
	 * 日期格式处理
	 * @param pattern
	 * @return
	 */
	public static String organizePatternString(String pattern){
		if(StringUtils.isBlank(pattern)){
			return null;
		}
		pattern = pattern.replace("-", "");
		pattern = pattern.replace("/", "");
		pattern = pattern.replace("_", "");
		pattern = pattern.replace(" ", "");
		pattern = pattern.replace(":", "");

		return StringUtils.trimToEmpty(pattern);
	}

	/**
	 * 年龄计算
	 * @param birthDay
	 * @param textFormat
	 * @return
	 */
	public static int calculateAge(String birthDay, String textFormat){

		if(StringUtils.isBlank(birthDay) || StringUtils.isBlank(textFormat)){
			log.error("请输入正确出生年月和时间格式，birthday:{}, format:{}", birthDay, textFormat);
			return 0;
		}

		LocalDate birth = LocalDate.parse(birthDay, DateTimeFormatter.ofPattern(textFormat));
		Long age = birth.until(LocalDate.now(), ChronoUnit.MONTHS)/12;

		LocalDate thisYear = birth.withYear(LocalDate.now().getYear());
		if(thisYear.isBefore(LocalDate.now())){
			age = age + 1;
		}

		return age.intValue();
	}

	/**
	 * date -> localDateTime
	 * @param date
	 * @return
	 */
	public static LocalDateTime dt2Lc(Date date) {
		if(null == date){
			return null;
		}
		Instant instant = date.toInstant();
		return LocalDateTime.ofInstant(instant, sysZone);
	}

	/**
	 * localDateTime -> date
	 * @param time
	 * @return
	 */
	public static Date lc2Dt(LocalDateTime time) {
		if(null == time){
			return null;
		}
		Instant instant = time.toInstant(zoneOffset);
		return Date.from(instant);
	}


}

	