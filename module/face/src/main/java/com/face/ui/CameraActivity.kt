package com.face.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture
import com.face.BR
import com.face.R
import com.face.databinding.ActivityCameraBinding
import com.face.key.Constants
import com.face.util.GVM
import com.face.viewmodel.activity.CameraViewModel
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ImgLoader.loadImage
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.toast
import java.io.File
import java.util.concurrent.ExecutionException


class CameraActivity : BaseBindingActivity<ActivityCameraBinding, CameraViewModel>(
    R.layout.activity_camera, CameraViewModel::class.java
) {

    private var mImageCapture: ImageCapture? = null
    private lateinit var launcherPermission: ActivityResultLauncher<String>


    //摄像头
    private var mCamera: Camera? = null

    //前后摄像头
    private var isBackCamera: Boolean = false

    val file = File(Constants.savePath, Constants.saveImg)
    override fun init(savedInstanceState: Bundle?) {
//        bar {
//            transparent()
//            lightStatusBar(false)
//            showNavigationBar(false)
//            setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE)
//        }

        //单个权限申请
        launcherPermission =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                if (it) {//同意
                    startCamera()
                } else {//拒绝
                    val intent = Intent()
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.setData(Uri.parse("package:" + this.packageName))
                    startActivity(intent)
                    toast(getString(R.string.permissions_camera))
                    finish()
                }
            }

        val packageManager = packageManager
        val hasCamera = packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)
        if (!hasCamera) {
            // 相机不存在，进行相应处理
            toast(getString(R.string.permissions_camera))
            finish()
        }else{
            checkPermission()
        }

    }

    /**
     * 开始预览
     */
    private fun startCamera() {
        val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> =
            ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            try {
                //将相机的生命周期和activity的生命周期绑定，camerax 会自己释放
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build()
                //创建图片的 capture
                mImageCapture =
                    ImageCapture.Builder().setFlashMode(ImageCapture.FLASH_MODE_OFF).build()
                //选择摄像头
                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(if (isBackCamera) CameraSelector.LENS_FACING_BACK else CameraSelector.LENS_FACING_FRONT)
                    .build()
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                //参数中如果有mImageCapture才能拍照，否则会报下错
                //Not bound to a valid Camera [ImageCapture:androidx.camera.core.ImageCapture-bce6e930-b637-40ee-b9b9-
                mCamera = cameraProvider.bindToLifecycle(
                    this@CameraActivity, cameraSelector, preview, mImageCapture
                )
                preview.setSurfaceProvider(mBinding?.mPreviewView?.getSurfaceProvider())
            } catch (e: ExecutionException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }


    /**
     * 确认拍照
     */
    fun okPhoto() {
//        launch {
//            if (file.path.startsWith("content:", true)) {//高版本手机保存图片
//                FileUtil.copyUriFile(Uri.parse(file.path))
//            } else {//低版本手机保存图片
//                val mBitmap: Bitmap? = FileUtil.getDiskBitmap(file.path)
//                FileUtil.saveImgToDisk(Constants.saveImg, mBitmap)
//            }
//        }
        openActivity<UploadFaceActivity> {
            putString("img_url",file.path)
        }
        finish()
    }

    /**
     * 再次拍照
     */
    fun againPhoto() {
        mModel.showPhoto.value = false
    }


    /**
     * 旋转相机
     */
    fun rotatePhoto() {
        isBackCamera = !isBackCamera
        startCamera()
    }

    /**
     * 拍照并存到存储空间
     */
    fun takeFilePhoto() {
        if (mImageCapture != null) {
            val dir = File(Constants.savePath)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            //文件存在就移除
            if (file.exists()) {
                file.delete()
            }


            //前置摄像需要左右翻转图片
            val metadata = ImageCapture.Metadata()
            metadata.isReversedHorizontal = !isBackCamera

            //创建包文件的数据，比如创建文件
            val outputFileOptions =
                ImageCapture.OutputFileOptions.Builder(file).setMetadata(metadata).build()


            //开始拍照
            mImageCapture!!.takePicture(outputFileOptions,
                ContextCompat.getMainExecutor(this),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        mModel.showPhoto.value = true
                        mBinding?.photoImg?.loadImage(
                            file.path, placeholderResId = com.key.R.drawable.img_default_m
                        )
                    }

                    override fun onError(exception: ImageCaptureException) {
                        toast("Failed:${exception.message}")
                    }
                })
        }
    }

    /**
     * 检查是否拥有权限
     */
    private fun checkPermission() {
        val cameraPermission: Boolean =
            (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
                this@CameraActivity, Manifest.permission.CAMERA
            ))
        if (cameraPermission) {
            startCamera()
        } else {
            launcherPermission.launch(Manifest.permission.CAMERA)
        }
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this).addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}