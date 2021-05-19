package ru.hse.project.ecoapp.data

import android.content.Context
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import id.zelory.compressor.Compressor
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.concurrent.thread


class AWSClient(private val context: Context) {
    private val s3: AmazonS3Client

    init {
        val credentials = BasicAWSCredentials(ACCESS_KEY, SECRET_KEY)
        s3 = AmazonS3Client(credentials)
        s3.setRegion(Region.getRegion(Regions.US_EAST_1))
        s3.endpoint = "https://storage.yandexcloud.net/"
    }

    companion object {
        private const val ACCESS_KEY = "cELDWOyeFvxfxc86rR9H"
        private const val SECRET_KEY = "RR9YAk25BgnEO830PQdOWkNWt9ZQTI3raKm5v3Rj"
        private const val MY_BUCKET = "imagesecoapp/user_images"
    }

    private fun uploadImage(key: String, file: File): Task<Void> {
        val task = TaskCompletionSource<Void>()
        //Запускаем программу в отдельном потоке
        thread {
            // Показываем что участок кода может быть с прерываниями
            runBlocking {
                val transferUtility = TransferUtility.builder()
                    .s3Client(s3)
                    .context(context)
                    .build()

                val compressFile =Compressor.compress(context, file);

                val observer =
                    transferUtility.upload(
                        MY_BUCKET,
                        key,
                        compressFile,
                        CannedAccessControlList.BucketOwnerFullControl
                    )
                observer.setTransferListener(object : TransferListener {
                    override fun onStateChanged(id: Int, state: TransferState) {
                        if (state == TransferState.COMPLETED) {
                            task.setResult(null)
                        }
                    }
                    override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {}

                    override fun onError(id: Int, ex: Exception) {
                        task.setException(ex)
                    }
                })
            }
        }
        return task.task
    }


    fun updateImage(id: String, file: File): Task<Void> {
        val key = "$id.jpg"
        return uploadImage(key, file)
    }

}