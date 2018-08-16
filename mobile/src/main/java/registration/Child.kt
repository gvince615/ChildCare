package registration

data class Child(
    var childImageUri: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var birthDate: String = "",
    var isActive: String? = "",
    var enrollmentDate: String = "",
    var addressLn1: String = "",
    var addressLn2: String = "",
    var addressCity: String = "",
    var addressState: String = "",
    var addressZip: String = "") {

  constructor() : this("", "", "", "", "", "", "", "", "", "", "")
}

