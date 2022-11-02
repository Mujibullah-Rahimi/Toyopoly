package no.hiof.toyopoly.models

import java.util.Date

data class PhotoModel(
    var localUri: String = "",
    var remoteUri: String = "",
    var description: String = "",
    var dateTaken: Date = Date(),
    var id: String = ""
)

//TODO: possibility for use of camera
//    private fun invokeCamera() {
//        val file = createImageFile()
//        try {
//            uri = this.activity?.let { FileProvider.getUriForFile(it, "no.hiof.toyopoly", file) }
//        }catch (e:java.lang.Exception){
//            Log.e(TAG, "ERROR: ${e.message}")
//            var foo = e.message
//        }
//        getCameraImage.launch(uri)
//    }
//    private val getCameraImage = registerForActivityResult(ActivityResultContracts.TakePicture()){
//        success ->
//        if(success){
//            Log.i(TAG, "Img loc: ${uri}")
//        }else{
//            Log.e(TAG,"img no bueno. ${uri}")
//        }
//    }
//    private fun createImageFile() : File {
//        val timestamp = SimpleDateFormat("HHmmss_ddMMyyyy").format(Date())
//        val imageDirectory = this.requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//        return File.createTempFile(
//            "Value_${timestamp}",
//        ".jpg",
//            imageDirectory
//        ).apply {
//            currentImagePath = absolutePath
//        }
//    }
//    private fun uploadPhotos(){
//        photos.forEach{
//            photo ->
//            var uri = Uri.parse(photo.localUri)
//            var imageRef = storageReference.child("images/${user?.uid}/${uri.lastPathSegment}")
//            val uploadTask = imageRef.putFile(uri)
//            uploadTask.addOnSuccessListener {
//                Log.i(TAG, "Image uploaded $imageRef")
//                val downloadUrl = imageRef.downloadUrl
//                downloadUrl.addOnSuccessListener {
//                    remoteUri ->
//                    photo.remoteUri = remoteUri.toString()
//                    updatePhotoDatabase(photo)
//                }
//            }
//            uploadTask.addOnFailureListener{
//                Log.e(TAG, it.message ?: "no message")
//            }
//        }
//    }
//    private fun updatePhotoDatabase(photo: PhotoModel) {
//        var photoCollection = db.collection("Ads").document(ads.adId).collection("Images")
//        var handle = photoCollection.add(photo)
//        handle.addOnSuccessListener {
//            Log.i(TAG, "Successfully updated photo metadata")
//            photo.id = it.id
//            db.collection("Ads").document(ads.adId).collection("Images").document(photo.id).set(photo)
//        }
//        handle.addOnFailureListener{
//            Log.e(TAG, "Error updating photo data: ${it.message}")
//        }
//    }