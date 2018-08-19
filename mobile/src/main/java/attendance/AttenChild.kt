package attendance

class AttenChild(childId: String?, childImageUri: String?, firstName: String?, lastName: String?, isActive: String?, birthDate: String?,
    enrollmentDate: String?,
    checkInTime: String?) {
  internal var childId = childId
  internal var childImageUri = childImageUri
  internal var firstName = firstName
  internal var lastName = lastName
  internal var birthDate = birthDate
  internal var isActive = isActive
  internal var enrollmentDate = enrollmentDate
  internal var checkInTime: String? = checkInTime
}
