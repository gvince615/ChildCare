package registration

data class Child(
    var childId: String = "",
    var childImageUrl: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var birthDate: String = "",
    var isActive: String? = "",
    var enrollmentDate: String = "",
    var addressLn1: String = "",
    var addressLn2: String = "",
    var addressCity: String = "",
    var addressState: String = "",
    var addressZip: String = "",
    var guardians: ArrayList<Guardian>?,
    var medications: ArrayList<Medication>?,
    var pediatrician: Pediatrician?,
    var billing: Billing?
) {

  constructor() : this("", "", "", "", "",
      "", "", "", "", "", "",
      "", null, null, null, null)
}

