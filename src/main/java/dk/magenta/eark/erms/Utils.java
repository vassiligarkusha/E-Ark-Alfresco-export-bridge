package dk.magenta.eark.erms;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

/**
 * @author lanre.
 */
final public class Utils {

    private Utils() {
    }

    private final Logger logger = LoggerFactory.getLogger(Utils.class);
    /**
     * Convert the gregorian calendar date to ISO8601 date format. (Javascript should find it easier to work with this
     * format)
     * @param nasty_date
     * @return
     */
    public static String convertToISO8601Date(GregorianCalendar nasty_date){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        try {
            return df.format(nasty_date.getTime());
        }
        catch (Exception ge){
            ge.printStackTrace();
        }
        return null;
    }


    /**
     * Given a cmis property of the cmis:propertyName, this will return the string value (propertyName) of that value.
     * @param property
     * @return
     */
    public static String getPropertyPostFixValue(String property){
        return StringUtils.substringAfter(property, ":");
    }
}
