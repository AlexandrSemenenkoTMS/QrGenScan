package dev.fest.qrgenscan

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import dev.fest.qrgenscan.ConstQR.KEY
import me.dm7.barcodescanner.zbar.Result
import me.dm7.barcodescanner.zbar.ZBarScannerView

class ScannerActivity : AppCompatActivity(), ZBarScannerView.ResultHandler {

    private lateinit var zBarScannerView: ZBarScannerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        zBarScannerView = ZBarScannerView(this)
        setContentView(zBarScannerView)
    }

    override fun onResume() {
        super.onResume()
        zBarScannerView.setResultHandler(this)
        zBarScannerView.startCamera()
    }

    override fun onPause() {
        super.onPause()
        zBarScannerView.stopCamera()
    }

    override fun handleResult(result: Result?) {
        Log.d("ScannerAct", "Result: ${result?.contents}")
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(KEY, result?.contents)
        startActivity(intent)
        finish()
    }
}