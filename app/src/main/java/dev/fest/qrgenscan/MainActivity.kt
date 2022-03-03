package dev.fest.qrgenscan

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.WriterException
import com.squareup.picasso.Picasso
import dev.fest.qrgenscan.ConstQR.DIMEN
import dev.fest.qrgenscan.ConstQR.KEY
import dev.fest.qrgenscan.ConstQR.REQUEST_CODE
import dev.fest.qrgenscan.databinding.ActivityMainBinding
import io.github.ponnamkarthik.richlinkpreview.MetaData
import io.github.ponnamkarthik.richlinkpreview.ResponseListener
import io.github.ponnamkarthik.richlinkpreview.RichPreview

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getTextFromQrCode()
        binding.buttonGen.setOnClickListener {
            generateCode(binding.editTextForQR.text.toString())
        }
        binding.buttonScan.setOnClickListener {
            checkCameraPermission()
        }
    }

    private fun generateCode(text: String) {
        val qrGenerator = QRGEncoder(text, null, QRGContents.Type.TEXT, DIMEN)
        try {
            val bMap = qrGenerator.encodeAsBitmap()
            binding.imageView.setImageBitmap(bMap)
            if (bMap != null)
                binding.apply {
                    cardView.visibility = View.VISIBLE
                    cardViewMetadata.visibility = View.GONE
                    textViewQRCode.visibility = View.GONE
                }
        } catch (e: WriterException) {
            Log.d("MainAct", "WriterException: $e")
        }
    }

    private fun getTextFromQrCode() = with(binding) {
        val argument = intent.extras
        val qrCode = argument?.get(KEY).toString()
        if (qrCode != "null") {
            setMetadataFromUrlToView(qrCode)
        }
    }

    private fun setMetadataFromUrlToView(qrCode: String) = with(binding) {
        if (qrCode.contains("http")) {
            Log.d("MainAct", "$qrCode")
            val richPreview = RichPreview(object : ResponseListener {
                override fun onData(metaData: MetaData) {
                    Log.d("MainAct", "data: ${metaData}")
                    Log.d("MainAct", "title: ${metaData.title}")
                    Log.d("MainAct", "description: ${metaData.description}")
                    Log.d("MainAct", "mediatype: ${metaData.mediatype}")
                    Log.d("MainAct", "favicon: ${metaData.favicon}")
                    Log.d("MainAct", "imageurl: ${metaData.imageurl}")
                    Log.d("MainAct", "sitename: ${metaData.sitename}")
                    Log.d("MainAct", "url: ${metaData.url}")
                    cardViewMetadata.visibility = View.VISIBLE
                    if (metaData.url != null) textViewUrlMetadata.text = metaData.url
                    if (metaData.favicon != null) Picasso.get().load(metaData.favicon)
                        .into(imageViewFavicon)
                    if (metaData.title != null) textViewTitleMetadata.text = metaData.title
                    if (metaData.description != null) textViewDescriptionMetadata.text =
                        metaData.description
                    if (metaData.imageurl != null) Picasso.get().load(metaData.imageurl)
                        .into(imageViewImageURL)
                }

                override fun onError(e: Exception) {
                    Log.d("MainAct", "Exception: ${e.message}")
                    cardViewMetadata.visibility = View.GONE
                    textViewQRCode.text = qrCode
                }
            })
            richPreview.getPreview(qrCode)
        } else {
            cardViewMetadata.visibility = View.GONE
            textViewQRCode.text = qrCode
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CODE)
        } else {
            startActivity(Intent(this, ScannerActivity::class.java))
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(Intent(this, ScannerActivity::class.java))
            }
        }
    }
}