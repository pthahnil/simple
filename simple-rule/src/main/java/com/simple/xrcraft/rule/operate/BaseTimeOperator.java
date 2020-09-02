package com.simple.xrcraft.rule.operate;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @description:
 * @author pthahnil
 * @date 2019/11/29 9:26
 */
public class BaseTimeOperator {

	private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
	private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
	private static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmmss");

	public static LocalDate getDate(String in) throws Exception {

		in = processInput(in);

		if(in.length() < 8) {
			throw new Exception("input date length too short");
		}

		if(in.length() > 8) {
			in = in.substring(0, 8);
		}

		return LocalDate.parse(in, dateFormatter);
	}

	public static LocalTime getTime(String in) throws Exception {
		in = processInput(in);

		if(in.length() < 4) {
			throw new Exception("input date length too short");
		}
		if(in.length() == 4){
			in = in + "00";
		}

		if(in.length() != 6) {
			throw new Exception("input time error");
		}

		return LocalTime.parse(in, timeFormatter);
	}

	public static LocalDateTime getDateTime(String in) throws Exception {
		in = processInput(in);

		if(in.length() < 8) {
			throw new Exception("input date length too short");
		}

		if(in.length() > 14) {
			in = in.substring(0, in.length() - 14);
		}
		if(in.length() != 14) {
			String append = IntStream.range(0, 14 - in.length()).boxed().map(i -> "0").collect(Collectors.joining());
			in = in + append;
		}

		return LocalDateTime.parse(in, dateTimeFormatter);
	}

	private static String processInput(String in) throws Exception {
		if(StringUtils.isBlank(in)) {
			throw new Exception("input is blank");
		}
		return in.trim().replaceAll("\\D", "");
	}

	public static void main(String[] args) throws Exception {
		String str = "2019-10.12 14";
		System.out.println(getDateTime(str));
	}
}
