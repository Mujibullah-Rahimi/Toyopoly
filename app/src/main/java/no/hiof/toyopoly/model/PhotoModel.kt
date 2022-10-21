package no.hiof.toyopoly.model

import java.util.Date

data class PhotoModel(
    var localUri: String = "",
    var remoteUri: String = "",
    var description: String = "",
    var dateTaken: Date = Date(),
    var id: String = ""
)
