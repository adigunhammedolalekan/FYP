package app.fyp.models

import org.parceler.Parcel

@Parcel
data class Conversation(
    val toId: String = "",
    val fromId: String = "",
    val toName: String = "",
    val fromName: String = "",
    val lastMessage: String = ""
) {

    fun name(currentUserId: String): String {
        if (fromId == currentUserId)
            return toName
        if (toId == currentUserId)
            return fromName
        return ""
    }

    companion object {
        const val KEY = "conversationKey"
    }
}

data class Message(
    val text: String = "",
    val fromId: String = "",
    val toId: String = "",
    val ts: Long = System.currentTimeMillis()
)