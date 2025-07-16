package com.example.pengingatsholat.ui

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.*
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.fragment.app.Fragment
import com.example.pengingatsholat.databinding.FragmentQiblaBinding
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class QiblaFragment : Fragment(), SensorEventListener {

    private lateinit var binding: FragmentQiblaBinding
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var magnetometer: Sensor? = null

    private var gravity = FloatArray(3)
    private var geomagnetic = FloatArray(3)
    private var currentDegree = 0f

    // Koordinat Ka'bah (Mekkah)
    private val latKaabah = 21.4225
    private val lonKaabah = 39.8262

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentQiblaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> gravity = event.values.clone()
            Sensor.TYPE_MAGNETIC_FIELD -> geomagnetic = event.values.clone()
        }

        val R = FloatArray(9)
        val I = FloatArray(9)

        val success = SensorManager.getRotationMatrix(R, I, gravity, geomagnetic)
        if (success) {
            val orientation = FloatArray(3)
            SensorManager.getOrientation(R, orientation)

            val azimuthInRadians = orientation[0]
            val azimuthInDegrees = Math.toDegrees(azimuthInRadians.toDouble()).toFloat()

            val bearingToKaabah = calculateQiblaDirection()
            val direction = (azimuthInDegrees - bearingToKaabah + 360) % 360

            // Rotate compass image
            val rotate = RotateAnimation(
                currentDegree,
                -direction,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
            )
            rotate.duration = 500
            rotate.fillAfter = true

            binding.compassImage.startAnimation(rotate)
            currentDegree = -direction

            // Tampilkan derajat
            val degreeDisplay = "Arah Kiblat: %.0f°".format(direction)
            binding.tvDerajat.text = degreeDisplay
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun calculateQiblaDirection(): Float {
        val userLocation = getLastKnownLocation() ?: return 0f
        val lat1 = Math.toRadians(userLocation.first)
        val lon1 = Math.toRadians(userLocation.second)
        val lat2 = Math.toRadians(latKaabah)
        val lon2 = Math.toRadians(lonKaabah)

        val dLon = lon2 - lon1
        val y = sin(dLon) * cos(lat2)
        val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLon)
        val bearing = atan2(y, x)
        return Math.toDegrees(bearing).toFloat().let { (it + 360) % 360 }
    }

    private fun getLastKnownLocation(): Pair<Double, Double>? {
        // Nilai default fallback Jakarta jika lokasi tidak tersedia
        return Pair(-6.200000, 106.816666)
    }
}
