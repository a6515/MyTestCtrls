package com.seeyon.apps.myTestCtrl.utils.kit;

import org.apache.tools.ant.util.DateUtils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @date 2018年5月23日上午9:05:01
 * @Copyright Beijing Seeyon Software Co.,LTD
 */
public class StrKit {

    public static String str(Object o) {
        if(o == null) {
            return "";
        }
        if(o instanceof Date) {
            return DateUtils.format((Date) o, "yyyy-MM-dd HH:mm:ss");
        }
        if(o instanceof String) {
            return (String)o;
        }
        return o.toString();
    }
    
    public static float toFloat(Object o) {
        if(o == null) {
            return 0f;
        } else if(o instanceof Float) {
            return (Float)o;
        } else if(o instanceof String) {
            return Float.valueOf((String)o);
        } else if(o instanceof BigDecimal) {
            return ((BigDecimal)o).floatValue();
        }
        return 0f;
    }

    public static Long toLong(Object o) {
        if(null == o) {
            return 0L;
        } else if(o instanceof Long) {
            return (Long)o;
        } else if(o instanceof String) {
            if("".equals(o)) {
                return 0L;
            }
            return Long.valueOf((String)o);
        } else if(o instanceof BigDecimal) {
            return ((BigDecimal)o).longValue();
        }
        return 0L;
    }

    /**
     * 取int值，为空返回0
     * @param obj 对象
     * @return
     */
    public static Integer toInteger(Object obj) {
        if(obj == null) {
            return 0;
        } else if(obj instanceof Long){
        	 return ((Long)obj).intValue();
        } else if(obj instanceof BigDecimal) {
            return ((BigDecimal)obj).intValue();
        }else if(obj instanceof String) {
           String o = (String) obj;
           if("".equals(o)) {
               return 0;
           } else {
               try {
                   return Integer.valueOf((String) obj);
               } catch(Exception e) {
                   return 0;
               }
           }
        } else if(obj instanceof Integer) {
        	return (Integer) obj;
        }
        return 0;
    }
    
    public static List<?> toList(Object o) {
        if(o == null) {
            return null;
        } else if(o instanceof List) {
            return (List<?>) o;
        } else {
            return null;
        }
    }
    
    /**
     * 判断对象是否为空
     * @param o
     * @return
     */
    public static boolean isNull(Object o) {
    	if(null == o) {
    		return true;
    	}
    	if(o instanceof String) {
    		return "".equals((String) o);
    	}
    	if(o instanceof Collection) {
    		// 集合数量为0 则为空
    		return ((Collection<?>) o).size() == 0;
    	}
    	return false;
    }
}
