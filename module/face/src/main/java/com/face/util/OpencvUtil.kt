package com.face.util

import com.face.bean.FaceRectBean
import com.zzkj.structure.util.moshi.MoshiHelper

object OpencvUtil {

    private var faceRectBean = FaceRectBean()

    init {
        faceRectBean =
            MoshiHelper.adapter(FaceRectBean::class.java).fromJson(SPUtils.faceRect)
                ?: FaceRectBean()
    }

//    fun loadFace(mBitmap: Bitmap?): List<Bitmap> {
//        LogUtils.e(">>>>>>>>>faceRectBean:$faceRectBean")
//        val faceBitmaps = mutableListOf<Bitmap>()
//        if (mBitmap == null) {
//            return faceBitmaps
//        }
//        // 加载级联分类器
//        val faceDetector = CascadeClassifier("$opencvPath${faceRectBean.mode}.xml")
//        if (faceDetector.empty()) {
//            toast("Please restart the application")
//            return faceBitmaps
//        } else {
//            val image = Mat()
//            Utils.bitmapToMat(mBitmap, image)
//            // 检查图像是否成功加载
////            if (image.empty()) {
////                finish()
////                return
////            }
//            val faceDetections = MatOfRect()
//            faceDetector.detectMultiScale(
//                image,// 灰度图像
//                faceDetections,// 存储检测到的人脸
//                faceRectBean.scalefactor, // 缩放因子较小，确保精度
//                faceRectBean.minneighbors,// 增加邻居数量，减少误报
//                0,// 标志
//                Size(faceRectBean.minwh, faceRectBean.minwh)// 最小人脸尺寸
//            )
//            val expandedFaces = mutableListOf<Rect>()
//
//            // 扩展每个人脸框的范围
//            for (rect in faceDetections.toArray()) {
//                val expandedRect = expandFaceRect(
//                    rect,
//                    faceRectBean.rectsize,
//                    image.width(),
//                    image.height()
//                )
//                expandedFaces.add(expandedRect)
//            }
//            expandedFaces.toList().forEach {
//                val faceMat = Mat(image, it)
//                val faceBitmap = Bitmap.createBitmap(
//                    faceMat.cols(),
//                    faceMat.rows(),
//                    Bitmap.Config.ARGB_8888
//                )
//                Utils.matToBitmap(faceMat, faceBitmap)
//                faceBitmaps.add(faceBitmap)
//            }
//            return faceBitmaps
//        }
//    }
//
//    fun loadFace2(mBitmap: Bitmap?): List<Bitmap> {
//        val faceBitmaps = mutableListOf<Bitmap>()
//        if (mBitmap == null) {
//            return faceBitmaps
//        }
//        // 加载级联分类器
//        val faceDetector = CascadeClassifier("${opencvPath}haarcascade_frontalface_alt.xml")
//        if (faceDetector.empty()) {
//            toast("Please restart the application")
//            return faceBitmaps
//        } else {
//            val image = Mat()
//            Utils.bitmapToMat(mBitmap, image)
//            //检查图像是否成功加载
////            if (image.empty()) {
////                finish()
////                return
////            }
//            val faceDetections = MatOfRect()
//            faceDetector.detectMultiScale(
//                image,// 灰度图像
//                faceDetections,// 存储检测到的人脸
//                1.1, // 缩放因子较小，确保精度
//                3,// 增加邻居数量，减少误报
//                0,// 标志
//                Size(20.0, 20.0),// 最小人脸尺寸
//                Size()
//            )
//            val expandedFaces = mutableListOf<Rect>()
//
//            // 扩展每个人脸框的范围
//            for (rect in faceDetections.toArray()) {
//                val expandedRect = expandFaceRect(
//                    rect,
//                    faceRectBean.rectsize,
//                    image.width(),
//                    image.height()
//                )
//                expandedFaces.add(expandedRect)
//            }
//            expandedFaces.toList().forEach {
//                val faceMat = Mat(image, it)
//                val faceBitmap = Bitmap.createBitmap(
//                    faceMat.cols(),
//                    faceMat.rows(),
//                    Bitmap.Config.ARGB_8888
//                )
//                Utils.matToBitmap(faceMat, faceBitmap)
//                faceBitmaps.add(faceBitmap)
//            }
//            return faceBitmaps
//        }
//    }
//
//
//    fun expandFaceRect(rect: Rect, scaleFactor: Double, imgW: Int, imgH: Int): Rect {
//        val width = rect.width
//        val height = rect.height
//        // 扩展宽度和高度，scaleFactor 可以是 1.2（扩大 20%）或其他值
//        val newWidth = (width * scaleFactor).toInt()
//        val newHeight = (height * scaleFactor).toInt()
//        // 计算扩展后的坐标（左上角位置）
//        val newX = 0.coerceAtLeast(rect.x - (newWidth - width) / 2)
//        val newY = 0.coerceAtLeast(rect.y - (newHeight - height) / 2)
//        // 创建一个新的矩形框，包含扩展后的区域
//        return if (imgW < (newX + newWidth) || imgH < (newY + newHeight)) {//原图不够大
//            rect
//        } else {
//            Rect(newX, newY, newWidth, newHeight)
//        }
//    }

}