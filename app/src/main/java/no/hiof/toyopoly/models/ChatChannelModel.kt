package no.hiof.toyopoly.models

import androidx.annotation.Keep

@Keep
data class ChatChannelModel(
    val chatChannelId : String,
    val userIds : MutableList<String>
){
    constructor() : this(chatChannelId= "",userIds = mutableListOf())
}
