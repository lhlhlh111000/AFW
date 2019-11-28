package com.lease.framework.core;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@SuppressLint("SimpleDateFormat")
/**
 * 通用日历计算类
 */
public class DateUtils {

	public static Calendar dataToCalendar(long time){
		Calendar calendar=Calendar.getInstance();
		calendar.setTimeInMillis(time);
		return calendar;
	}

	/**
	 * 根据选择的日期,以今天的时间为准,计算出新的对照表
	 * @param	pattern {0:1:0:1:0:1:0}
	 * */
	public static String InitialPattern(String pattern){

		Calendar calendarNow = Calendar.getInstance();
		String newPattern = pattern;

		int adow = GetActuallyDayOfWeek(calendarNow.get(Calendar.DAY_OF_WEEK))-1;

		//非周一
		if (adow>0) {
			int cursor = adow*2;

			StringBuilder tmpAlpha = new StringBuilder();
			StringBuilder tmpBravo = new StringBuilder();

			tmpAlpha.append(pattern.substring(0,adow*2));
			tmpBravo.append(pattern.substring(cursor));
			tmpBravo.append(":");
			tmpAlpha.deleteCharAt(tmpAlpha.lastIndexOf(":"));
			newPattern = tmpBravo.append(tmpAlpha).toString();
		}

		return newPattern;
	}


	/**
	 * 根据选择的日期,以今天的时间为准,计算出未来几天的提醒日
	 * @param	pattern {0:1:0:1:0:1:0}
	 * */
	public static List<Long> InitialDateList(String pattern){

		List<Long> remindDate = new ArrayList<Long>();

		Calendar tmpCalendar = Calendar.getInstance();

		String [] arrayDayzSelected = pattern.split(":");

		for (int i = 0; i < arrayDayzSelected.length; i++) {

			if (Integer.valueOf(arrayDayzSelected[i])==1) {
				remindDate.add(tmpCalendar.getTimeInMillis());
			}
			tmpCalendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		return remindDate;

	}


	/**
	 * 获得实际星期几
	 * */
	private static int GetActuallyDayOfWeek(int i){
		if (i==1) {
			return 7;
		}else{
			return i-1;
		}
	}
	/**
	 * 时间转化yyyy-M-d
	 * 
	 * @param time
	 * @return
	 */
	public static String formatCalendar(Calendar time) {
		time = time == null ? Calendar.getInstance() : time;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-M-d");
		Date curDate = time.getTime();
		return formatter.format(curDate);
	}

	private static DateFormat df = new SimpleDateFormat("yyyy-M-d");

	/**
	 * 将"2001-01-01" 转化为Calendar格式
	 * 
	 * @param time
	 * @return
	 */
	public static Calendar formatString(String time) {
		try {
			Date d = df.parse(time);
			Calendar c = Calendar.getInstance();
			c.setTime(d);
			return c;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取两个日期之间的月数
	 *
	 * @param old
	 * @param now
	 * @return
	 */
	public static int getMonthBetweenCalendar(Calendar old, Calendar now) {
		int year = now.get(Calendar.YEAR) - old.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH) + year * 12 - old.get(Calendar.MONTH);
		return month;
	}

	/**
	 * 获取之间的月份
	 * @param old
	 * @param now
	 * @return
	 */
	public static int getMonthExactlyBetweenCalendar(Calendar old, Calendar now) {
		int yearCha = now.get(Calendar.YEAR) - old.get(Calendar.YEAR);
		int monthCha = now.get(Calendar.MONTH) - old.get(Calendar.MONTH);
		int dayCha = now.get(Calendar.DAY_OF_MONTH) - old.get(Calendar.DAY_OF_MONTH);
		if(dayCha>=0){
			return  yearCha*12+monthCha;
		}else{
			return  yearCha*12+monthCha-1;
		}
	}

	public static Calendar getCalenar(String string) {
		try{
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-M-d");
			Date date = simpleDateFormat.parse(string);
			Calendar calendar  =(Calendar)Calendar.getInstance().clone();
			calendar.setTime(date);
			return calendar;
		}catch (Exception ex){
			ex.printStackTrace();
		}
		return null;

	}

	/**
	 * 获取两个日历之间的天数
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static int get2CalendarDaysBetween(Calendar start, Calendar end) {

		start = (start == null) ? Calendar.getInstance() : start;
		end = (end == null) ? Calendar.getInstance() : end;

		if (end.get(Calendar.YEAR) != start.get(Calendar.YEAR)) {
			Calendar startTag = (Calendar) start.clone();
			startTag.set(Calendar.HOUR_OF_DAY, end.get(Calendar.HOUR_OF_DAY));
			startTag.set(Calendar.MINUTE, end.get(Calendar.MINUTE));
			startTag.set(Calendar.SECOND, end.get(Calendar.SECOND));
			float second = (end.getTimeInMillis() - startTag.getTimeInMillis()) / 1000;
			float res = second / (24 * 60 * 60);
			return Math.round(res);
		} else {
			return end.get(Calendar.DAY_OF_YEAR)
					- start.get(Calendar.DAY_OF_YEAR);
		}
	}

	/**
	 * 转换UTC时间为本地时间
	 *
	 * @param strUtcTime
	 * @return
	 */
	public static String convertUtcToLocalTime(String strUtcTime) {
		try {
			// LogUtils.d("CalendarUtil","转化前时间为："+strUtcTime); 2013-09-22
			// 00:48:08
			// 获取UTC时间的Date
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d HH:mm:ss");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			Date utcDate = sdf.parse(strUtcTime);
			// 获取本地时区格式化utc时间为本地
			SimpleDateFormat localFormater = new SimpleDateFormat(
					"yyyy-M-d HH:mm:ss");
			localFormater.setTimeZone(TimeZone.getDefault());
			String localTime = localFormater.format(utcDate.getTime());
			// LogUtils.d("CalendarUtil","转化后时间为："+localTime);
			return localTime;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "";
	}

	/**
	 * 获取怀孕几率
	 *
	 * @return
	 */
	/**
	 * 获取下一次来月经时间
	 * 
	 * @param calendar
	 * @return
	 */
	public static Calendar getNextPeriodTime(Calendar calendar, int periodCircle) {
		calendar = fixCalendar(calendar, periodCircle);
		Calendar now = (Calendar) Calendar.getInstance().clone();
		while (true) {
			if (get2CalendarDaysBetween(calendar, now) >= 0) {
				calendar.add(Calendar.DAY_OF_YEAR, periodCircle);
			} else {
				break;
			}
		}
		// calendar.add(Calendar.DAY_OF_YEAR, 1);
		// calendar.add(Calendar.DAY_OF_YEAR, 2 *periodCircle);
		return (Calendar) calendar.clone();
	}

	/**
	 * 为了避免日期在当月或者未来日期时，计算日期之差时出现负数， 将日期修改到本月之前的月份（必须按例假周期来增减）
	 * 
	 * @param calendar
	 * @return
	 */
	private static Calendar fixCalendar(Calendar calendar, int periodCircle) {
		/*
		 * Calendar thisMonth = (Calendar) Calendar.getInstance().clone(); int y
		 * = calendar.get(Calendar.YEAR) - thisMonth.get(Calendar.YEAR); if(y >=
		 * 1){ calendar.add(Calendar.DAY_OF_YEAR, -1 * periodCircle * 11 * y);//
		 * 修正年份
		 * 
		 * int m = calendar.get(Calendar.MONTH) - thisMonth.get(Calendar.MONTH);
		 * calendar.add(Calendar.DAY_OF_YEAR, -1 * periodCircle * m);// 修正月份 //
		 * 修改到本月之前 for (;;) { calendar.add(Calendar.DAY_OF_YEAR, -1 *
		 * periodCircle); // 当时间在本月之前，结束循环 if (calendar.before(thisMonth) &&
		 * !isYearMonthSame(thisMonth,calendar)) { break; } } }
		 */
		return calendar;
	}

	/**
	 * 是否是同一个月
	 * 
	 * @param calendar
	 * @param calendar2
	 * @return
	 */
	public static boolean isYearMonthSame(Calendar calendar, Calendar calendar2) {
		if (calendar == null || calendar2 == null) {
			return false;
		}
		if (calendar.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)) {
			if (calendar.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 是否是同一天
	 * 
	 * @param calendar
	 * @param calendar2
	 * @return
	 */
	public static boolean isYearMonthDaySame(Calendar calendar,
			Calendar calendar2) {
		try {

			if (calendar == null || calendar2 == null) {
				return false;
			}
			if (calendar.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)) {
				if (calendar.get(Calendar.MONTH) == calendar2
						.get(Calendar.MONTH)) {
					if (calendar.get(Calendar.DAY_OF_MONTH) == calendar2
							.get(Calendar.DAY_OF_MONTH)) {
						return true;
					}
				}
			}
			return false;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}
	/**
	 * 返回 年/月/日 格式数据
	 * */
	public static String getStrDateViaCalendar(Calendar c, String... divider) {
		SimpleDateFormat format = new SimpleDateFormat("MM" + divider[0] + "dd"
				+ divider[1]);
		try {
			String result = format.format(c.getTime());
			return result;
			// StringBuilder sb = new StringBuilder();
			// sb.append(c.get(Calendar.YEAR)).append("/").append((c.get(Calendar.MONTH)+1)).append("/").append(c.get(Calendar.DAY_OF_MONTH));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return format.format(Calendar.getInstance().getTime());
	}

	/**
	 * 转换UTC时间 为 几天前 几小时前 几分钟前 几秒前 2014-07-25 15:33:17
	 *
	 * @param isNeedChange
	 *            是否需要转换时间格式，true=需要转换
	 * @param ts
	 * @param startTimeAdd
	 *            开始的时间上增加startTimeAdd毫秒，startTimeAdd=0时表示不添加
	 * @param style
	 *            1= month + "-" + day_month + ""; 2=month + "月" + day_month +
	 *            "日";
	 * @return 例子： String ts = "2007-10-23T17:15:44.000Z";
	 *         LogUtils.dln("ts = " + ts); ts = ts.replace("Z", " UTC");
	 *         LogUtils.dln("ts = " + ts); SimpleDateFormat sdf = new
	 *         SimpleDateFormat("yyyy-M-d'T'HH:mm:ss");
	 *         updated_date":"2013-07-11 02:40:05"
	 */
	public static String convertUtcTime(String ts, boolean isNeedChange,
										int style, int startTimeAdd) {
		try {
			if (TextUtils.isEmpty(ts))
				return "";
			if (isNeedChange) {
				ts = convertUtcToLocalTime(ts);
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d HH:mm:ss"); // 2014-03-05
			// 12:24:06
			Date date = sdf.parse(ts);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			Calendar start = sdf.getCalendar();
			if (startTimeAdd != 0) {
				start.add(Calendar.MILLISECOND, startTimeAdd);
			}
			Calendar end = Calendar.getInstance();
			// 计算间隔
			long startTime = start.getTimeInMillis();
			long endTime = end.getTimeInMillis();
			long time = endTime - startTime;
			long day = time / (24 * 60 * 60 * 1000);
			if (day > 0 && day < 4) {
				return day + "天前";
			} else if (day >= 4) {
				if (calendar.get(Calendar.YEAR) == Calendar.getInstance().get(
						Calendar.YEAR)) {
					@SuppressWarnings("deprecation")
					int month = date.getMonth() + 1;
					int day_month = calendar.get(Calendar.DAY_OF_MONTH);
					if (style == 1) {
						return month + "-" + day_month + "";
					} else {
						return month + "月" + day_month + "日";
					}
				} else {
					int year = calendar.get(Calendar.YEAR);
					@SuppressWarnings("deprecation")
					int month = date.getMonth() + 1;
					int day_month = calendar.get(Calendar.DAY_OF_MONTH);
					if (style == 1) {
						return year + "-" + month + "-" + day_month + "";
					} else {
						return year + "年" + month + "月" + day_month + "日";
					}
				}
			}
			long hour = time / (1000 * 60 * 60);
			if (hour > 0)
				return hour + "小时前";
			long minute = time / (1000 * 60);
			if (minute > 0)
				return minute + "分钟前";
			return "刚刚";
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (style == 1) {
			return "-";
		} else {
			return "";
		}
	}

	public static String convertLocalTimeToUTC(String originlocal){
		try {
			// LogUtils.d("CalendarUtil","转化前时间为："+strUtcTime); 2013-09-22
			// 00:48:08
			// 获取UTC时间的Date
			SimpleDateFormat local = new SimpleDateFormat("yyyy-M-d HH:mm:ss");
			local.setTimeZone(TimeZone.getDefault());
			Date localDate = local.parse(originlocal);
			// 获取本地时区格式化utc时间为本地
			SimpleDateFormat utcFormater = new SimpleDateFormat(
					"yyyy-M-d HH:mm:ss");
			utcFormater.setTimeZone(TimeZone.getTimeZone("UTC"));
			String localTime = utcFormater.format(localDate.getTime());
			// LogUtils.d("CalendarUtil","转化后时间为："+localTime);
			return localTime;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "";

	}
	/**
	 * 时间转换（Calendar转为String）
	 *
	 * @param calendar
	 * @param style
	 * @return
	 */
	public static String convertCalendarToString(Calendar calendar, int style) {
		try {
			SimpleDateFormat sdf = null;
			switch (style) {
				case 0:
					sdf = new SimpleDateFormat("yyyy-M-d HH:mm:ss");
					break;
				case 1:
					sdf = new SimpleDateFormat("yyyy-M-d");
					break;
				default:
					sdf = new SimpleDateFormat("yyyy-M-d HH:mm:ss");
			}
			String date = sdf.format(calendar.getTime());
			return date;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "";
	}


	/**
	 * 获取日 没有日这个字
	 *
	 * @param calendar
	 * @return
	 */
	public static String getDayNoRi(Calendar calendar) {
		calendar = calendar == null ? Calendar.getInstance() : calendar;
		return calendar.get(Calendar.DAY_OF_MONTH) + "";
	}

	/**
	 * 获取月
	 *
	 * @param calendar
	 * @return
	 */
	public static String getMonth(Calendar calendar) {
		calendar = calendar == null ? Calendar.getInstance() : calendar;
		SimpleDateFormat yearFormat = new SimpleDateFormat("MM月");
		return yearFormat.format(calendar.getTime());
	}

	/**
	 * 获取月日 格式（3月2日） 非（03月03日）
	 *
	 * @param calendar
	 * @return
	 */
	public static String getMonthDayLevel2(Calendar calendar) {
		calendar = calendar == null ? Calendar.getInstance() : calendar;
		return (calendar.get(Calendar.MONTH) + 1) + "月"
				+ calendar.get(Calendar.DAY_OF_MONTH) + "日";
	}

	public static String getWeek(Calendar calendar) {
		calendar = calendar == null ? Calendar.getInstance() : calendar;

		return (DAY_OF_WEEK2[calendar.get(Calendar.DAY_OF_WEEK) - 1]);
	}

	private static String[] DAY_OF_WEEK2 = new String[] { "星期天", "星期一", "星期二",
			"星期三", "星期四", "星期五", "星期六" };

	public static String getTimeZoneName() {
		try {
			TimeZone tz = TimeZone.getDefault();
			//String s = "TimeZone   "+tz.getDisplayName(false, TimeZone.SHORT)+" Timezon id :: " +tz.getID();
			//LogUtils.dln(s);

			return tz.getDisplayName(false, TimeZone.SHORT);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "";
	}
}
