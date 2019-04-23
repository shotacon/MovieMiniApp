package com.shotacon.movie.utils.old;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    /**
     * 帖子的附件(图片)的日期格式 yyyyMM/dd/
     */
    public static final String ATTACHMENT_DATE = "yyyyMM/dd/";
    /**
     * 整个日期时间格式 yyyy-MM-dd HH:mm:ss
     */
    public static final String TOTAL_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    /**
     * 整个日期时间格式(没杠和分好空格，含毫秒 yyyyMMddHHmmssSSS
     */
    public static final String TOTAL_DATE_TIME_SIMPLE_SECOND = "yyyyMMddHHmmssSSS";

    /**
     * 整个日期时间格式 没有秒和毫秒 yyyy-MM-dd HH:mm
     */
    public static final String TOTAL_DATE_TIME_NOT_SECOND = "yyyy-MM-dd HH:mm";
    /**
     * 整个日期时间格式【简化版】 yyyyMMddHHmmss
     */
    public static final String TOTAL_DATE_TIME_SIMPLE = "yyyyMMddHHmmss";
    /**
     * 整个日期时间格式【日期没有杠的版本】 yyyyMMdd HH:mm:ss
     */
    public static final String TOTAL_DATE_TIME_NOT_GANG = "yyyyMMdd HH:mm:ss";
    /**
     * 整个日期时间格式【中间没有空格的版本】 yyyy-MM-ddHH:mm:ss
     */
    public static final String TOTAL_DATE_TIME_NOT_SPACE = "yyyy-MM-ddHH:mm:ss";
    /**
     * 整个日期时间格式【有毫秒】 yyyy-MM-dd HH:mm:ss SSS
     */
    public static final String TOTAL_DATE_TIME_SSS = "yyyy-MM-dd HH:mm:ss SSS";
    /**
     * 整个日期时间格式【中间没有空格的版本并且日期没有杠的版本】 yyyyMMddHH:mm:ss
     */
    public static final String TOTAL_DATE_TIME_NOT_SPACE_GANG = "yyyyMMddHH:mm:ss";
    /**
     * 简单时间格式 HH:mm:ss
     */
    public static final String SIMPLE_TIME = "HH:mm:ss";
    /**
     * 简单时间格式【没有冒号】 HHmmss
     */
    public static final String SIMPLE_TIME_NOT_COMMA = "HHmmss";
    /**
     * 简单日期格式 yyyyMMdd
     */
    public static final String SIMPLE_DATE = "yyyyMMdd";
    /**
     * 简单日期格式【带杠的】 yyyy-MM-dd
     */
    public static final String SIMPLE_GANG_DATE = "yyyy-MM-dd";

    /**
     * 日期格式 MM-dd HH:mm
     */
    public static final String SIMPLE_DATE_TIME = "MM-dd HH:mm";

    /**
     * // /Date(1547136000000+0800)/
     * 
     * @param date
     * @return
     */
    public static String handlerDateFromMF(String date) {
        String dateStr = date.replace("/Date(", "").replace(")/", "");
        String time = dateStr.substring(0, dateStr.length() - 5);
        Date realDate = new Date(Long.parseLong(time));
        SimpleDateFormat format = new SimpleDateFormat(TOTAL_DATE_TIME);
        return format.format(realDate);
    }

    /**
     * 以指定格式 格式化 制定日期
     *
     * @param date
     * @param formatStr
     * @return
     */
    public static String getDateStrUseFormat(Date date, String formatStr) {
        return new SimpleDateFormat(formatStr).format(date);
    }

    /**
     * 字符串日期的格式转换
     *
     * @param formatStr
     * @return
     */
    public static String getDateStrUseFormatToFormat(String dateStr, String formatStr, String toFormatStr) {
        try {
            return new SimpleDateFormat(toFormatStr).format(new SimpleDateFormat(formatStr).parse(dateStr));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 以指定格式 格式化 今天的日期
     *
     * @param formatStr
     * @return
     */
    public static String getTodayStrUseFormat(String formatStr) {
        return new SimpleDateFormat(formatStr).format(new Date());
    }

    /**
     * 以【指定格式】和【日期串】获得昨天的日期字符串
     *
     * @param formatStr
     * @return
     */
    public static String getYesterdayStrUseFormatAndDateStr(String formatStr, String dateStr) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatStr);
            // 现在
            Date now = simpleDateFormat.parse(dateStr);
            return simpleDateFormat.format(getYesterdayDate(now).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 以【指定格式】和【日期】获得昨天的日期字符串
     *
     * @param formatStr
     * @return
     */
    public static String getYesterdayStrUseFormatAndDate(String formatStr, Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatStr);
        return simpleDateFormat.format(getYesterdayDate(date).getTime());
    }

    /**
     * 用指定日期获得昨天的日期对象
     *
     * @param now
     * @return
     */
    private static Date getYesterdayDate(Date now) {
        Calendar yesterday = Calendar.getInstance();
        yesterday.setTime(now);
        yesterday.set(Calendar.DATE, yesterday.get(Calendar.DATE) - 1);
        return yesterday.getTime();
    }

    /**
     * 用指定日期获得昨天的Calendar对象
     *
     * @return
     */
    public static Calendar getYesterdayCalendar(Date date) {
        Calendar yesterday = Calendar.getInstance();
        yesterday.setTime(date);
        yesterday.set(Calendar.DATE, yesterday.get(Calendar.DATE) - 1);
        return yesterday;
    }

    /**
     * 获得【现在时刻】的Calendar对象
     *
     * @return
     */
    public static Calendar getNowCalendar() {
        Calendar now = Calendar.getInstance();
        now.setTime(new Date());
        return now;
    }

    /**
     * 用指定日期对象获得明天的的日期对象
     *
     * @param dateCalendar
     * @return
     */
    public static void getTomorrow(Calendar dateCalendar) {
        dateCalendar.set(Calendar.DATE, dateCalendar.get(Calendar.DATE) + 1);
    }

    /**
     * 通过【格式串】获得明天的日期字符串
     *
     * @return
     */
    public static String getTomorrowStrByFormatStr(String formatStr) {
        Calendar dateCalendar = getCalendarFromDate(new Date());
        dateCalendar.set(Calendar.DATE, dateCalendar.get(Calendar.DATE) + 1);
        return new SimpleDateFormat(formatStr).format(dateCalendar.getTime());
    }

    /**
     * 通过指定【日期字符串】和【日期格式】获取Calendar对象
     *
     * @param formatStr
     * @param dateStr
     * @return
     */
    public static Calendar getCalendarFromDateStrAndFormat(String formatStr, String dateStr) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getDateFromDateStrAndFormat(formatStr, dateStr));
        return calendar;
    }

    /**
     * 通过指定【日期】获取Calendar对象
     *
     * @return
     */
    public static Calendar getCalendarFromDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    /**
     * 通过指定【日期字符串】和【日期格式】获取日期
     *
     * @param formatStr
     * @param dateStr
     * @return
     */
    public static Date getDateFromDateStrAndFormat(String formatStr, String dateStr) {
        try {
            return new SimpleDateFormat(formatStr).parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 验证传入的日期是否是周末 true -> 周末 false -> 不是周末
     *
     * @param calendar
     * @return
     */
    public static boolean validateWeekend(Calendar calendar) {
        // 日期为周六
        boolean saturdayFlag = calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY;
        // 日期为周日
        boolean sundayFlag = calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
        if (saturdayFlag || sundayFlag) {
            return true;
        }
        return false;
    }

    /**
     * 获取【指定天数】前的【指定日期】的日期对象
     *
     * @param dayNum
     * @param date
     * @return
     */
    public static Calendar getCalendarBeforeDayByDate(int dayNum, Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - dayNum);
        return calendar;
    }

    /**
     * 【指定格式】的【日期字符串】和【日期对象】比较 true = 【日期字符串】小于【日期对象】 false = 【日期字符串】大于【日期对象】
     *
     * @param dateStr
     * @param formatStr
     * @param dateCalendar
     * @return
     */
    public static boolean compareBefore(String dateStr, String formatStr, Calendar dateCalendar) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatStr);
        Calendar dateStrCalendar = Calendar.getInstance();
        try {
            dateStrCalendar.setTime(simpleDateFormat.parse(dateStr));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateStrCalendar.before(dateCalendar);
    }

    /**
     * 【指定格式】的【日期字符串】和【日期对象】比较 true = 【日期字符串】大于【日期对象】 false = 【日期字符串】小于【日期对象】
     *
     * @param dateStr
     * @param formatStr
     * @param dateCalendar
     * @return
     */
    public static boolean compareBeforeFromCalendarToStr(String dateStr, String formatStr, Calendar dateCalendar) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatStr);
        Calendar dateStrCalendar = Calendar.getInstance();
        try {
            dateStrCalendar.setTime(simpleDateFormat.parse(dateStr));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateCalendar.before(dateStrCalendar);
    }

    /**
     * 获得【指定日期格式】的【指定日期对象】的字符串
     *
     * @param formatStr
     * @param calendar
     * @return
     */
    public static String getDateStrByCalendarAndFormat(String formatStr, Calendar calendar) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatStr);
        return simpleDateFormat.format(calendar.getTime());
    }

    /**
     * 比较【第一个时间】位于【第二个时间】之后或【两者】相等
     *
     * @param timeOne
     * @param timeTwo
     * @return
     */
    public static boolean compareTimeAfterEqualsByTimeStr(String timeOne, String timeTwo) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(SIMPLE_TIME);
        Calendar oneCalendar = Calendar.getInstance();
        Calendar twoCalendar = Calendar.getInstance();
        try {
            oneCalendar.setTime(simpleDateFormat.parse(timeOne));
            twoCalendar.setTime(simpleDateFormat.parse(timeTwo));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return oneCalendar.after(twoCalendar) || timeOne.equals(timeTwo);
    }

    /**
     * 比较【第一个时间】位于【第二个时间】之后或【两者】相等
     *
     * @param formatStr 【格式串】
     * @return 2017年9月7日 下午5:30:37
     */
    public static boolean compareTimeAfterEqualsByTimeAndFormat(String timeStr, Calendar twoCalendar,
            String formatStr) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatStr);
        Calendar oneCalendar = Calendar.getInstance();
        try {
            oneCalendar.setTime(simpleDateFormat.parse(timeStr));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return oneCalendar.after(twoCalendar) || oneCalendar.equals(twoCalendar);
    }

    /**
     * 比较【第一个时间】位于【第二个时间对象】之后（或等于）
     *
     * @param timeOne
     * @return
     */
    public static boolean compareTimeAfterEqualsByTimeStr(String timeOne, Calendar timeTwoCalendar) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(SIMPLE_TIME);
        Calendar oneCalendar = Calendar.getInstance();
        try {
            oneCalendar.setTime(simpleDateFormat.parse(timeOne));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return oneCalendar.after(timeTwoCalendar) || oneCalendar.equals(timeTwoCalendar);
    }

    /**
     * 比较一个【日期时间对象】在另一个【日期时间字符串】之后
     *
     * @param oneCalendar
     * @param timeStr
     * @param formatStr
     * @return
     */
    public static boolean compareTimeAfterByCalendarAndTime(Calendar oneCalendar, String timeStr, String formatStr) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatStr);
        Calendar twoCalendar = Calendar.getInstance();
        try {
            twoCalendar.setTime(simpleDateFormat.parse(timeStr));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return oneCalendar.after(twoCalendar);
    }

    /**
     * 比较一个【日期时间字符串】在另一个【日期时间对象】之后
     *
     * @return
     */
    public static boolean compareTimeAfterByCalendarAndDate(String timeOne, Calendar timeTwoCalendar) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(SIMPLE_GANG_DATE);
        Calendar oneCalendar = Calendar.getInstance();
        try {
            oneCalendar.setTime(simpleDateFormat.parse(timeOne));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return oneCalendar.after(timeTwoCalendar);
    }

    /**
     * 比较一个【日期时间字符串】在另一个【日期时间对象】之后
     *
     * @return
     */
    public static boolean compareTimeAfterByCalendarAndTime(String timeOne, Calendar timeTwoCalendar) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(SIMPLE_TIME);
        Calendar oneCalendar = Calendar.getInstance();
        try {
            oneCalendar.setTime(simpleDateFormat.parse(timeOne));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return oneCalendar.after(timeTwoCalendar);
    }

    /**
     * 比较一个【日期时间字符串】在另一个【日期时间对象】之前
     *
     * @param timeOne
     * @param timeTwoCalendar
     * @return 2017年9月8日 下午5:04:50
     */
    public static boolean compareTimeStrBeforeByTimeObject(String timeOne, Calendar timeTwoCalendar) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(SIMPLE_TIME);
        Calendar strCalendar = Calendar.getInstance();
        try {
            strCalendar.setTime(simpleDateFormat.parse(timeOne));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return strCalendar.before(timeTwoCalendar);
    }

    /**
     * 比较一个【日期时间字符串】在另一个【日期时间对象】之前
     *
     * @param timeOne
     * @param timeTwoCalendar
     * @return 2017年9月8日 下午5:04:50
     */
    public static boolean compareTimeStrBeforeByTimeObject(String timeOne, Calendar timeTwoCalendar, String formatStr) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatStr);
        Calendar strCalendar = Calendar.getInstance();
        try {
            strCalendar.setTime(simpleDateFormat.parse(timeOne));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return strCalendar.before(timeTwoCalendar);
    }

    /**
     * 比较一个【日期时间对象】在另一个【日期时间对象】之后
     *
     * @param oneCalendar
     * @param twoCalendar
     * @return
     */
    public static boolean compareCalendarAfter(Calendar oneCalendar, Calendar twoCalendar) {
        return oneCalendar.after(twoCalendar);
    }

    /**
     * 用【格式化字符串】格式【Calendar对象】返回【字符串】
     *
     * @param calendar
     * @param formatStr
     * @return
     */
    public static String getStrByCalendarAndFormatStr(Calendar calendar, String formatStr) {

        try {
            return new SimpleDateFormat(formatStr).format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获得【当前时间】的【时间字符串】
     *
     * @return
     */
    public static String getTimeStyByNowTime() {
        Calendar nowCalendar = getCalendarFromDate(new Date());
        nowCalendar.add(Calendar.MINUTE, 1);
        return new SimpleDateFormat(SIMPLE_TIME).format(nowCalendar.getTime());
    }

    /**
     * 获得【当前时间】加【指定秒数】的【日期对象】
     *
     * @param secondNum
     * @return 2017年6月13日 下午5:13:15
     */
    public static Date getDateByAddSecond(int secondNum) {
        Calendar nowCalendar = getCalendarFromDate(new Date());
        nowCalendar.add(Calendar.SECOND, secondNum);
        return nowCalendar.getTime();
    }

    /**
     * 【时间字符串】指定单位加指定数值并返回【时间对象】
     *
     * @param addNum
     * @param unit
     * @param timeStr
     * @return
     */
    public static Calendar getTimeCalendarByAdded(int addNum, int unit, String timeStr) {
        Calendar calendar = DateUtil.getCalendarFromDateStrAndFormat(DateUtil.SIMPLE_TIME, timeStr);
        calendar.add(unit, addNum);
        return calendar;
    }

    /**
     * 获得添加指定【秒数】的【现在时刻】
     *
     * @param secondNum
     * @return 添加完后的【毫秒数】
     */
    public static long getNowByAdded(int secondNum) {
        Calendar calendar = getNowCalendar();
        calendar.add(Calendar.SECOND, secondNum);
        return calendar.getTime().getTime();
    }

    /**
     * 获得添加指定【天数】，指定格式的【现在时刻】
     *
     * @param dayNum
     * @param formatStr
     * @return 2017年3月15日 下午5:32:00
     */
    public static String getNowByAdded(int dayNum, String formatStr) {
        Calendar calendar = getNowCalendar();
        calendar.add(Calendar.DATE, dayNum);
        return new SimpleDateFormat(formatStr).format(calendar.getTime());
    }

    /**
     * 某个日期+N天
     *
     * @param date      某个日期
     * @param dayNum    N
     * @param formatStr
     * @return
     */
    public static String getDateByAdded(String date, int dayNum, String formatStr) {
        Calendar calendar = getCalendarFromDateStrAndFormat(formatStr, date);
        calendar.add(Calendar.DATE, dayNum);
        return new SimpleDateFormat(formatStr).format(calendar.getTime());
    }

    /**
     * 获得添加指定【分钟数】指定格式的【现在时刻】
     *
     * @param minute
     * @param formatStr
     * @return 2017年3月15日 下午5:32:00
     */
    public static String getNowByAddedMinute(int minute, String formatStr) {
        Calendar calendar = getNowCalendar();
        calendar.add(Calendar.MINUTE, minute);
        return new SimpleDateFormat(formatStr).format(calendar.getTime());
    }

    /**
     * 获得当前的秒数【距1970】 【10位】
     *
     * @return 2016年12月15日 下午3:05:46
     */
    public static long getNowSecond() {
        return new Date().getTime() / 1000;
    }

    /**
     * 获得当前的秒数【距1970】 【返回字符串】
     *
     * @return 2016年11月19日 上午11:26:04
     */
    public static String getNowSecondStr() {
        return new Date().getTime() / 1000 + "";
    }

    /**
     * 当前毫秒时间戳 + 指定秒数
     *
     * @return
     */
    public static long getTimeAddSecond(int secondNum) {
        return new Date().getTime() + (secondNum * 1000);
    }

    /**
     * 当前毫秒时间戳 + 指定小时数 【返回（字符串）】 返回：毫秒数
     *
     * @param hourNum
     * @return 2016年11月22日 下午3:11:24
     */
    public static String getTimeStrAddHours(int hourNum) {
        return new Date().getTime() + (hourNum * 60 * 60 * 1000) + "";
    }

    /**
     * 当前毫秒时间戳 + 指定小时数 【返回（字符串）】 返回：秒数
     *
     * @param hourNum
     * @return 2016年11月22日 下午3:11:24
     */
    public static String getTimeStrAddHoursSecond(int hourNum) {
        return new Date().getTime() + (hourNum * 60 * 60) + "";
    }

    /**
     * 当前毫秒时间戳 + 指定小时数 【返回（long）】 返回：【秒数】
     *
     * @param hourNum
     * @return 2016年11月22日 下午3:11:24
     */
    public static int getTimeAddHoursSecond(int hourNum) {
        return Integer.parseInt(((new Date().getTime() + (hourNum * 60 * 60 * 1000)) / 1000) + "");
    }

    /**
     * 当前毫秒时间戳 + 指定分钟数 【返回（int）】 返回：【秒数】
     *
     * @param minuteNum
     * @return 2016年11月22日 下午3:11:24
     */
    public static int getTimeAddMinute(int minuteNum) {
        return Integer.parseInt(((new Date().getTime() + (minuteNum * 60 * 1000)) / 1000) + "");
    }

    /**
     * 【当前时刻】和【传入时刻】比较 true -> 【当时时刻】超过【传入时刻】
     *
     * @param time 【毫秒单位】
     * @return
     */
    public static boolean compareNowAndTime(long time) {
        return new Date().getTime() - time > 0;
    }

    /**
     * 【秒数比较】 【当前时刻】和【传入时刻】比较 true -> 【当时时刻】超过【传入时刻】
     *
     * @param time 【秒单位】
     * @return
     */
    public static boolean compareNowAndTimeBySecond(long time) {
        return new Date().getTime() / 1000 - time > 0;
    }

    /**
     * 通过【格式字符串】获得【现在时间字符串】
     *
     * @param formatStr
     * @return
     */
    public static String getNowDateStrByFormatStr(String formatStr) {
        return new SimpleDateFormat(formatStr).format(new Date());
    }

    /**
     * 将时间戳转换成指定格格式的时间
     *
     * @param formatStr
     * @param timeStamp
     * @return
     */
    public static String transTimeStamp(String formatStr, String timeStamp) {
        long time = new Long(timeStamp);
        Date date = new Date(time);
        return new SimpleDateFormat(formatStr).format(date);
    }

    public static void main(String[] args) {
        
        String date = "/Date(1547136000000+0800)/";
        System.out.println(handlerDateFromMF(date));

//        System.out.println(transTimeStamp(TOTAL_DATE_TIME, "14689897654"));
//		System.out.println(DateUtil.getYesterdayStrUseFormatAndDateStr(SIMPLE_DATE, "20150709"));

//		Date date1 = null;
//		try {
//			date1 = new SimpleDateFormat(SIMPLE_DATE).parse("20170908");
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		Date date2 = new Date();
//		System.out.println(date2.getDate() - date1.getDate());
//		System.out.println(date2.before(date1));

//		Calendar calendar = Calendar.getInstance();
//		calendar.setTime(new Date());
//		getTomorrow(calendar);
//		System.out.println(calendar.getTime());
//		getTomorrow(calendar);
//		System.out.println(calendar.getTime());
//		getTomorrow(calendar);
//		System.out.println(calendar.getTime());
//		getTomorrow(calendar);
//		System.out.println(calendar.getTime());
//		getTomorrow(calendar);
//		System.out.println(calendar.getTime());
//		Calendar calendar = Calendar.getInstance();
//		calendar.setTime(new Date());
//		System.out.println(compareBefore("2015-10-21", SIMPLE_GANG_DATE, calendar));
//		System.out
//				.println(new SimpleDateFormat(DateUtil.TOTAL_DATE_TIME_NOT_SPACE).format(DateUtil
//						.getCalendarFromDateStrAndFormat(
//								DateUtil.TOTAL_DATE_TIME_NOT_SPACE,
//								"2014-03-039:09:42").getTime()));
//		String timeStr = "09:09:42";
//		System.out.println(timeStr.substring(0, timeStr.indexOf(':')));
//		String fileName = "j05_20140303";
//		System.out.println(fileName.substring(0, fileName.indexOf('_')));

//		Date date = new Date();
//        long time = date.getTime();
//        System.out.println(time);
//		System.out.println(getNowSecond());
//		System.out.println(getTimeAddSecond(60));
//		System.out.println(getNowByAdded(60));
//		System.out.println(compareNowAndTime(1471349054291l));
//		System.out.println(DateUtil.getTodayStrUseFormat(TOTAL_DATE_TIME_SSS));
//		long time = getTimeAddSecond(5);
//		try {
//			Thread.sleep(6000l);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		System.out.println(compareNowAndTime(time));
        // 20161104084959901
//		System.out.println((new Date().getTime() + (5 * 60 * 60 * 1000)));
//		long x = 1480319069609l;
//		System.out.println(new Date().getTime() - x);
//		int x = 1898220689;
//		System.out.println(compareNowAndTimeBySecond(x));
//		System.out.println(new Date().getTime() / 1000);
//		201603/31
//		System.out.println(getNowDateStrByFormatStr(ATTACHMENT_DATE));
//		System.out.println(getNowByAdded(2, TOTAL_DATE_TIME));
//		System.out.println(Long.parseLong(DateUtil.getTodayStrUseFormat(DateUtil.SIMPLE_DATE)) == 20170112);

//		System.out.println(getTimeAddMinute(30));
//		System.out.println(DateFormatUtil.dateFormat("1490101101"));
//		System.out.println(new Date().getTime() / 1000);
//		System.out.println(getTimeAddHoursSecond(24));
//		System.out.println(getNowSecondStr());
//		System.out.println(getNowByAddedMinute(2, TOTAL_DATE_TIME_SSS));
    }
}
