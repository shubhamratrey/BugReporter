package com.sillylife.bugreporter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sillylife.bugreporter.screenshot.OnScreenshotTakenListener
import com.sillylife.bugreporter.screenshot.ScreenshotObserver
import com.sillylife.bugreporter.shakedetector.Screenshotter
import com.sillylife.bugreporter.shakedetector.ShakeDetector
import com.sillylife.bugreporter.utils.FileAttachment
import com.sillylife.bugreporter.utils.Log
import com.sillylife.bugreporter.utils.MimeTypes
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream


class MainActivity : AppCompatActivity() {

    private var mScreenshotListener: OnScreenshotTakenListener? = null
    private var mScreenshotObserver: ScreenshotObserver? = null

    private var mSensorManager: SensorManager? = null
    private var mShakeDetector: ShakeDetector? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpScreenshotObserver()
        setUpShakeDetector()
    }

    fun setUpScreenshotObserver() {
        if (mScreenshotListener == null) {
            mScreenshotListener = OnScreenshotTakenListener {
                if (it.exists()) {
                    val myBitmap = BitmapFactory.decodeFile(it.absolutePath)
                    photos?.setImageBitmap(myBitmap)
                }
            }
        }
        mScreenshotObserver = ScreenshotObserver.Factory.newInstance(this, mScreenshotListener)

        // Try to start the screenshot observer. However, if permission is denied by the user,
        // then disable screenshot invocations. This way, the user doesn't repeatedly get prompted to
        // grant permissions, but screenshot invocations can still be re-enabled programattically in the same session.
        mScreenshotObserver?.start(this, ScreenshotObserver.ScreenshotObserverPermissionListener {
            setUpScreenshotObserver()
        })

        Log.d("Starting screenshot invocation method!")
    }

    fun setUpShakeDetector() {
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager?
        if (mSensorManager == null) {
            throw RuntimeException("Unable to obtain SensorManager!")
        }

        val accelerometer = mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mShakeDetector = ShakeDetector(object : ShakeDetector.OnShakeListener {
            override fun onShake() {
                Log.d("Shake detected")
                Toast.makeText(applicationContext,"SHake Detected", Toast.LENGTH_SHORT).show()
                val attachment = captureScreenshot()
                if (attachment != null && attachment.file.exists()) {
                    attachment.file
                    val myBitmap = BitmapFactory.decodeFile(attachment.file.absolutePath)
                    photos?.setImageBitmap(myBitmap)
                }
            }
        })

        val registered = mSensorManager?.registerListener(mShakeDetector, accelerometer, SensorManager.SENSOR_DELAY_UI)
        if (registered!!) {
            Log.d("Starting shake invocation method!")
        } else {
            Log.e("Unable to register shake listener")
        }
    }

    fun captureScreenshot(): FileAttachment? {
        val bitmap = Screenshotter(this).bitmap
        val file = generateScreenshotFile()
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, FileOutputStream(file))
            return FileAttachment(file, MimeTypes.PNG)
        } catch (e: FileNotFoundException) {
            Log.e("Error saving screenshot!", e)
//            Toast.makeText(mAppContext, R.string.error_save_screenshot, Toast.LENGTH_LONG).show()
        }

        return null
    }

    private fun generateScreenshotFile(): File {
        val filename = "screenshot_" + System.currentTimeMillis() + ".png"
        return File(this.cacheDir, filename)
    }

}
