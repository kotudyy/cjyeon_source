package com.example.chap16

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.example.chap16.databinding.ActivityMainBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var filePath: String

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.galleryButton.setOnClickListener {
            //gallery app........................
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            getGallery.launch(intent)
        }

        binding.cameraButton.setOnClickListener {
            //camera app......................
            //파일 준비...............
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES) //해당 앱에서만 이용할 수 있는 파일 생성
            val file = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
            // 'JPEG_${timeStamp}_'로 시작하 '.jpg'로 끝나는 이름을 가진 임시파일을 storageDir 경로에 저장

            filePath = file.absolutePath    //파일 절대경로

            val photoURI: Uri = FileProvider.getUriForFile(
                this, "com.example.chap16.fileprovider", file)

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            getCamera.launch(intent)
        }
    }

    //갤러리 인텐트
    @RequiresApi(Build.VERSION_CODES.N)
    private val getGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            try {
                //inSampleSize 비율 계산, 지정
                val calRatio = calculateInSampleSize(
                    it.data!!.data!!,
                    resources.getDimensionPixelSize(R.dimen.imgSize),
                    resources.getDimensionPixelSize(R.dimen.imgSize)
                )
                val option = BitmapFactory.Options()
                option.inSampleSize = calRatio

                //이미지 로딩
                var inputStream = contentResolver.openInputStream(it.data!!.data!!)
                var bitmap = BitmapFactory.decodeStream(inputStream, null, option)
                inputStream!!.close()
                inputStream = null

//                //meta data 저장하는 Exif
//                val exif = ExifInterface(inputStream)
//                //TAG_ORIENTATION -> 사진이 회전된 각도
//                val exifOrientation: Int = exif.getAttributeInt(
//                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
//                Log.d("degree", exifOrientation.toString())
//                val exifDegree = exifOrientationToDegrees(exifOrientation)
//                Log.d("degree", exifDegree.toString())
//                bitmap = bitmap?.let { it1 -> rotate(it1, exifDegree) }

                bitmap?.let {
                    binding.userImageView.setImageBitmap(bitmap)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //카메라 인텐트
    private val getCamera = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            //inSampleSize 비율 계산
            val calRatio = calculateInSampleSize(
                Uri.fromFile(File(filePath)),
                resources.getDimensionPixelSize(R.dimen.imgSize),
                resources.getDimensionPixelSize(R.dimen.imgSize)
            )
            val option = BitmapFactory.Options()
            option.inSampleSize = calRatio
            var bitmap = BitmapFactory.decodeFile(filePath, option)

            //meta data 저장하는 Exif
            val exif= filePath?.let { ExifInterface(filePath) }
            val exifOrientation: Int = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            val exifDegree = exifOrientationToDegrees(exifOrientation)
            bitmap = rotate(bitmap, exifDegree)

            bitmap?.let {
                binding.userImageView.setImageBitmap(bitmap)
            }
        }
    }

    private fun calculateInSampleSize(fileUri: Uri, reqWidth: Int, reqHeight: Int): Int {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true   //옵션만 설정하기 위해 true로 지정
        try {
            var inputStream = contentResolver.openInputStream(fileUri)

            //inJustDecodeBounds 값을 true 로 설정한 상태에서 decodeXXX() 를 호출.
            //로딩 하고자 하는 이미지의 각종 정보가 options 에 설정 된다.
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream!!.close()
            inputStream = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
        //비율 계산........................
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1
        //inSampleSize 비율 계산
        if (height > reqHeight || width > reqWidth) {

            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    //사진 각도 반환 함수
    fun exifOrientationToDegrees(exifOrientation: Int): Int {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270
        }
        return 0
    }

    // 이미지 회전
    private fun rotate(bitmap: Bitmap, degree: Int) : Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix,true)
    }
}
