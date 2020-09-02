package com.simple.xrcraft.common.constants;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by pthahnil on 2017/10/28.
 */
public class TrainConstants {

	/**
	 * 八个验证图片
	 */
	public enum PostionMapping {
		A11("1",  "50,50"),
		A12("2",  "100,50"),
		A13("3",  "175,50"),
		A14("4",  "250,50"),

		A21("5",  "50,100"),
		A22("6",  "100,100"),
		A23("7",  "175,100"),
		A24("8",  "250,100");

		/**
		 * 索引
		 */
		private String index;

		/**
		 * 坐标
		 */
		private String coods;

		PostionMapping(String index, String coods) {
			this.index = index;
			this.coods = coods;
		}

		/**
		 * 获取坐标
		 * @param index
		 * @return
		 */
		public static String getCoodByIndex(String index){
			index = StringUtils.trimToEmpty(index);
			if(StringUtils.isBlank(index)){
				return null;
			}

			PostionMapping[] matrix = PostionMapping.values();
			for (PostionMapping postionMapping : matrix) {
				if(postionMapping.index.equals(index)){
					return postionMapping.coods;
				}
			}

			return null;
		}

		public String getCoods() {
			return coods;
		}
	}

	/**
	 * 12306 各个接口
	 */
	public static class Urls{
		//获取验证码
		public static String LOGIN_IMAGE = "https://kyfw.12306.cn/passport/captcha/captcha-image?login_site=E&module=login&rand=sjrand";
		//验证码校验
		public static String LOGIN_IMAGE_CHECK = "https://kyfw.12306.cn/passport/captcha/captcha-check";
		//登陆
		public static String LOGIN_URL = "https://kyfw.12306.cn/passport/web/login";
		//查票
		public static String TICKET_QUERY = "https://kyfw.12306.cn/otn/leftTicket/query?leftTicketDTO.train_date=2017-10-28&leftTicketDTO.from_station=SZQ&leftTicketDTO.to_station=YTG&purpose_codes=ADULT";
		//待支付
		public static String NOT_PAIED_TICKETS = "https://kyfw.12306.cn/otn/queryOrder/queryMyOrderNoComplete";
		//取消订单
		public static String CANCEL_ORDER = "https://kyfw.12306.cn/otn/queryOrder/cancelNoCompleteMyOrder";

		public static String PASSENGER_LIST = "https://kyfw.12306.cn/otn/confirmPassenger/getPassengerDTOs";

		//订单数据放入列队
		public static String CREATE_ORDER = "https://kyfw.12306.cn/otn/confirmPassenger/confirmSingleForQueue";

		public static String ORDER_STATUS = "https://kyfw.12306.cn/otn/confirmPassenger/queryOrderWaitTime?random=1509183205240&tourFlag=dc&_json_att=&REPEAT_SUBMIT_TOKEN=0236e9c522d0b8e9b98c10016be247d7";

	}

}
