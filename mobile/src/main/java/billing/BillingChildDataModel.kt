package billing

import attendance.AttendanceRecord


class BillingChildDataModel(
    var childId: String = "",
    var firstName: String = "",
    val attendanceRecord: ArrayList<AttendanceRecord>
)