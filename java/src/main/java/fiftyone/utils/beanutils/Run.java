package fiftyone.utils.beanutils;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import org.apache.commons.beanutils.BeanUtils;

public class Run {

	
	public static void main(String[] args) {
		Orign orign = new Orign();
		orign.setDate(new Date());
		orign.setName("test");
		
//		IfreeDateBeanConvert convert = new IfreeDateBeanConvert();
//		ConvertUtils.register(convert, Date.class);
		
		Orign n = new Orign();
		try {
			BeanUtils.copyProperties(n, orign);
			
			System.out.println(n);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
}


