package registration

data class Child(

    var firstName: String = "",
    var lastName: String = "",
    var birthDate: String = "",
    var enrollmentDate: String = "",
    var addressLn1: String = "",
    var addressLn2: String = "",
    var addressCity: String = "",
    var addressState: String = "",
    var addressZip: String = "") {

  constructor() : this("", "", "", "", "", "", "", "", "")
}

