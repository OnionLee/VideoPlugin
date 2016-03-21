using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

public static class TimeUtility
{
	public static DateTime ToDateTime(double timeStamp)
	{
		DateTime epochTime = new DateTime(1970, 1, 1, 0, 0, 0, 0);
		return epochTime.AddSeconds(timeStamp);
	}

	public static double ToTimeStamp(this DateTime dateTime)
	{
		DateTime epochTime = new DateTime(1970, 1, 1, 0, 0, 0, 0);
		TimeSpan diff = dateTime - epochTime;
		return diff.TotalSeconds;
	}
}