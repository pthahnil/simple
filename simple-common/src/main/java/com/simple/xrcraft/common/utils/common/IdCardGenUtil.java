package com.simple.xrcraft.common.utils.common;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;

/**
 * @description:
 * @author pthahnil
 * @date 2020/12/2 16:23
 */
public class IdCardGenUtil {

	public static final String[] numbs = {"0","1","2 ","3","4","5","6","7","8","9"};

	public static String genIdCard() throws Exception {
		//生成格式正确的身份证号
		boolean genSucc = false;

		List<String> lines = PatternUtil.loadAreaCodes();
		String idCard = null;
		while (!genSucc){
			idCard = genIdCard(lines);
			genSucc = StringUtils.isNotBlank(idCard);
		}
		return idCard;
	}

	/**
	 * 1960年后随机年月日
	 * @return
	 */
	public static LocalDate genBirthday(){
		Year begin = Year.parse("1960");
		return genBirthday(begin);
	}

	/**
	 * 随机年月日
	 * @return
	 */
	public static LocalDate genBirthday(Year begin){
		Year year = Year.now();
		Long years = begin.until(year, ChronoUnit.YEARS);

		Random random = new Random();
		int yearGap = random.nextInt(years.intValue());

		//1960年后随机年份
		Year targetYear = begin.plusYears(yearGap);

		//随机月份
		int monthInt = 1 + random.nextInt(12);
		Month targetMonth = Month.of(monthInt);

		YearMonth yearMonth = YearMonth.of(targetYear.getValue(), targetMonth);

		int monthLength = yearMonth.lengthOfMonth();
		//随机日期
		int dayOfMonth = 1 + random.nextInt(monthLength);

		LocalDate localDate = LocalDate.of(targetYear.getValue(), targetMonth.getValue(), dayOfMonth);
		return localDate;
	}

	/**
	 * 生产身份证号
	 * @param lines
	 * @return
	 * @throws Exception
	 */
	private static String genIdCard(List<String> lines) {
		int size = lines.size();
		Random random = new Random();
		int index = random.nextInt(size);

		String areaCode = lines.get(index);

		LocalDate date = genBirthday();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		String idCardPrefix = areaCode + formatter.format(date);

		index = random.nextInt(10);
		String num4 = numbs[index];

		index = random.nextInt(10);
		String num3 = numbs[index];

		index = random.nextInt(10);
		String num2 = numbs[index];
		idCardPrefix = idCardPrefix + num4 + num3 + num2;

		for (int i = 0; i < PatternUtil.remNums.length; i++) {
			String idCardNo = idCardPrefix + PatternUtil.remNums[i];
			if(PatternUtil.idCardlastNumCheck(idCardNo)){
				return idCardNo;
			}
		}
		return null;
	}
}
