package no.hiof.toyopoly.models

import androidx.annotation.Keep

@Keep
data class ChatChannelModel(
    val userIds : MutableList<String>
){
    constructor() : this(userIds = mutableListOf())
}
