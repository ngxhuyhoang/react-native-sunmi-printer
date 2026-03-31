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
import com.sunmi.printerx.style.LabelStyle
import com.sunmi.printerx.style.TextStyle

class SunmiPrinterModule(reactContext: ReactApplicationContext) :
  NativeSunmiPrinterSpec(reactContext) {

  private var printer: Printer? = null
  private var isTransMode = false

  // Formatting state
  private var currentAlignment: Align = Align.LEFT
  private var currentFontSize: Int = 24
  private var currentFontName: String? = null
  private var currentBold: Boolean = false
  private var currentUnderline: Boolean = false
  private var currentItalic: Boolean = false
  private var currentStrikethrough: Boolean = false
  private var currentAntiWhite: Boolean = false
  private var currentInvert: Boolean = false
  private var currentDoubleWidth: Boolean = false
  private var currentDoubleHeight: Boolean = false
  private var currentTextSpace: Int = 0
  private var currentLineSpacing: Int = -1

  // WoyouConsts keys
  companion object {
    const val NAME = NativeSunmiPrinterSpec.NAME
    private const val ENABLE_BOLD = 0
    private const val ENABLE_UNDERLINE = 1
    private const val ENABLE_ANTI_WHITE = 2
    private const val ENABLE_STRIKETHROUGH = 3
    private const val ENABLE_ITALIC = 4
    private const val ENABLE_INVERT = 5
    private const val SET_TEXT_RIGHT_SPACING = 6
    private const val SET_LINE_SPACING = 9
    private const val ENABLE_DOUBLE_WIDTH = 14
    private const val ENABLE_DOUBLE_HEIGHT = 15
  }

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

  private fun buildTextStyle(): TextStyle {
    return TextStyle.getStyle().apply {
      setTextSize(currentFontSize)
      enableBold(currentBold)
      enableUnderline(currentUnderline)
      enableStrikethrough(currentStrikethrough)
      enableItalics(currentItalic)
      enableAntiColor(currentAntiWhite)
      enableInvert(currentInvert)
      if (currentDoubleWidth) setTextWidthRatio(2) else setTextWidthRatio(1)
      if (currentDoubleHeight) setTextHeightRatio(2) else setTextHeightRatio(1)
      if (currentTextSpace > 0) setTextSpace(currentTextSpace)
      currentFontName?.let { if (it.isNotEmpty()) setFont(it) }
    }
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
      // PrinterX SDK does not expose firmware update status directly.
      // Returns 0 (unknown). Use updatePrinterState() for operational status.
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
      val distance = p.queryApi().getInfo(PrinterInfo.DISTANCE)
      promise.resolve(distance ?: "0")
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
      currentFontName = null
      currentBold = false
      currentUnderline = false
      currentItalic = false
      currentStrikethrough = false
      currentAntiWhite = false
      currentInvert = false
      currentDoubleWidth = false
      currentDoubleHeight = false
      currentTextSpace = 0
      currentLineSpacing = -1
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
      currentFontName = typeface
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
      val k = key.toInt()
      val v = value.toInt()
      val enabled = v == 1
      when (k) {
        ENABLE_BOLD -> currentBold = enabled
        ENABLE_UNDERLINE -> currentUnderline = enabled
        ENABLE_ANTI_WHITE -> currentAntiWhite = enabled
        ENABLE_STRIKETHROUGH -> currentStrikethrough = enabled
        ENABLE_ITALIC -> currentItalic = enabled
        ENABLE_INVERT -> currentInvert = enabled
        SET_TEXT_RIGHT_SPACING -> currentTextSpace = v
        SET_LINE_SPACING -> currentLineSpacing = v
        ENABLE_DOUBLE_WIDTH -> currentDoubleWidth = enabled
        ENABLE_DOUBLE_HEIGHT -> currentDoubleHeight = enabled
      }
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
        printText(text, buildTextStyle())
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
      val style = buildTextStyle().setTextSize(fontsize.toInt())
      if (typeface.isNotEmpty()) style.setFont(typeface)
      p.lineApi()?.run {
        initLine(BaseStyle.getStyle().setAlign(currentAlignment))
        printText(text, style)
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

  // region Print Style Query

  override fun getForcedDouble(promise: Promise) {
    try {
      val value = when {
        currentDoubleWidth && currentDoubleHeight -> 3
        currentDoubleHeight -> 2
        currentDoubleWidth -> 1
        else -> 0
      }
      promise.resolve(value)
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun isForcedBold(promise: Promise) {
    try {
      promise.resolve(currentBold)
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun isForcedUnderline(promise: Promise) {
    try {
      promise.resolve(currentUnderline)
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun isForcedAntiWhite(promise: Promise) {
    try {
      promise.resolve(currentAntiWhite)
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun getPrinterDensity(promise: Promise) {
    val p = getPrinter(promise) ?: return
    try {
      val density = p.queryApi().getInfo(PrinterInfo.DENSITY)
      promise.resolve(density?.toIntOrNull() ?: 0)
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun getForcedRowHeight(promise: Promise) {
    try {
      promise.resolve(currentLineSpacing)
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun getCurrentFontName(promise: Promise) {
    try {
      promise.resolve(currentFontName ?: "")
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

  override fun print2DCode(data: String, symbology: Double, modulesize: Double, errorlevel: Double, promise: Promise) {
    val p = getPrinter(promise) ?: return
    try {
      when (symbology.toInt()) {
        1 -> {
          // QR Code - delegate to printQrCode
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
        }
        2 -> {
          // PDF417 via ESC/POS GS ( k commands
          val dataBytes = data.toByteArray(Charsets.UTF_8)
          val storeLen = dataBytes.size + 3
          val pL = (storeLen and 0xFF).toByte()
          val pH = ((storeLen shr 8) and 0xFF).toByte()
          val cmd = byteArrayOf(
            0x1D, 0x28, 0x6B, 0x03, 0x00, 0x30, 0x43, modulesize.toInt().toByte(),
            0x1D, 0x28, 0x6B, 0x04, 0x00, 0x30, 0x45, 0x30, errorlevel.toInt().toByte()
          ) + byteArrayOf(0x1D, 0x28, 0x6B, pL, pH, 0x30, 0x50, 0x30) + dataBytes +
            byteArrayOf(0x1D, 0x28, 0x6B, 0x03, 0x00, 0x30, 0x51, 0x30)
          p.commandApi()?.sendEscCommand(cmd)
        }
        3 -> {
          // DataMatrix via ESC/POS GS ( k commands
          val dataBytes = data.toByteArray(Charsets.UTF_8)
          val storeLen = dataBytes.size + 3
          val pL = (storeLen and 0xFF).toByte()
          val pH = ((storeLen shr 8) and 0xFF).toByte()
          val cmd = byteArrayOf(
            0x1D, 0x28, 0x6B, 0x03, 0x00, 0x36, 0x43, modulesize.toInt().toByte()
          ) + byteArrayOf(0x1D, 0x28, 0x6B, pL, pH, 0x36, 0x50, 0x30) + dataBytes +
            byteArrayOf(0x1D, 0x28, 0x6B, 0x03, 0x00, 0x36, 0x51, 0x30)
          p.commandApi()?.sendEscCommand(cmd)
        }
        else -> {
          promise.reject("PRINTER_ERROR", "Unsupported 2D symbology: ${symbology.toInt()}")
          return
        }
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

  override fun getDrawerStatus(promise: Promise) {
    val p = getPrinter(promise) ?: return
    try {
      val isOpen = p.cashDrawerApi()?.isOpen() ?: false
      promise.resolve(if (isOpen) 1 else 0)
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun getOpenDrawerTimes(promise: Promise) {
    val p = getPrinter(promise) ?: return
    try {
      // PrinterX SDK does not track drawer open count. Returns 0.
      promise.resolve(0)
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  // endregion

  // region Cut Paper Info

  override fun getCutPaperTimes(promise: Promise) {
    val p = getPrinter(promise) ?: return
    try {
      val cutter = p.queryApi().getInfo(PrinterInfo.CUTTER)
      promise.resolve(cutter?.toIntOrNull() ?: 0)
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun getPrinterBBMDistance(promise: Promise) {
    val p = getPrinter(promise) ?: return
    try {
      val distance = p.queryApi().getInfo(PrinterInfo.DISTANCE)
      promise.resolve(distance?.toIntOrNull() ?: 0)
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  // endregion

  // region Label

  override fun labelLocate(promise: Promise) {
    val p = getPrinter(promise) ?: return
    try {
      p.lineApi()?.initLine(LabelStyle.getStyle().setAlign(currentAlignment))
      promise.resolve(null)
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun labelOutput(promise: Promise) {
    val p = getPrinter(promise) ?: return
    try {
      p.lineApi()?.autoOut()
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

  override fun sendLCDString(text: String, promise: Promise) {
    val p = getPrinter(promise) ?: return
    try {
      p.lcdApi()?.showText(text, 0, false)
      promise.resolve(null)
    } catch (e: Exception) {
      promise.reject("PRINTER_ERROR", e.message, e)
    }
  }

  override fun sendLCDDoubleString(topText: String, bottomText: String, promise: Promise) {
    val p = getPrinter(promise) ?: return
    try {
      p.lcdApi()?.showTexts(arrayOf(topText, bottomText), intArrayOf(1, 1))
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

  override fun commitPrinterBufferWithCallback(promise: Promise) {
    commitPrinterBuffer(promise)
  }

  override fun exitPrinterBufferWithCallback(commit: Boolean, promise: Promise) {
    exitPrinterBuffer(commit, promise)
  }

  // endregion

  override fun invalidate() {
    super.invalidate()
    PrinterSdk.getInstance().destroy()
    printer = null
  }
}
