package registration

class Parent(

    var firstName: String = "",
    var lastName: String = "",
    var addressLn1: String = "",
    var addressLn2: String = "",
    var addressCity: String = "",
    var addressState: String = "",
    var addressZip: String = "",
    var phoneNumber1: String = "",
    var phoneNumber2: String = "") {

  constructor() : this("", "", "", "", "", "", "", "", "")
}
