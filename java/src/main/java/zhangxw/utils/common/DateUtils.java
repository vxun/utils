package zhangxw.utils.common;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Title: DateUtils
 * @Description: DateUtils
 * @author zhangxw
 * @date 2017年7月4日 下午3:05:25
 */
public class DateUtils {

	
	/**
	 * @Title: date2MilliStr
	 * @Description: 时间转换为毫秒级字符串.eg: 2017-09-09 12:09:32:00
	 * @author zhangxw
	 * @date 2017年7月4日 下午3:06:53
	 *
	 * @return
	 */
	public static String date2MilliStr(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS");
		return sdf.format(date);
	}
	
	
	public static void main(String[] args) {
		String s = DateUtils.date2MilliStr(new Date());
		System.out.println(s);
	}
	
}
