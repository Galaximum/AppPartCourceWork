package ru.hse.project.ecoapp.ui.main.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import ru.hse.project.ecoapp.App
import ru.hse.project.ecoapp.R
import ru.hse.project.ecoapp.util.ImageUtils
import java.io.File
import java.io.FileNotFoundException


class DialogFragmentChoseEventSetImage(private val updateImage: () -> Unit) : DialogFragment() {

    companion object {
        private const val IMAGE_FROM_GALLERY = 1
        private const val IMAGE_FROM_CAMERA = 2
    }

    private val repository = App.getComponent().getRepository()
    private val awsClient = App.getComponent().getAWSClient()
    private lateinit var tempFileCamera: File

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(
            R.layout.dialog_fragment_chose_event_set_image,
            container,
            false
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        root.findViewById<ConstraintLayout>(R.id.btn_create_camera).setOnClickListener {
            val photoCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            tempFileCamera = ImageUtils.createTempFile(requireContext())
            photoCameraIntent.putExtra(
                MediaStore.EXTRA_OUTPUT,
                FileProvider.getUriForFile(
                    requireContext(),
                    "ru.hse.project.ecoapp.provider",
                    tempFileCamera
                )
            )
            startActivityForResult(photoCameraIntent, IMAGE_FROM_CAMERA)
        }

        root.findViewById<ConstraintLayout>(R.id.btn_import_gallary).setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, IMAGE_FROM_GALLERY)
        }

        root.findViewById<ConstraintLayout>(R.id.btn_cancel_set_image)
            .setOnClickListener {
                dismiss()
            }

        return root
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            IMAGE_FROM_GALLERY -> {
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        val imageUri: Uri = data?.data!!

                        awsClient.updateImage(repository.user!!.id, File(ImageUtils.getUrlFromUri(requireContext(),imageUri)))
                            .addOnSuccessListener {
                                //Очищаем кэш картинки
                                App.getComponent().getPicassoHttps()
                                    .invalidate(repository.user!!.urlImage)
                                //Обновляем картинку
                                updateImage.invoke()
                            }
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    }
                }
            }
            IMAGE_FROM_CAMERA -> {
                if (resultCode == Activity.RESULT_OK) {
                    try {

                        awsClient.updateImage(repository.user!!.id, tempFileCamera)
                            .addOnSuccessListener {
                                //Очищаем кэш картинки
                                App.getComponent().getPicassoHttps()
                                    .invalidate(repository.user!!.urlImage)
                                //Обновляем картинку
                                updateImage.invoke()
                            }
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    }
                }
            }
        }
        dismiss()
    }

}