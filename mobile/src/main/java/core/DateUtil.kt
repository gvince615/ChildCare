package core

import java.text.SimpleDateFormat
import java.util.*

class DateUtil {
  fun getDateDifference(startDate: Date, endDate: Date): String {

    //milliseconds
    var different = endDate.time - startDate.time

    val secondsInMilli = 1000
    val minutesInMilli = secondsInMilli * 60
    val hoursInMilli = minutesInMilli * 60
    val daysInMilli = hoursInMilli * 24

    val elapsedDays = different / daysInMilli
    different %= daysInMilli

    val elapsedHours = different / hoursInMilli
    different %= hoursInMilli

    val elapsedMinutes = different / minutesInMilli
    different %= minutesInMilli

    var elapsedSeconds = different / secondsInMilli

    var output = ""

    if (elapsedDays > 0) {
      output += elapsedDays.toString() + " d :"
    }
    if (elapsedHours >= 0) {
      output += elapsedHours.toString() + " h :"
    }
    if (elapsedMinutes >= 0) {
      output += elapsedMinutes.toString() + " m :"
    }
    if (elapsedSeconds >= 0) {
      output += elapsedSeconds.toString() + " s"
    }

    return output
  }

  fun getDateObjectFromStringFormat(fmt: SimpleDateFormat, dateString: String?): Date? {
    return fmt.parse(dateString)
  }

  fun getFormattedDateTimeFromDate(fmt: SimpleDateFormat, date: Date?): String {
    return fmt.format(date)
  }

}