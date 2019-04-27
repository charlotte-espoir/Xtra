package com.github.exact7.xtra.model

import android.content.Context
import androidx.core.content.edit
import com.github.exact7.xtra.util.C
import com.github.exact7.xtra.util.TwitchApiHelper
import com.github.exact7.xtra.util.prefs

sealed class User(val id: String,
                  val name: String,
                  val token: String) {

    companion object {
        fun get(context: Context): User {
            return with(context.prefs()) {
                val id = getString(C.USER_ID, null)
                if (id != null) {
                    val name = getString(C.USERNAME, null)!!
                    val token = getString(C.TOKEN, null)!!
                    if (TwitchApiHelper.validated) {
                        LoggedIn(id, name, token)
                    } else {
                        NotValidated(id, name, token)
                    }
                } else {
                    NotLoggedIn()
                }
            }
        }

        fun set(context: Context, user: User?) {
            context.prefs().edit {
                putString(C.USER_ID, user?.id)
                putString(C.USERNAME, user?.name)
                putString(C.TOKEN, user?.token)
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (id != other.id) return false
        if (name != other.name) return false
        if (token != other.token) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + token.hashCode()
        return result
    }
}

class LoggedIn(id: String, name: String, token: String) : User(id, name, token) {
    constructor(user: NotValidated) : this(user.id, user.name, user.token)
}
class NotValidated(id: String, name: String, token: String) : User(id, name, token)
class NotLoggedIn : User("", "", "")
