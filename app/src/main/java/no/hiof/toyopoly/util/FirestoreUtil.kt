package no.hiof.toyopoly.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import no.hiof.toyopoly.model.ChatChannelModel

object FirestoreUtil {
    private val db : FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    val currentUserDocRef : DocumentReference
        get() = FirebaseFirestore.getInstance().document("Users/${FirebaseAuth.getInstance().currentUser?.uid
            ?:throw NullPointerException("UID is null.")}")
    private val chatChannelsCollectionRef : CollectionReference
        get() = FirebaseFirestore.getInstance().collection("ChatChannels")

    fun getOrCreateChatChannel(otherUserId: String, onComplete: (channelId: String) -> Unit){
        currentUserDocRef.collection("engagedChatChannels").document(otherUserId).get().addOnSuccessListener {
            if (it.exists()){
                onComplete(it["channelId"] as String)
                return@addOnSuccessListener
            }
            val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

            val newChannel = chatChannelsCollectionRef.document()
            newChannel.set(ChatChannelModel(mutableListOf(currentUserId,otherUserId)))

            currentUserDocRef
                .collection("engagedChats")
                .document(otherUserId)
                .set(mapOf("channelId" to newChannel.id))

            db.collection("Users").document(otherUserId)
                .collection("engagedChats")
                .document(currentUserId)
                .set(mapOf("channelId" to newChannel.id))

            onComplete(newChannel.id)
        }
    }
}