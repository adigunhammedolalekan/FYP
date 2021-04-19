package app.fyp.models

class User(
    val userId: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val timeJoined: Long = System.currentTimeMillis()
) {

    fun name() = "$firstName $lastName"
}