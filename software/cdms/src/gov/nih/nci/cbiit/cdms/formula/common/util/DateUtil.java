/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cdms.formula.common.util;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Calendar;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Nov 22, 2010
 * Time: 10:50:06 AM
 * To change this template use File | Settings | File Templates.
 */
public class DateUtil
{
    SimpleDateFormat sourceDateFormat;
    SimpleDateFormat targetDateFormat;
    SimpleDateFormat defaultDateFormat;
    String defaultDateFormatString;
    Calendar fromCal;
    Calendar toCal;
    java.util.Date sourceDate;
    java.util.Date targetDate;
    java.util.Date nowDate;

    public DateUtil()
    {
        sourceDateFormat = null;
        targetDateFormat = null;
        sourceDate = null;
        targetDate = null;
        nowDate = new java.util.Date();
        defaultDateFormatString = "yyyyMMddHHmmss";
        defaultDateFormat = new SimpleDateFormat(defaultDateFormatString);
        fromCal = Calendar.getInstance();
        toCal = Calendar.getInstance();
    }

    public SimpleDateFormat checkSimpleDateFormat(String sdfStr) throws Exception
    {
       SimpleDateFormat sdf;
       try
        {
           sdf = new SimpleDateFormat(sdfStr);
        }
        catch(IllegalArgumentException ie)
        {
            throw new Exception("Illegal Date Format [" + sdfStr + "] (refer to the specification of Java's SimpleDateFormat class)");
        }
        catch(NullPointerException ie)
        {
            throw new Exception("The given date Format String is null value : ");
        }
        return sdf;
    }

    public java.util.Date parseDateFromString(SimpleDateFormat sdf, String dateStr) throws Exception
    {
        java.util.Date dt = null;
        try
        {
            dt = sdf.parse(dateStr);
        }
        catch(ParseException pe)
        {
            throw new Exception("Invalid date Format in date String : " + dateStr);
        }
        return dt;
    }
    public String getCurrentTime(String timeFormat) throws Exception
    {
        nowDate = new java.util.Date();
        SimpleDateFormat dateFormat = checkSimpleDateFormat(timeFormat);
        return dateFormat.format(nowDate);
    }
    public String getCurrentTime()
    {
        nowDate = new java.util.Date();
        return defaultDateFormat.format(nowDate);
    }
    public String getDefaultDateFormatString()
    {
        return defaultDateFormatString;
    }
    //public String changeDefaultFormat(String inDate, String fromFormat) throws Exception
    //{
    //   return changeFormat(inDate, fromFormat, defaultDateFormatString);
    //}

    public String getYearsBetweenDates(String fromDate, String fromFormat, String toDate, String toFormat) throws Exception
    {
       setCalendarInstances(fromDate, fromFormat, toDate, toFormat);
       return Integer.toString(toCal.get(Calendar.YEAR) - fromCal.get(Calendar.YEAR));
    }
    public String getYearsBetweenDatesInDefaultFormat(String fromDate, String toDate) throws Exception
    {
       return getYearsBetweenDates(fromDate, defaultDateFormatString, toDate, defaultDateFormatString);
    }

    public String getMonthsBetweenDates(String fromDate, String fromFormat, String toDate, String toFormat) throws Exception
    {
       setCalendarInstances(fromDate, fromFormat, toDate, toFormat);
       int years = toCal.get(Calendar.YEAR) - fromCal.get(Calendar.YEAR);
       int months = (years * 12) + toCal.get(Calendar.MONTH) + (12 - fromCal.get(Calendar.YEAR));
       return Integer.toString(months);
    }
    public String getMonthsBetweenDatesInDefaultFormat(String fromDate, String toDate) throws Exception
    {
       return getMonthsBetweenDates(fromDate, defaultDateFormatString, toDate, defaultDateFormatString);
    }

    public String getDaysBetweenDates(String fromDate, String fromFormat, String toDate, String toFormat) throws Exception
    {
       Long millis = getMillisBetweenDates(fromDate, fromFormat, toDate, toFormat);
       int days = (int) (millis / (1000*60*60*24));
       int daySecondsOfFrom = (fromCal.get(Calendar.HOUR)*3600) + (fromCal.get(Calendar.MINUTE)*60) + (fromCal.get(Calendar.SECOND));
       int daySecondsOfTo = (toCal.get(Calendar.HOUR)*3600) + (toCal.get(Calendar.MINUTE)*60) + (toCal.get(Calendar.SECOND));
       if (daySecondsOfTo < daySecondsOfFrom) days++;
       return Integer.toString(days);
    }
    public String setMatchedDateFormat(String format, String date)
    {
        date = date.trim();
        format = format.trim();
        if (format.length() == date.length()) return date;
        else if (format.length() < date.length()) return date.substring(0, format.length());

        String str = "";
        for (int i=0;i<(format.length() - date.length());i++) str = str + "0";
        return date + str;
    }
    public int countDays(String fromDate, String toDate) throws Exception
    {
        if ((fromDate==null)||(fromDate.trim().equals(""))) throw new Exception("Null value of fromDate");
        if ((toDate==null)||(toDate.trim().equals(""))) throw new Exception("Null value of toDate");
        fromDate = setMatchedDateFormat(defaultDateFormatString, fromDate);
        toDate = setMatchedDateFormat(defaultDateFormatString, toDate);
        String str = getDaysBetweenDates(fromDate, defaultDateFormatString, toDate, defaultDateFormatString);
        return Integer.parseInt(str);
    }
    public Long getMillisBetweenDates(String fromDate, String fromFormat, String toDate, String toFormat) throws Exception
    {
       setCalendarInstances(fromDate, fromFormat, toDate, toFormat);

       Long millis = toCal.getTimeInMillis() - fromCal.getTimeInMillis();
       return millis;
    }

    public void setCalendarInstances(String fromDate, String fromFormat, String toDate, String toFormat) throws Exception
    {
       SimpleDateFormat objFromFormat = checkSimpleDateFormat(fromFormat);
       SimpleDateFormat objToFormat = checkSimpleDateFormat(toFormat);
       java.util.Date objFromDate = parseDateFromString(objFromFormat, fromDate);
       java.util.Date objToDate = parseDateFromString(objToFormat, toDate);
       fromCal.setTime(objFromDate);
       toCal.setTime(objToDate);
    }
    public String changeAnotherFormat(String fromFormat, String toFormat, String inDate) throws Exception
    {
       SimpleDateFormat objFromFormat = checkSimpleDateFormat(fromFormat);
       SimpleDateFormat objToFormat = checkSimpleDateFormat(toFormat);
       java.util.Date objDate = parseDateFromString(objFromFormat, inDate);
       return objToFormat.format(objDate);
    }
    private String arrangeMonthFormat(String fromFormat)
    {
       String filteredFormat = filteringFormat(fromFormat);
       /*
       int n = 0;
       n = filteredFormat.indexOf("mm");
       if (n < 0) return fromFormat;
       if ((filteredFormat.indexOf("MM")) >= 0) return fromFormat;
       if (((filteredFormat.toUpperCase()).indexOf("Y")) < 0) return fromFormat;
       boolean threeM = false;
       if ((filteredFormat.substring(n)).startsWith("mmm")) threeM = true;
       if (((filteredFormat.toUpperCase()).indexOf("H")) < 0)
       {
           if (threeM) return fromFormat.replace("mmm", "MMM");
           else return fromFormat.replace("mm", "MM");
       }
       */
       int n = 0;
       int m = 0;
       String t = "";
       String temp = "";
       while(true)
       {
           temp = "";
           if (n == 0) temp = "yyyyMMdd";
           else if (n == 1) temp = "yyMMdd";
           else if (n == 2) temp = "yyddMM";
           else if (n == 3) temp = "MMddyyyy";
           else if (n == 4) temp = "MMddyy";
           else if (n == 5) temp = "yyyy?MMM?dd";
           else if (n == 6) temp = "MMM?dd?yyyy";
           else if (n == 7) temp = "yy?MMM?dd";
           else if (n == 8) temp = "MMM?dd?yy";
           else if (n == 9) temp = "yyyy?MM?dd";
           else if (n == 10) temp = "MM?dd?yyyy";
           else if (n == 11) temp = "yy?MM?dd";
           else if (n == 12) temp = "MM?dd?yy";
           else break;
           n++;
           m = 0;
           while(true)
           {
               if (m == 0) t = "/";
               else if (m == 1) t = ".";
               else if (m == 2) t = "-";
               else if (m == 3) t = "|";
               else if (m == 4) t = "\\";
               else if (m == 5) t = "_";
               else break;
               m++;
               String temp2 = temp.replace("?", t);

               if (fromFormat.indexOf(temp2) >= 0) break;
               int j = (fromFormat.toUpperCase()).indexOf(temp2.toUpperCase());
               //System.out.println("ZZZZZZZZZZ : " + temp2 + " : "+ fromFormat + " : "+fromFormat.toUpperCase()+" : "+ j);
               if (j >= 0)
               {
                  fromFormat = fromFormat.substring(0,j) + temp2 + fromFormat.substring(j+temp2.length());
                  break;
               }
           }
       }
       n = 0;
       m = 0;
       if ((filteredFormat.toUpperCase()).indexOf("H") < 0) return fromFormat;
       while(true)
       {
           temp = "";
           if (n == 0) temp = "mmss";
           else if (n == 1) temp = "mm?ss";
           else if (n == 2) temp = "ssmm";
           else if (n == 3) temp = "ss?mm";
           else break;
           n++;
           m = 0;
           while(true)
           {
               if (m == 0) t = ":";
               else if (m == 1) t = ".";
               else break;
               m++;
               String temp2 = temp.replace("?", t);

               if (fromFormat.indexOf(temp2) >= 0) return fromFormat;
               int j = (fromFormat.toUpperCase()).indexOf(temp2.toUpperCase());
               //System.out.println("QQQQQQQQQQQ : " + temp2 + " : "+ fromFormat + " : "+fromFormat.toUpperCase()+" : "+ j);
               if (j >= 0)
                  return fromFormat.substring(0,j) + temp2 + fromFormat.substring(j+temp2.length());
           }
       }
       return fromFormat;
    }

    private String filteringFormat(String fromFormat)
    {
       boolean quotMark = false;
       String filteredFormat = "";
       String achar = "";
       for (int i=0;i<fromFormat.length();i++)
       {
           achar = fromFormat.substring(i, i+1);
           if (achar.equals("'"))
           {
               if (quotMark) quotMark = false;
               else quotMark = true;
               continue;
           }
           if (!quotMark) filteredFormat =  filteredFormat + achar;
       }
       return filteredFormat;
    }
    public String changeFormat(String fromFormat, String inDate) throws Exception
    {
        //System.out.println("YYYYYYYYYYYYYYYYYYYYYYYYYYY : Date");
        //if(fromFormat.equals("DD-MON-YYYY")) fromFormat = "dd-MMM-yyyy";
        //if (inDate.equals("")) throw new Exception("Year, Month or day data is needed(BBBBB). : " + inDate, 511, new Throwable(), ApplicationException.SEVERITY_LEVEL_WARNING);
        String result = "";
        if (inDate == null) return "";
        inDate = inDate.trim();
        if (inDate.equals("")) return "";
        String filteredFormat = filteringFormat(fromFormat);
        String arrangedFormat = arrangeMonthFormat(fromFormat);
        //System.out.println("VVVVVV : " + arrangedFormat + " : " + inDate);
        SimpleDateFormat objFromFormat = checkSimpleDateFormat(arrangedFormat);
        //SimpleDateFormat objToFormat = checkSimpleDateFormat(toFormat);
        java.util.Date objDate = parseDateFromString(objFromFormat, inDate);

        //System.out.println("ASAS : " +filteredFormat+":" + filteredFormat.indexOf("y") );
        if ((filteredFormat.toUpperCase()).indexOf("Y") < 0) throw new Exception("Year, Month or day data is needed. : " + inDate);
        if ((filteredFormat.toUpperCase()).indexOf("H") < 0) result = (defaultDateFormat.format(objDate)).substring(0,8) + "000000";
        else result = defaultDateFormat.format(objDate);

        int plus = inDate.indexOf("+");
        int minus = inDate.indexOf("-");
        //System.out.println("PPPPP : " + plus + " : " + minus);
        if ((plus > 0)||(minus > 0))
        {
            if ((plus > 0)&&(minus > 0)) throw new Exception("Invalid Timezones data : duplicate '+/-' : " + inDate);
            int timezoneS = 0;
            if (plus > 0) timezoneS = plus;
            else timezoneS = minus;
            String timezoneC = inDate.substring(timezoneS, timezoneS+1);
            String timezoneD = inDate.substring(timezoneS+1).trim();
            if (timezoneD.length() == 0) return result;
            else if (timezoneD.length() == 1) timezoneD = "0" + timezoneD + "00";
            else if (timezoneD.length() == 2) timezoneD = timezoneD + "00";
            else if (timezoneD.length() == 3) timezoneD = "0" + timezoneD;
            else if (timezoneD.length() == 4) {}
            else throw new Exception("Invalid Timezones data : higher than four digits : " + inDate);

            int timezoneHour = 0;
            try
            {
                timezoneHour = Integer.parseInt(timezoneD.substring(0,2));
            }
            catch(NumberFormatException ne)
            {
                throw new Exception("Invalid Timezones data : Not number data of timezone (1) : " + inDate);
            }
            if (timezoneHour > 12) throw new Exception("Invalid Timezones data : more than 12 hour data : " + inDate);
            int timezoneMinute = 0;
            try
            {
                timezoneMinute = Integer.parseInt(timezoneD.substring(2));
            }
            catch(NumberFormatException ne)
            {
                throw new Exception("Invalid Timezones data : Not number data of timezone (2) : " + inDate);
            }
            if (timezoneMinute >= 60) throw new Exception("Invalid Timezones data : more than 60 minute data : " + inDate);

            return result + timezoneC + timezoneD;
        }
        else return result;//return defaultDateFormat.format(objDate);
    }
    /*
    public Object changeFormat(Object fromFormatO, Object[] inDateO) throws Exception
    {
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXX Date") ;
        String fromFormat = fromFormatO.toString();
        String inDate = "";
        try
        {
            inDate = inDateO[1].toString();
            throw new Exception("Too many input date data. : " + inDate, 511, new Throwable(), ApplicationException.SEVERITY_LEVEL_WARNING);
        }
        catch(ArrayIndexOutOfBoundsException e)
        { }


        try
        {
            inDate = inDateO[0].toString();
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            throw new Exception("No input date data. : " + inDate, 511, new Throwable(), ApplicationException.SEVERITY_LEVEL_WARNING);
        }
        return (Object) changeFormat(fromFormat, inDate);
    }
    */
    /*
    public String formatDate(String strDate, String strOriginalDateFormat) throws Exception
    {
       String strHL7Date = null;
       SimpleDateFormat objHL7Format = null;
       SimpleDateFormat objDateFormat = null;
       Date objDate = null;

        try
        {
           objDateFormat = new SimpleDateFormat(strOriginalDateFormat);
           objDate = objDateFormat.parse(strDate);

           //Define the new date format
           objHL7Format = new SimpleDateFormat("yyyyMMdd");

           //Get the new format
           strHL7Date = objHL7Format.format(objDate);
        }
        catch (ParseException e)
        {
        }
    return strHL7Date;
    }
    */

}
