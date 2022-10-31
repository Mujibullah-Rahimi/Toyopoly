
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