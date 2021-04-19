package app.fyp

enum class State {
    LOADING, SUCCESS, ERROR
}

class Resource<T>(val state: State = State.LOADING, val message: String? = "", val data: T?) {
    companion object {
        fun<T> success(data: T?, message: String = ""): Resource<T> {
            return Resource(State.SUCCESS, message, data)
        }

        fun<T> error(message: String?, e: T? = null): Resource<T> {
            return Resource(State.ERROR, message, e)
        }

        fun<T> loading(data: T? = null): Resource<T> {
            return Resource(State.LOADING, "", data)
        }
    }
}

class Useful {
    companion object {
        fun cleanEmail(email: String): String {
            val newMail = email.replace("@", "")
            return newMail.replace(".", "")
        }
    }
}