import { TurboModuleRegistry, type TurboModule } from 'react-native';

export interface Spec extends TurboModule {
  // Printer Info
  getPrinterSerialNo(): Promise<string>;
  getPrinterVersion(): Promise<string>;
  getPrinterModal(): Promise<string>;
  getPrinterPaper(): Promise<number>;
  getPrinterMode(): Promise<number>;
  getServiceVersion(): Promise<string>;
  getFirmwareStatus(): Promise<number>;
  updatePrinterState(): Promise<number>;
  getPrintedLength(): Promise<string>;
  getPrinterFactory(): Promise<string>;

  // Initialization
  printerInit(): Promise<void>;
  printerSelfChecking(): Promise<void>;

  // Formatting
  setAlignment(alignment: number): Promise<void>;
  setFontName(typeface: string): Promise<void>;
  setFontSize(fontsize: number): Promise<void>;
  setPrinterStyle(key: number, value: number): Promise<void>;

  // Text
  printText(text: string): Promise<void>;
  printTextWithFont(
    text: string,
    typeface: string,
    fontsize: number
  ): Promise<void>;
  printOriginalText(text: string): Promise<void>;

  // Image
  printImage(base64: string): Promise<void>;
  printBitmapCustom(base64: string, type: number): Promise<void>;

  // Barcode
  printBarCode(
    data: string,
    symbology: number,
    height: number,
    width: number,
    textposition: number
  ): Promise<void>;
  printQRCode(
    data: string,
    modulesize: number,
    errorlevel: number
  ): Promise<void>;

  // Table
  printColumnsText(
    texts: string[],
    widths: number[],
    aligns: number[]
  ): Promise<void>;
  printColumnsString(
    texts: string[],
    widths: number[],
    aligns: number[]
  ): Promise<void>;

  // Raw
  sendRAWData(data: number[]): Promise<void>;

  // Paper
  lineWrap(lines: number): Promise<void>;
  cutPaper(): Promise<void>;
  autoOutPaper(): Promise<void>;

  // Cash Drawer
  openDrawer(): Promise<void>;

  // Label
  labelLocate(): Promise<void>;
  labelOutput(): Promise<void>;

  // LCD
  sendLCDCommand(flag: number): Promise<void>;
  sendLCDFillString(
    text: string,
    size: number,
    fill: boolean
  ): Promise<void>;
  sendLCDMultiString(texts: string[], align: number[]): Promise<void>;
  sendLCDBitmap(base64: string): Promise<void>;

  // Transaction
  enterPrinterBuffer(clean: boolean): Promise<void>;
  exitPrinterBuffer(commit: boolean): Promise<void>;
  commitPrinterBuffer(): Promise<void>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('SunmiPrinter');
