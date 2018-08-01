package registration

class Parent(

    var firstName: String = "",
    var lastName: String = "",
    var emailAddress: String = "",
    var phoneNumber1: String = "",
    var phoneNumber2: String = "") {

  constructor() : this("", "", "","", "")
}
