package no.hiof.toyopoly.models

data class UserModel(
    val firstName:String,
    val lastName:String,
    val birthday:String,
    val address:String,
    val email:String,
    val imageUri : String = "/images/users/default.png",
){
    constructor(firstName: String, lastName: String, email: String)
            : this(firstName, lastName, "", "" , email, "/images/users/default.png")
}
