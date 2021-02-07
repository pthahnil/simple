package com.simple.xrcraft.common.utils.common;

import com.simple.xrcraft.common.constants.Gender;

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

	public static String maleIdCard() throws Exception {
		return genIdCard(Gender.MALE);
	}

	public static String femaleIdCard() throws Exception {
		return genIdCard(Gender.FEMALE);
	}

	public static String genIdCard(Gender gender) throws Exception {
		//生成格式正确的身份证号
		List<String> lines = PatternUtil.loadAreaCodes();
		return genIdCard(lines, gender);
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
	private static String genIdCard(List<String> lines, Gender gender) {
		int size = lines.size();
		Random random = new Random();
		int index = random.nextInt(size);

		String areaCode = lines.get(index);

		LocalDate date = genBirthday();

		return genIdCard(areaCode, date, gender);
	}

	/**
	 * 生产身份证号
	 * @param areaCode
	 * @param date
	 * @param gender
	 * @return
	 */
	private static String genIdCard(String areaCode, LocalDate date, Gender gender) {
		Random random = new Random();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		String idCardPrefix = areaCode + formatter.format(date);

		int index = random.nextInt(10);
		String num4 = numbs[index];

		index = random.nextInt(10);
		String num3 = numbs[index];

		String num2 = null;
		index = random.nextInt(10);
		boolean evenNum = index % 2 == 0;
		switch (gender){
			case MALE:
				//偶数
				num2 = evenNum && index >= 0 ? numbs[index] : numbs[index-1];
				break;
			case FEMALE:
				//基数
				num2 = !evenNum ? numbs[index] : (index > 0 ? numbs[index-1] : numbs[1]);
				break;
			default:
				num2 = numbs[index];
				break;
		}
		idCardPrefix = idCardPrefix + num4 + num3 + num2;

		String[] nums = idCardPrefix.split("");
		Integer add = 0;
		for (int i = 0; i < nums.length; i++) {
			Integer fi = Integer.parseInt(nums[i]);
			Integer si = Integer.parseInt(PatternUtil.checkNum[i]);
			add += (fi * si);
		}

		int remainIndex = add % 11;
		if(remainIndex > 10){
			return genIdCard(areaCode, date, gender);
		} else {
			idCardPrefix += PatternUtil.remNums[remainIndex];
			return idCardPrefix;
		}
	}

}
