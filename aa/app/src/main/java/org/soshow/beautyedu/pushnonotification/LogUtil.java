
package org.soshow.beautyedu.pushnonotification;

/** 
 * Utility class for LogCat.
 *
 * @author 
 */
public class LogUtil {
    
    public static String makeLogTag(Class cls) {
        return "Androidpn_" + cls.getSimpleName();
    }

}
