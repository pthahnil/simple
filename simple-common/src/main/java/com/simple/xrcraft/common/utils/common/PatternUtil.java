package com.simple.xrcraft.common.utils.common;

import com.simple.xrcraft.common.models.IdCardAreaModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @description:
 * @author pthahnil
 * @date 2020/4/1 15:22
 */
@Slf4j
public class PatternUtil {

	/**身份证前17位的系数*/
	private static final String[] checkNum = {"7","9","10","5","8","4","2","1","6","3","7","9","10","5","8","4","2"};
	/**身份证最后一位*/
	private static final String[] remNums = {"1","0","X ","9","8","7","6","5","4","3","2"};

	/**身份证长度的匹配*/
	private static Pattern ID_CARD_PATTERN = Pattern.compile("\\d{17}[0-9X]{1}");

	/**手机号长度的匹配*/
	private static Pattern PHONE_NO_PATTERN = Pattern.compile("1{1}\\d{10}");

	/**
	 * 手机号基本正则校验
	 * @param phoneNo
	 * @return
	 */
	public static boolean phoneNoPatternCheck(String phoneNo) {
		if(StringUtils.isBlank(phoneNo)) {
			return false;
		}
		Matcher matcher = PHONE_NO_PATTERN.matcher(phoneNo);
		return matcher.matches();
	}


	/**
	 * 身份证基本正则校验
	 * @param idCardNo
	 * @return
	 */
	public static boolean idCardPatternCheck(String idCardNo){
		if(StringUtils.isBlank(idCardNo)) {
			return false;
		}
		Matcher matcher = ID_CARD_PATTERN.matcher(idCardNo);
		return matcher.matches();
	}

	/**
	 * 最后以为计算
	 * @param idCardNo
	 * @return
	 */
	public static boolean idCardlastNumCheck(String idCardNo) {
		if(!idCardPatternCheck(idCardNo)) {
			return false;
		}
		String actualLastNum = idCardNo.substring(17);
		String[] nums = idCardNo.substring(0, 17).split("");

		Integer add = 0;
		for (int i = 0; i < nums.length; i++) {
			Integer fi = Integer.parseInt(nums[i]);
			Integer si = Integer.parseInt(checkNum[i]);
			add += (fi * si);
		}
		String estematedLastNum = remNums[add % 11];
		return actualLastNum.equals(estematedLastNum);
	}

	/**
	 * 日期计算
	 * @param idCardNo
	 * @return
	 */
	public static boolean idCardDateCheck(String idCardNo) {
		if(!idCardPatternCheck(idCardNo)) {
			return false;
		}
		String dateStr = idCardNo.substring(6, 14);
		try {
			//解析成功即可
			LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyyMMdd"));
			return null != date;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 地区码校验
	 * @param idCardNo
	 * @return
	 */
	public static boolean idCardAreaCodeCheck(String idCardNo) {
		if(!idCardPatternCheck(idCardNo)) {
			return false;
		}
		try {
			String areaCode = idCardNo.substring(0, 6);
			List<String> areaCodes = loadAreaCodes();
			return areaCodes.stream().filter(cod -> cod.equals(areaCode)).findAny().isPresent();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 地区码校验
	 * @param idCardNo
	 * @return
	 */
	public static IdCardAreaModel matchArea(String idCardNo) {
		if(!idCardPatternCheck(idCardNo)) {
			return null;
		}
		try {
			String areaCode = idCardNo.substring(0, 6);
			List<IdCardAreaModel> areaModels = loadAreaModels();
			return areaModels.stream().filter(mod -> mod.getAreaCode().equals(areaCode)).findAny().orElse(null);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 加载地区码
	 * @return
	 * @throws Exception
	 */
	public static List<String> loadAreaCodes() throws Exception{
		ClassPathResource resource = new ClassPathResource("areacode.txt");
		try {
			List<String> lines = FileUtils.readLines(resource.getFile(), "utf8");
			return lines.stream().filter(StringUtils::isNotBlank).map(str -> {
				String[] segs = str.split("=");
				return segs[0];
			}).collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 加载地区码
	 * @return
	 * @throws Exception
	 */
	public static List<IdCardAreaModel> loadAreaModels() throws Exception{
		ClassPathResource resource = new ClassPathResource("areacode.txt");
		try {
			List<String> lines = FileUtils.readLines(resource.getFile(), "utf8");
			return lines.stream().filter(StringUtils::isNotBlank).map(str -> {
				String[] segs = str.split("=");
				return new IdCardAreaModel(segs[0], segs[1]);
			}).collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}

}
