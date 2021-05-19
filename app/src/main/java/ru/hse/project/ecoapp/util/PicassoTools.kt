package ru.hse.project.ecoapp.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.ByteArrayOutputStream
import java.security.KeyStore
import java.util.*
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager


class PicassoTools {
    companion object {
        fun getHttpsPicasso(context: Context): Picasso {
            val trustManagerFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            trustManagerFactory.init(null as KeyStore?)
            val trustManagers = trustManagerFactory.trustManagers
            check(!(trustManagers.size != 1 || trustManagers[0] !is X509TrustManager)) {
                "Unexpected default trust managers:" + Arrays.toString(trustManagers)
            }

            val interceptor = Interceptor {
                val response = it.proceed(it.request())
                val body = response.body
                if (body?.contentType()?.type == "image") {
                    val bytes = body.bytes()
                    val degrees = bytes.inputStream().use { input ->
                        when (ExifInterface(input).getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_NORMAL
                        )) {
                            ExifInterface.ORIENTATION_ROTATE_270 -> 270
                            ExifInterface.ORIENTATION_ROTATE_180 -> 180
                            ExifInterface.ORIENTATION_ROTATE_90 -> 90
                            else -> 0
                        }
                    }
                    if (degrees != 0) {
                        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        ByteArrayOutputStream().use { output ->
                            Bitmap.createBitmap(
                                bitmap,
                                0,
                                0,
                                bitmap.width,
                                bitmap.height,
                                Matrix().apply { postRotate(degrees.toFloat()) },
                                true
                            )
                                .compress(Bitmap.CompressFormat.PNG, 100, output)
                            response.newBuilder()
                                .body(output.toByteArray().toResponseBody(body.contentType()))
                                .build()
                        }
                    } else
                        response.newBuilder().body(bytes.toResponseBody(body.contentType())).build()
                } else
                    response
            }

            val trustManager = trustManagers[0] as X509TrustManager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, arrayOf<TrustManager>(trustManager), null)

            val client = OkHttpClient().newBuilder()
                .hostnameVerifier { _, _ -> true }
                .sslSocketFactory(sslContext.socketFactory, trustManager)
                .addNetworkInterceptor(interceptor)
                .build()


            return Picasso.Builder(context)
                .downloader(OkHttp3Downloader(client))
                .listener { _, _, exception -> Log.e("PICASSO", exception.message!!) }
                .build()
        }


    }
}