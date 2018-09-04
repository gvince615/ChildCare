package attendance

class AttendanceRecord(childId: String?, firstName: String?, lastName: String?, checkInTime: String?, checkOutTime: String?) {
  internal var childId = childId
  internal var firstName = firstName
  internal var lastName = lastName
  internal var checkInTime: String? = checkInTime
  internal var checkOutTime: String? = checkOutTime

}
