package com.sunmiprinter

import android.graphics.BitmapFactory
import android.util.Base64
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.sunmi.printerx.PrinterSdk
import com.sunmi.printerx.PrinterSdk.Printer
import com.sunmi.printerx.api.PrintResult
import com.sunmi.printerx.enums.Align
import com.sunmi.printerx.enums.Command
import com.sunmi.printerx.enums.DividingLine
import com.sunmi.printerx.enums.ErrorLevel
import com.sunmi.printerx.enums.HumanReadable
import com.sunmi.printerx.enums.ImageAlgorithm
import com.sunmi.printerx.enums.PrinterInfo
import com.sunmi.printerx.enums.PrinterType
import com.sunmi.printerx.style.BaseStyle
import com.sunmi.printerx.style.BarcodeStyle
import com.sunmi.printerx.style.BitmapStyle
import com.sunmi.printerx.style.QrStyle
import com.sunmi.printerx.style.TextStyle

class SunmiPrinterModule(reactContext: ReactApplicationContext) :
  NativeSunmiPrinterSpec(reactContext) {

  private var printer: Printer? = null
  private var isTransMode = false

  // Formatting state
  private var currentAlignment: Align = Align.LEFT
  private var currentFontSize: Int = 24

  init {
    PrinterSdk.getInstance().getPrinter(reactContext, object : PrinterSdk.PrinterListen {
      override fun onDefPrinter(printer: Printer?) {
        this@SunmiPrinterModule.printer = printer
      }

      override fun onPrinters(printers: MutableList<Printer>?) {
        if (this@SunmiPrinterModule.printer == null && !printers.isNullOrEmpty()) {
          this@SunmiPrinterModule.printer = printers[0]
        }
      }
    })
  }

  private fun getPrinter(promise: Promise): Printer? {
    return printer ?: run {
      promise.reject("PRINTER_NOT_FOUND", "No printer found")
      null
    }
  }

  private fun decodeBitmap(base64: String, promise: Promise): android.graphics.Bitmap? {
    val bytes = Base64.decode(base64, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: run {
      promise.reject("DECODE_ERROR", "Failed to decode base64 image")
      null
    }
  }

  private fun toAlign(value: Int): Align = when (value) {
    0 -> Align.LEFT
    1 -> Align.CENTER
    2 -> Align.RIGHT
    else -> Align.LEFT
  }

  // region Printer Info

  override fun getPrinterSerialNo(promise: Promise) {
    val p = getPrinter(promise) ?: return
    try {
      promise.resolve(p.queryApi().getInfo(PrinterInfo.ID))
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun getPrinterVersion(promise: Promise) {
    val p = getPrinter(promise) ?: return
    try {
      promise.resolve(p.queryApi().getInfo(PrinterInfo.VERSION))
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun getPrinterModal(promise: Promise) {
    val p = getPrinter(promise) ?: return
    try {
      promise.resolve(p.queryApi().getInfo(PrinterInfo.NAME))
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun getPrinterPaper(promise: Promise) {
    val p = getPrinter(promise) ?: return
    try {
      val paper = p.queryApi().getInfo(PrinterInfo.PAPER)
      val paperWidth = paper?.replace("mm", "")?.trim()?.toIntOrNull() ?: 0
      promise.resolve(paperWidth)
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun getPrinterMode(promise: Promise) {
    val p = getPrinter(promise) ?: return
    try {
      val type = p.queryApi().getInfo(PrinterInfo.TYPE)
      val mode = when (type) {
        PrinterType.THERMAL.toString() -> 0
        PrinterType.BLACK_LABEL.toString() -> 1
        PrinterType.LABEL.toString() -> 2
        else -> 0
      }
      promise.resolve(mode)
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun getServiceVersion(promise: Promise) {
    val p = getPrinter(promise) ?: return
    try {
      promise.resolve(p.queryApi().getInfo(PrinterInfo.VERSION))
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun getFirmwareStatus(promise: Promise) {
    val p = getPrinter(promise) ?: return
    try {
      promise.resolve(0)
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun updatePrinterState(promise: Promise) {
    val p = getPrinter(promise) ?: return
    try {
      promise.resolve(p.queryApi().status.ordinal)
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun getPrintedLength(promise: Promise) {
    val p = getPrinter(promise) ?: return
    try {
      promise.resolve("0")
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun getPrinterFactory(promise: Promise) {
    val p = getPrinter(promise) ?: return
    try {
      promise.resolve("SUNMI")
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  // endregion

  // region Initialization

  override fun printerInit(promise: Promise) {
    val p = getPrinter(promise) ?: return
    try {
      currentAlignment = Align.LEFT
      currentFontSize = 24
      p.lineApi()?.initLine(BaseStyle.getStyle())
      promise.resolve(null)
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun printerSelfChecking(promise: Promise) {
    val p = getPrinter(promise) ?: return
    try {
      p.lineApi()?.run {
        initLine(BaseStyle.getStyle().setAlign(Align.CENTER))
        printText("Printer Self Check OK", TextStyle.getStyle())
        autoOut()
      }
      promise.resolve(null)
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  // endregion

  // region Formatting

  override fun setAlignment(alignment: Double, promise: Promise) {
    try {
      currentAlignment = toAlign(alignment.toInt())
      promise.resolve(null)
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun setFontName(typeface: String, promise: Promise) {
    try {
      promise.resolve(null)
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun setFontSize(fontsize: Double, promise: Promise) {
    try {
      currentFontSize = fontsize.toInt()
      promise.resolve(null)
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun setPrinterStyle(key: Double, value: Double, promise: Promise) {
    try {
      promise.resolve(null)
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  // endregion

  // region Text

  override fun printText(text: String, promise: Promise) {
    val p = getPrinter(promise) ?: return
    try {
      p.lineApi()?.run {
        initLine(BaseStyle.getStyle().setAlign(currentAlignment))
        printText(text, TextStyle.getStyle().setTextSize(currentFontSize))
        if (!isTransMode) autoOut()
      }
      promise.resolve(null)
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun printTextWithFont(text: String, typeface: String, fontsize: Double, promise: Promise) {
    val p = getPrinter(promise) ?: return
    try {
      p.lineApi()?.run {
        initLine(BaseStyle.getStyle().setAlign(currentAlignment))
        printText(text, TextStyle.getStyle().setTextSize(fontsize.toInt()))
        if (!isTransMode) autoOut()
      }
      promise.resolve(null)
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun printOriginalText(text: String, promise: Promise) {
    val p = getPrinter(promise) ?: return
    try {
      p.commandApi()?.sendEscCommand(text.toByteArray(Charsets.UTF_8))
      promise.resolve(null)
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  // endregion

  // region Image

  override fun printImage(base64: String, promise: Promise) {
    val p = getPrinter(promise) ?: return
    try {
      val bitmap = decodeBitmap(base64, promise) ?: return
      p.lineApi()?.run {
        printBitmap(bitmap, BitmapStyle.getStyle().setAlign(currentAlignment))
        if (!isTransMode) autoOut()
      }
      promise.resolve(null)
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun printBitmapCustom(base64: String, type: Double, promise: Promise) {
    val p = getPrinter(promise) ?: return
    try {
      val bitmap = decodeBitmap(base64, promise) ?: return
      val algorithm = when (type.toInt()) {
        0 -> ImageAlgorithm.BINARIZATION
        1 -> ImageAlgorithm.DITHERING
        else -> ImageAlgorithm.BINARIZATION
      }
      p.lineApi()?.run {
        printBitmap(bitmap, BitmapStyle.getStyle().setAlign(currentAlignment).setAlgorithm(algorithm))
        if (!isTransMode) autoOut()
      }
      promise.resolve(null)
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
    val p = getPrinter(promise) ?: return
    try {
      val readable = when (textposition.toInt()) {
        0 -> HumanReadable.HIDE
        1 -> HumanReadable.POS_ONE
        2 -> HumanReadable.POS_TWO
        3 -> HumanReadable.POS_THREE
        else -> HumanReadable.POS_TWO
      }
      p.lineApi()?.run {
        initLine(BaseStyle.getStyle().setAlign(currentAlignment))
        printBarCode(data, BarcodeStyle.getStyle()
          .setAlign(currentAlignment)
          .setBarHeight(height.toInt())
          .setDotWidth(width.toInt())
          .setReadable(readable))
        if (!isTransMode) autoOut()
      }
      promise.resolve(null)
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun printQRCode(data: String, modulesize: Double, errorlevel: Double, promise: Promise) {
    val p = getPrinter(promise) ?: return
    try {
      val level = when (errorlevel.toInt()) {
        0 -> ErrorLevel.L
        1 -> ErrorLevel.M
        2 -> ErrorLevel.Q
        3 -> ErrorLevel.H
        else -> ErrorLevel.L
      }
      p.lineApi()?.run {
        initLine(BaseStyle.getStyle().setAlign(currentAlignment))
        printQrCode(data, QrStyle.getStyle()
          .setAlign(currentAlignment)
          .setDot(modulesize.toInt())
          .setErrorLevel(level))
        if (!isTransMode) autoOut()
      }
      promise.resolve(null)
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  // endregion

  // region Table

  override fun printColumnsText(texts: ReadableArray, widths: ReadableArray, aligns: ReadableArray, promise: Promise) {
    val p = getPrinter(promise) ?: return
    try {
      val textsArr = Array(texts.size()) { texts.getString(it) ?: "" }
      val widthsArr = IntArray(widths.size()) { widths.getInt(it) }
      val stylesArr = Array(aligns.size()) {
        TextStyle.getStyle().setAlign(toAlign(aligns.getInt(it)))
      }
      p.lineApi()?.run {
        printTexts(textsArr, widthsArr, stylesArr)
        if (!isTransMode) autoOut()
      }
      promise.resolve(null)
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun printColumnsString(texts: ReadableArray, widths: ReadableArray, aligns: ReadableArray, promise: Promise) {
    printColumnsText(texts, widths, aligns, promise)
  }

  // endregion

  // region Raw

  override fun sendRAWData(data: ReadableArray, promise: Promise) {
    val p = getPrinter(promise) ?: return
    try {
      val bytes = ByteArray(data.size()) { data.getInt(it).toByte() }
      p.commandApi()?.sendEscCommand(bytes)
      promise.resolve(null)
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  // endregion

  // region Paper

  override fun lineWrap(lines: Double, promise: Promise) {
    val p = getPrinter(promise) ?: return
    try {
      p.lineApi()?.run {
        printDividingLine(DividingLine.EMPTY, lines.toInt() * 30)
        if (!isTransMode) autoOut()
      }
      promise.resolve(null)
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun cutPaper(promise: Promise) {
    val p = getPrinter(promise) ?: return
    try {
      // ESC/POS cut command: GS V 1
      p.commandApi()?.sendEscCommand(byteArrayOf(0x1d, 0x56, 0x01))
      promise.resolve(null)
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun autoOutPaper(promise: Promise) {
    val p = getPrinter(promise) ?: return
    try {
      p.lineApi()?.autoOut()
      promise.resolve(null)
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  // endregion

  // region Cash Drawer

  override fun openDrawer(promise: Promise) {
    val p = getPrinter(promise) ?: return
    try {
      p.cashDrawerApi()?.open(null)
      promise.resolve(null)
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  // endregion

  // region Label

  override fun labelLocate(promise: Promise) {
    try {
      promise.resolve(null)
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun labelOutput(promise: Promise) {
    try {
      promise.resolve(null)
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  // endregion

  // region LCD

  override fun sendLCDCommand(flag: Double, promise: Promise) {
    val p = getPrinter(promise) ?: return
    try {
      val command = when (flag.toInt()) {
        1 -> Command.INIT
        2 -> Command.WAKE
        3 -> Command.SLEEP
        4 -> Command.CLEAR
        else -> Command.INIT
      }
      p.lcdApi()?.config(command)
      promise.resolve(null)
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun sendLCDFillString(text: String, size: Double, fill: Boolean, promise: Promise) {
    val p = getPrinter(promise) ?: return
    try {
      p.lcdApi()?.showText(text, size.toInt(), fill)
      promise.resolve(null)
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun sendLCDMultiString(texts: ReadableArray, align: ReadableArray, promise: Promise) {
    val p = getPrinter(promise) ?: return
    try {
      val textsArr = Array(texts.size()) { texts.getString(it) ?: "" }
      val alignArr = IntArray(align.size()) { align.getInt(it) }
      p.lcdApi()?.showTexts(textsArr, alignArr)
      promise.resolve(null)
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun sendLCDBitmap(base64: String, promise: Promise) {
    val p = getPrinter(promise) ?: return
    try {
      val bitmap = decodeBitmap(base64, promise) ?: return
      p.lcdApi()?.showBitmap(bitmap)
      promise.resolve(null)
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  // endregion

  // region Transaction

  override fun enterPrinterBuffer(clean: Boolean, promise: Promise) {
    val p = getPrinter(promise) ?: return
    try {
      isTransMode = true
      p.lineApi()?.enableTransMode(true)
      if (clean) {
        p.lineApi()?.initLine(BaseStyle.getStyle())
      }
      promise.resolve(null)
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun exitPrinterBuffer(commit: Boolean, promise: Promise) {
    val p = getPrinter(promise) ?: return
    try {
      if (commit) {
        p.lineApi()?.autoOut()
        p.lineApi()?.printTrans(object : PrintResult() {
          override fun onResult(resultCode: Int, message: String?) {
            isTransMode = false
            p.lineApi()?.enableTransMode(false)
            if (resultCode == 0) promise.resolve(null)
            else promise.reject("PRINTER_ERROR", message ?: "Print failed")
          }
        })
      } else {
        isTransMode = false
        p.lineApi()?.enableTransMode(false)
        promise.resolve(null)
      }
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun commitPrinterBuffer(promise: Promise) {
    val p = getPrinter(promise) ?: return
    try {
      p.lineApi()?.autoOut()
      p.lineApi()?.printTrans(object : PrintResult() {
        override fun onResult(resultCode: Int, message: String?) {
          if (resultCode == 0) promise.resolve(null)
          else promise.reject("PRINTER_ERROR", message ?: "Print failed")
        }
      })
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  // endregion

  override fun invalidate() {
    super.invalidate()
    PrinterSdk.getInstance().destroy()
    printer = null
  }

  companion object {
    const val NAME = NativeSunmiPrinterSpec.NAME
  }
}
