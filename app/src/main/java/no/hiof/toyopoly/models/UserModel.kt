package no.hiof.toyopoly.models

data class UserModel(
    val firstName:String,
    val lastName:String,
    val birthday:String,
    val address:String,
    val email:String
){
    constructor(firstName: String, lastName: String, email: String)
            : this(firstName, lastName, "", "", email)
}
