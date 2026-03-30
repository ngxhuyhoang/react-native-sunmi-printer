package com.sunmiprinter

import android.graphics.BitmapFactory
import android.util.Base64
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.sunmi.peripheral.printer.InnerLcdCallback
import com.sunmi.peripheral.printer.InnerPrinterCallback
import com.sunmi.peripheral.printer.InnerPrinterManager
import com.sunmi.peripheral.printer.InnerResultCallback
import com.sunmi.peripheral.printer.SunmiPrinterService

class SunmiPrinterModule(reactContext: ReactApplicationContext) :
  NativeSunmiPrinterSpec(reactContext) {

  private var printerService: SunmiPrinterService? = null

  private val printerCallback = object : InnerPrinterCallback() {
    override fun onConnected(service: SunmiPrinterService) {
      printerService = service
    }

    override fun onDisconnected() {
      printerService = null
    }
  }

  init {
    InnerPrinterManager.getInstance().bindService(reactContext, printerCallback)
  }

  private fun getService(promise: Promise): SunmiPrinterService? {
    return printerService ?: run {
      promise.reject("SERVICE_NOT_CONNECTED", "Printer service is not connected")
      null
    }
  }

  private fun resultCallback(promise: Promise) = object : InnerResultCallback() {
    override fun onRunResult(isSuccess: Boolean) {
      if (isSuccess) promise.resolve(null)
      else promise.reject("PRINTER_ERROR", "Operation failed")
    }

    override fun onReturnString(result: String?) {}

    override fun onRaiseException(code: Int, msg: String?) {
      promise.reject("PRINTER_EXCEPTION", msg ?: "Printer exception (code: $code)")
    }

    override fun onPrintResult(code: Int, msg: String?) {}
  }

  private fun stringResultCallback(promise: Promise) = object : InnerResultCallback() {
    override fun onRunResult(isSuccess: Boolean) {}

    override fun onReturnString(result: String?) {
      promise.resolve(result)
    }

    override fun onRaiseException(code: Int, msg: String?) {
      promise.reject("PRINTER_EXCEPTION", msg ?: "Printer exception (code: $code)")
    }

    override fun onPrintResult(code: Int, msg: String?) {}
  }

  private fun lcdCallback(promise: Promise) = object : InnerLcdCallback() {
    override fun onRunResult(show: Boolean) {
      promise.resolve(null)
    }
  }

  private fun decodeBitmap(base64: String, promise: Promise): android.graphics.Bitmap? {
    val bytes = Base64.decode(base64, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: run {
      promise.reject("DECODE_ERROR", "Failed to decode base64 image")
      null
    }
  }

  // region Printer Info

  override fun getPrinterSerialNo(promise: Promise) {
    val service = getService(promise) ?: return
    try {
      promise.resolve(service.printerSerialNo)
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun getPrinterVersion(promise: Promise) {
    val service = getService(promise) ?: return
    try {
      promise.resolve(service.printerVersion)
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun getPrinterModal(promise: Promise) {
    val service = getService(promise) ?: return
    try {
      promise.resolve(service.printerModal)
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun getPrinterPaper(promise: Promise) {
    val service = getService(promise) ?: return
    try {
      promise.resolve(service.printerPaper)
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun getPrinterMode(promise: Promise) {
    val service = getService(promise) ?: return
    try {
      promise.resolve(service.printerMode)
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun getServiceVersion(promise: Promise) {
    val service = getService(promise) ?: return
    try {
      promise.resolve(service.serviceVersion)
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun getFirmwareStatus(promise: Promise) {
    val service = getService(promise) ?: return
    try {
      promise.resolve(service.firmwareStatus)
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun updatePrinterState(promise: Promise) {
    val service = getService(promise) ?: return
    try {
      promise.resolve(service.updatePrinterState())
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun getPrintedLength(promise: Promise) {
    val service = getService(promise) ?: return
    try {
      service.getPrintedLength(stringResultCallback(promise))
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun getPrinterFactory(promise: Promise) {
    val service = getService(promise) ?: return
    try {
      service.getPrinterFactory(stringResultCallback(promise))
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  // endregion

  // region Initialization

  override fun printerInit(promise: Promise) {
    val service = getService(promise) ?: return
    try {
      service.printerInit(resultCallback(promise))
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun printerSelfChecking(promise: Promise) {
    val service = getService(promise) ?: return
    try {
      service.printerSelfChecking(resultCallback(promise))
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  // endregion

  // region Formatting

  override fun setAlignment(alignment: Double, promise: Promise) {
    val service = getService(promise) ?: return
    try {
      service.setAlignment(alignment.toInt(), resultCallback(promise))
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun setFontName(typeface: String, promise: Promise) {
    val service = getService(promise) ?: return
    try {
      service.setFontName(typeface, resultCallback(promise))
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun setFontSize(fontsize: Double, promise: Promise) {
    val service = getService(promise) ?: return
    try {
      service.setFontSize(fontsize.toFloat(), resultCallback(promise))
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun setPrinterStyle(key: Double, value: Double, promise: Promise) {
    val service = getService(promise) ?: return
    try {
      service.setPrinterStyle(key.toInt(), value.toInt())
      promise.resolve(null)
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  // endregion

  // region Text

  override fun printText(text: String, promise: Promise) {
    val service = getService(promise) ?: return
    try {
      service.printText(text, resultCallback(promise))
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun printTextWithFont(text: String, typeface: String, fontsize: Double, promise: Promise) {
    val service = getService(promise) ?: return
    try {
      service.printTextWithFont(text, typeface, fontsize.toFloat(), resultCallback(promise))
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun printOriginalText(text: String, promise: Promise) {
    val service = getService(promise) ?: return
    try {
      service.printOriginalText(text, resultCallback(promise))
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  // endregion

  // region Image

  override fun printImage(base64: String, promise: Promise) {
    val service = getService(promise) ?: return
    try {
      val bitmap = decodeBitmap(base64, promise) ?: return
      service.printBitmap(bitmap, resultCallback(promise))
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun printBitmapCustom(base64: String, type: Double, promise: Promise) {
    val service = getService(promise) ?: return
    try {
      val bitmap = decodeBitmap(base64, promise) ?: return
      service.printBitmapCustom(bitmap, type.toInt(), resultCallback(promise))
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  // endregion

  // region Barcode

  override fun printBarCode(
    data: String,
    symbology: Double,
    height: Double,
    width: Double,
    textposition: Double,
    promise: Promise
  ) {
    val service = getService(promise) ?: return
    try {
      service.printBarCode(
        data,
        symbology.toInt(),
        height.toInt(),
        width.toInt(),
        textposition.toInt(),
        resultCallback(promise)
      )
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun printQRCode(data: String, modulesize: Double, errorlevel: Double, promise: Promise) {
    val service = getService(promise) ?: return
    try {
      service.printQRCode(data, modulesize.toInt(), errorlevel.toInt(), resultCallback(promise))
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  // endregion

  // region Table

  override fun printColumnsText(texts: ReadableArray, widths: ReadableArray, aligns: ReadableArray, promise: Promise) {
    val service = getService(promise) ?: return
    try {
      val textsArr = Array(texts.size()) { texts.getString(it) }
      val widthsArr = IntArray(widths.size()) { widths.getInt(it) }
      val alignsArr = IntArray(aligns.size()) { aligns.getInt(it) }
      service.printColumnsText(textsArr, widthsArr, alignsArr, resultCallback(promise))
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun printColumnsString(texts: ReadableArray, widths: ReadableArray, aligns: ReadableArray, promise: Promise) {
    val service = getService(promise) ?: return
    try {
      val textsArr = Array(texts.size()) { texts.getString(it) }
      val widthsArr = IntArray(widths.size()) { widths.getInt(it) }
      val alignsArr = IntArray(aligns.size()) { aligns.getInt(it) }
      service.printColumnsString(textsArr, widthsArr, alignsArr, resultCallback(promise))
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  // endregion

  // region Raw

  override fun sendRAWData(data: ReadableArray, promise: Promise) {
    val service = getService(promise) ?: return
    try {
      val bytes = ByteArray(data.size()) { data.getInt(it).toByte() }
      service.sendRAWData(bytes, resultCallback(promise))
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  // endregion

  // region Paper

  override fun lineWrap(lines: Double, promise: Promise) {
    val service = getService(promise) ?: return
    try {
      service.lineWrap(lines.toInt(), resultCallback(promise))
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun cutPaper(promise: Promise) {
    val service = getService(promise) ?: return
    try {
      service.cutPaper(resultCallback(promise))
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun autoOutPaper(promise: Promise) {
    val service = getService(promise) ?: return
    try {
      service.autoOutPaper(resultCallback(promise))
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  // endregion

  // region Cash Drawer

  override fun openDrawer(promise: Promise) {
    val service = getService(promise) ?: return
    try {
      service.openDrawer(resultCallback(promise))
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  // endregion

  // region Label

  override fun labelLocate(promise: Promise) {
    val service = getService(promise) ?: return
    try {
      service.labelLocate()
      promise.resolve(null)
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun labelOutput(promise: Promise) {
    val service = getService(promise) ?: return
    try {
      service.labelOutput()
      promise.resolve(null)
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  // endregion

  // region LCD

  override fun sendLCDCommand(flag: Double, promise: Promise) {
    val service = getService(promise) ?: return
    try {
      service.sendLCDCommand(flag.toInt())
      promise.resolve(null)
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun sendLCDFillString(text: String, size: Double, fill: Boolean, promise: Promise) {
    val service = getService(promise) ?: return
    try {
      service.sendLCDFillString(text, size.toInt(), fill, lcdCallback(promise))
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun sendLCDMultiString(texts: ReadableArray, align: ReadableArray, promise: Promise) {
    val service = getService(promise) ?: return
    try {
      val textsArr = Array(texts.size()) { texts.getString(it) }
      val alignArr = IntArray(align.size()) { align.getInt(it) }
      service.sendLCDMultiString(textsArr, alignArr, lcdCallback(promise))
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun sendLCDBitmap(base64: String, promise: Promise) {
    val service = getService(promise) ?: return
    try {
      val bitmap = decodeBitmap(base64, promise) ?: return
      service.sendLCDBitmap(bitmap, lcdCallback(promise))
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  // endregion

  // region Transaction

  override fun enterPrinterBuffer(clean: Boolean, promise: Promise) {
    val service = getService(promise) ?: return
    try {
      service.enterPrinterBuffer(clean)
      promise.resolve(null)
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun exitPrinterBuffer(commit: Boolean, promise: Promise) {
    val service = getService(promise) ?: return
    try {
      service.exitPrinterBufferWithCallback(commit, resultCallback(promise))
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun commitPrinterBuffer(promise: Promise) {
    val service = getService(promise) ?: return
    try {
      service.commitPrinterBufferWithCallback(resultCallback(promise))
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  // endregion

  override fun invalidate() {
    super.invalidate()
    InnerPrinterManager.getInstance().unBindService(reactApplicationContext, printerCallback)
    printerService = null
  }

  companion object {
    const val NAME = NativeSunmiPrinterSpec.NAME
  }
}
