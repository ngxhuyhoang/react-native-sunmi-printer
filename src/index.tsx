import SunmiPrinter from './NativeSunmiPrinter';

// Printer Info
export function getPrinterSerialNo(): Promise<string> {
  return SunmiPrinter.getPrinterSerialNo();
}

export function getPrinterVersion(): Promise<string> {
  return SunmiPrinter.getPrinterVersion();
}

export function getPrinterModal(): Promise<string> {
  return SunmiPrinter.getPrinterModal();
}

export function getPrinterPaper(): Promise<number> {
  return SunmiPrinter.getPrinterPaper();
}

export function getPrinterMode(): Promise<number> {
  return SunmiPrinter.getPrinterMode();
}

export function getServiceVersion(): Promise<string> {
  return SunmiPrinter.getServiceVersion();
}

export function getFirmwareStatus(): Promise<number> {
  return SunmiPrinter.getFirmwareStatus();
}

export function updatePrinterState(): Promise<number> {
  return SunmiPrinter.updatePrinterState();
}

export function getPrintedLength(): Promise<string> {
  return SunmiPrinter.getPrintedLength();
}

export function getPrinterFactory(): Promise<string> {
  return SunmiPrinter.getPrinterFactory();
}

// Initialization
export function printerInit(): Promise<void> {
  return SunmiPrinter.printerInit();
}

export function printerSelfChecking(): Promise<void> {
  return SunmiPrinter.printerSelfChecking();
}

// Formatting
export function setAlignment(alignment: number): Promise<void> {
  return SunmiPrinter.setAlignment(alignment);
}

export function setFontName(typeface: string): Promise<void> {
  return SunmiPrinter.setFontName(typeface);
}

export function setFontSize(fontsize: number): Promise<void> {
  return SunmiPrinter.setFontSize(fontsize);
}

export function setPrinterStyle(key: number, value: number): Promise<void> {
  return SunmiPrinter.setPrinterStyle(key, value);
}

// Text
export function printText(text: string): Promise<void> {
  return SunmiPrinter.printText(text);
}

export function printTextWithFont(
  text: string,
  typeface: string,
  fontsize: number
): Promise<void> {
  return SunmiPrinter.printTextWithFont(text, typeface, fontsize);
}

export function printOriginalText(text: string): Promise<void> {
  return SunmiPrinter.printOriginalText(text);
}

// Image
export function printImage(base64: string): Promise<void> {
  return SunmiPrinter.printImage(base64);
}

export function printBitmapCustom(base64: string, type: number): Promise<void> {
  return SunmiPrinter.printBitmapCustom(base64, type);
}

// Barcode
export function printBarCode(
  data: string,
  symbology: number,
  height: number,
  width: number,
  textposition: number
): Promise<void> {
  return SunmiPrinter.printBarCode(
    data,
    symbology,
    height,
    width,
    textposition
  );
}

export function printQRCode(
  data: string,
  modulesize: number,
  errorlevel: number
): Promise<void> {
  return SunmiPrinter.printQRCode(data, modulesize, errorlevel);
}

// Table
export function printColumnsText(
  texts: string[],
  widths: number[],
  aligns: number[]
): Promise<void> {
  return SunmiPrinter.printColumnsText(texts, widths, aligns);
}

export function printColumnsString(
  texts: string[],
  widths: number[],
  aligns: number[]
): Promise<void> {
  return SunmiPrinter.printColumnsString(texts, widths, aligns);
}

// Raw
export function sendRAWData(data: number[]): Promise<void> {
  return SunmiPrinter.sendRAWData(data);
}

// Paper
export function lineWrap(lines: number): Promise<void> {
  return SunmiPrinter.lineWrap(lines);
}

export function cutPaper(): Promise<void> {
  return SunmiPrinter.cutPaper();
}

export function autoOutPaper(): Promise<void> {
  return SunmiPrinter.autoOutPaper();
}

// Cash Drawer
export function openDrawer(): Promise<void> {
  return SunmiPrinter.openDrawer();
}

// Label
export function labelLocate(): Promise<void> {
  return SunmiPrinter.labelLocate();
}

export function labelOutput(): Promise<void> {
  return SunmiPrinter.labelOutput();
}

// LCD
export function sendLCDCommand(flag: number): Promise<void> {
  return SunmiPrinter.sendLCDCommand(flag);
}

export function sendLCDFillString(
  text: string,
  size: number,
  fill: boolean
): Promise<void> {
  return SunmiPrinter.sendLCDFillString(text, size, fill);
}

export function sendLCDMultiString(
  texts: string[],
  align: number[]
): Promise<void> {
  return SunmiPrinter.sendLCDMultiString(texts, align);
}

export function sendLCDBitmap(base64: string): Promise<void> {
  return SunmiPrinter.sendLCDBitmap(base64);
}

// Transaction
export function enterPrinterBuffer(clean: boolean): Promise<void> {
  return SunmiPrinter.enterPrinterBuffer(clean);
}

export function exitPrinterBuffer(commit: boolean): Promise<void> {
  return SunmiPrinter.exitPrinterBuffer(commit);
}

export function commitPrinterBuffer(): Promise<void> {
  return SunmiPrinter.commitPrinterBuffer();
}
