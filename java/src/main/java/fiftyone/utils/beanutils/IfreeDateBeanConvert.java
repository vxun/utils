package fiftyone.utils.beanutils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.beanutils.Converter;

/**
 * BeanUtils 
 * @author Stephen
 */
public class IfreeDateBeanConvert implements Converter{

	@SuppressWarnings("unchecked")
	public <T> T convert(Class<T> type, Object orig) {
		
		if (orig instanceof Date) {
			Date date = (Date)orig;
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String str = format.format(date);
			try {
				return (T) format.parseObject(str);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//		
//		if (orig instanceof Date) {
//			Date date = (Date) orig;
//			String dateStr = dateFormat.format(date);
//			if (type.isAssignableFrom(String.class)) {
//				return (T) dateStr;
//			}
//			try {
//				date = dateFormat.parse(dateStr);
//			} catch (ParseException e) {
//				e.printStackTrace();
//			}
//			return (T) date;
//		}
//		
//		if (orig instanceof Long) {
//			
//		}
		
		
		return (T) orig;
	}

}
