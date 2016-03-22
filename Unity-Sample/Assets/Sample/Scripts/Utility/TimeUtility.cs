using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

public static class TimeUtility
{
	public static DateTime ToDateTime(double timeStamp)
	{
		//가끔씩 TimeStamp가 잘못 들어오는 경우가 있음
		DateTime epochTime = new DateTime(1970, 1, 1, 0, 0, 0, 0);
		try
		{
			return epochTime.AddSeconds(timeStamp);
		}
		catch
		{
			return epochTime;
		}
	}

	public static double ToTimeStamp(this DateTime dateTime)
	{
		DateTime epochTime = new DateTime(1970, 1, 1, 0, 0, 0, 0);
		TimeSpan diff = dateTime - epochTime;
		return diff.TotalSeconds;
	}
}