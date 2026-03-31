import {
  Text,
  View,
  StyleSheet,
  Button,
  ScrollView,
  Alert,
} from 'react-native';
import {
  // Printer Info
  getPrinterSerialNo,
  getPrinterVersion,
  getPrinterModal,
  getPrinterPaper,
  getPrinterMode,
  getServiceVersion,
  getFirmwareStatus,
  updatePrinterState,
  getPrintedLength,
  getPrinterFactory,
  // Initialization
  printerInit,
  printerSelfChecking,
  // Formatting
  setAlignment,
  setFontName,
  setFontSize,
  setPrinterStyle,
  // Text
  printText,
  printTextWithFont,
  printOriginalText,
  // Image
  printImage,
  printBitmapCustom,
  // Print Style Query
  getForcedDouble,
  isForcedBold,
  isForcedUnderline,
  isForcedAntiWhite,
  getForcedRowHeight,
  getCurrentFontName,
  getPrinterDensity,
  // Barcode
  printBarCode,
  printQRCode,
  print2DCode,
  // Table
  printColumnsText,
  printColumnsString,
  // Raw
  sendRAWData,
  // Paper
  lineWrap,
  cutPaper,
  autoOutPaper,
  getCutPaperTimes,
  getPrinterBBMDistance,
  // Cash Drawer
  openDrawer,
  getDrawerStatus,
  getOpenDrawerTimes,
  // Label
  labelLocate,
  labelOutput,
  // LCD
  sendLCDCommand,
  sendLCDString,
  sendLCDDoubleString,
  sendLCDFillString,
  sendLCDMultiString,
  sendLCDBitmap,
  // Transaction
  enterPrinterBuffer,
  exitPrinterBuffer,
  commitPrinterBuffer,
  exitPrinterBufferWithCallback,
  commitPrinterBufferWithCallback,
} from '@hoangnh0099/react-native-sunmi-printer';
import { useState } from 'react';

const SAMPLE_BASE64 =
  'iVBORw0KGgoAAAANSUhEUgAAAAoAAAAKCAYAAACNMs+9AAAAFklEQVQYV2P8/5+hnoEIwDiqEF8oAABf9AoL/k2KVAAAAABJRU5ErkJggg==';

function Section({
  title,
  children,
}: {
  title: string;
  children: React.ReactNode;
}) {
  return (
    <View style={styles.section}>
      <Text style={styles.sectionTitle}>{title}</Text>
      {children}
    </View>
  );
}

export default function App() {
  const [info, setInfo] = useState<string>('');

  const showResult = (label: string, value: unknown) => {
    const msg = `${label}: ${value}`;
    setInfo(msg);
  };

  const showError = (e: unknown) => {
    const msg = e instanceof Error ? e.message : String(e);
    Alert.alert('Error', msg);
  };

  // region Printer Info

  const handleGetPrinterInfo = async () => {
    try {
      const serial = await getPrinterSerialNo();
      const version = await getPrinterVersion();
      const model = await getPrinterModal();
      const paper = await getPrinterPaper();
      const mode = await getPrinterMode();
      const serviceVer = await getServiceVersion();
      const firmware = await getFirmwareStatus();
      const state = await updatePrinterState();
      const length = await getPrintedLength();
      const factory = await getPrinterFactory();
      setInfo(
        [
          `Serial: ${serial}`,
          `Version: ${version}`,
          `Model: ${model}`,
          `Paper: ${paper}mm`,
          `Mode: ${mode}`,
          `Service: ${serviceVer}`,
          `Firmware: ${firmware}`,
          `State: ${state}`,
          `Printed: ${length}`,
          `Factory: ${factory}`,
        ].join('\n')
      );
    } catch (e) {
      showError(e);
    }
  };

  const handleGetState = async () => {
    try {
      const state = await updatePrinterState();
      const labels: Record<number, string> = {
        1: 'Normal',
        2: 'Initializing',
        3: 'Hardware error',
        4: 'Out of paper',
        5: 'Overheating',
        6: 'Cover open',
        7: 'Cutter error',
        8: 'Cutter recovered',
        505: 'No printer',
      };
      showResult('Printer State', `${state} (${labels[state] ?? 'Unknown'})`);
    } catch (e) {
      showError(e);
    }
  };

  // endregion

  // region Initialization

  const handlePrinterInit = async () => {
    try {
      await printerInit();
      showResult('printerInit', 'OK');
    } catch (e) {
      showError(e);
    }
  };

  const handleSelfCheck = async () => {
    try {
      await printerSelfChecking();
      showResult('printerSelfChecking', 'OK');
    } catch (e) {
      showError(e);
    }
  };

  // endregion

  // region Formatting + Text

  const handlePrintText = async () => {
    try {
      await printText('Hello from Sunmi Printer!\n');
      await lineWrap(3);
      showResult('printText', 'OK');
    } catch (e) {
      showError(e);
    }
  };

  const handlePrintTextWithFont = async () => {
    try {
      await printTextWithFont('Custom Font Text\n', '', 28);
      await lineWrap(3);
      showResult('printTextWithFont', 'OK');
    } catch (e) {
      showError(e);
    }
  };

  const handlePrintOriginalText = async () => {
    try {
      await printOriginalText('Original Text\n');
      await lineWrap(3);
      showResult('printOriginalText', 'OK');
    } catch (e) {
      showError(e);
    }
  };

  const handlePrintFormatted = async () => {
    try {
      await setAlignment(1);
      await setFontSize(32);
      await printText('=== RECEIPT ===\n');

      await printerInit();
      await setAlignment(0);
      await printText('Item            Price\n');
      await printText('Coffee          $3.00\n');
      await printText('Donut           $1.50\n');
      await printText('------------------------\n');

      await setAlignment(2);
      await printText('Total: $4.50\n');

      await printerInit();
      await lineWrap(3);
      showResult('Formatted Print', 'OK');
    } catch (e) {
      showError(e);
    }
  };

  const handleSetFont = async () => {
    try {
      await setFontName('gh');
      await printText('Monospace Font\n');
      await printerInit();
      await lineWrap(3);
      showResult('setFontName', 'OK');
    } catch (e) {
      showError(e);
    }
  };

  const handleSetPrinterStyle = async () => {
    try {
      // Bold: key=0 (ENABLE_BOLD), value=1 (ENABLE)
      await setPrinterStyle(0, 1);
      await printText('Bold Text\n');
      // Disable bold
      await setPrinterStyle(0, 0);
      await lineWrap(3);
      showResult('setPrinterStyle', 'OK');
    } catch (e) {
      showError(e);
    }
  };

  const handleGetStyleInfo = async () => {
    try {
      const bold = await isForcedBold();
      const underline = await isForcedUnderline();
      const antiWhite = await isForcedAntiWhite();
      const double = await getForcedDouble();
      const rowHeight = await getForcedRowHeight();
      const fontName = await getCurrentFontName();
      const density = await getPrinterDensity();
      setInfo(
        [
          `Bold: ${bold}`,
          `Underline: ${underline}`,
          `AntiWhite: ${antiWhite}`,
          `Double: ${double}`,
          `Row Height: ${rowHeight}`,
          `Font: ${fontName || '(default)'}`,
          `Density: ${density}`,
        ].join('\n')
      );
    } catch (e) {
      showError(e);
    }
  };

  // endregion

  // region Image

  const handlePrintImage = async () => {
    try {
      await printImage(SAMPLE_BASE64);
      await lineWrap(3);
      showResult('printImage', 'OK');
    } catch (e) {
      showError(e);
    }
  };

  const handlePrintBitmapCustom = async () => {
    try {
      // type: 0=binarization, 1=dithering
      await printBitmapCustom(SAMPLE_BASE64, 1);
      await lineWrap(3);
      showResult('printBitmapCustom (dithering)', 'OK');
    } catch (e) {
      showError(e);
    }
  };

  // endregion

  // region Barcode

  const handlePrintBarCode = async () => {
    try {
      await setAlignment(1);
      // CODE128, height=162, width=2, text below
      await printBarCode('1234567890', 8, 162, 2, 2);
      await printerInit();
      await lineWrap(3);
      showResult('printBarCode', 'OK');
    } catch (e) {
      showError(e);
    }
  };

  const handlePrintQRCode = async () => {
    try {
      await setAlignment(1);
      // moduleSize=8, errorLevel=2 (Q=25%)
      await printQRCode('https://sunmi.com', 8, 2);
      await printerInit();
      await lineWrap(3);
      showResult('printQRCode', 'OK');
    } catch (e) {
      showError(e);
    }
  };

  const handlePrint2DCode = async () => {
    try {
      await setAlignment(1);
      // PDF417: symbology=2, moduleSize=2, errorLevel=2
      await print2DCode('Hello PDF417', 2, 2, 2);
      await printerInit();
      await lineWrap(3);
      showResult('print2DCode (PDF417)', 'OK');
    } catch (e) {
      showError(e);
    }
  };

  // endregion

  // region Table

  const handlePrintColumnsText = async () => {
    try {
      // widths as proportional weights
      await printColumnsText(['Name', 'Qty', 'Price'], [2, 1, 1], [0, 1, 2]);
      await printColumnsText(['Coffee', '2', '$6.00'], [2, 1, 1], [0, 1, 2]);
      await printColumnsText(['Donut', '3', '$4.50'], [2, 1, 1], [0, 1, 2]);
      await lineWrap(3);
      showResult('printColumnsText', 'OK');
    } catch (e) {
      showError(e);
    }
  };

  const handlePrintColumnsString = async () => {
    try {
      // widths as proportional weights
      await printColumnsString(['Name', 'Qty', 'Price'], [2, 1, 1], [0, 1, 2]);
      await printColumnsString(['Coffee', '2', '$6.00'], [2, 1, 1], [0, 1, 2]);
      await printColumnsString(['Donut', '3', '$4.50'], [2, 1, 1], [0, 1, 2]);
      await lineWrap(3);
      showResult('printColumnsString', 'OK');
    } catch (e) {
      showError(e);
    }
  };

  // endregion

  // region Raw / Paper

  const handleSendRAWData = async () => {
    try {
      // ESC/POS: bold on, print "RAW", bold off, newline
      await sendRAWData([
        0x1b, 0x45, 0x01, 0x52, 0x41, 0x57, 0x1b, 0x45, 0x00, 0x0a,
      ]);
      await lineWrap(3);
      showResult('sendRAWData', 'OK');
    } catch (e) {
      showError(e);
    }
  };

  const handleLineWrap = async () => {
    try {
      await printText('Line wrap test\n');
      await lineWrap(5);
      showResult('lineWrap', 'OK');
    } catch (e) {
      showError(e);
    }
  };

  const handleCutPaper = async () => {
    try {
      await cutPaper();
      showResult('cutPaper', 'OK');
    } catch (e) {
      showError(e);
    }
  };

  const handleAutoOutPaper = async () => {
    try {
      await autoOutPaper();
      showResult('autoOutPaper', 'OK');
    } catch (e) {
      showError(e);
    }
  };

  const handleGetCutPaperTimes = async () => {
    try {
      const times = await getCutPaperTimes();
      showResult('Cut Paper Times', times);
    } catch (e) {
      showError(e);
    }
  };

  const handleGetBBMDistance = async () => {
    try {
      const distance = await getPrinterBBMDistance();
      showResult('BBM Distance', distance);
    } catch (e) {
      showError(e);
    }
  };

  // endregion

  // region Cash Drawer

  const handleOpenDrawer = async () => {
    try {
      await openDrawer();
      showResult('openDrawer', 'OK');
    } catch (e) {
      showError(e);
    }
  };

  const handleGetDrawerStatus = async () => {
    try {
      const status = await getDrawerStatus();
      showResult('Drawer Status', status === 1 ? 'Open' : 'Closed');
    } catch (e) {
      showError(e);
    }
  };

  const handleGetOpenDrawerTimes = async () => {
    try {
      const times = await getOpenDrawerTimes();
      showResult('Open Drawer Times', times);
    } catch (e) {
      showError(e);
    }
  };

  // endregion

  // region Label

  const handleLabelLocate = async () => {
    try {
      await labelLocate();
      showResult('labelLocate', 'OK');
    } catch (e) {
      showError(e);
    }
  };

  const handleLabelOutput = async () => {
    try {
      await labelOutput();
      showResult('labelOutput', 'OK');
    } catch (e) {
      showError(e);
    }
  };

  // endregion

  // region LCD

  const handleLCDInit = async () => {
    try {
      await sendLCDCommand(1); // init
      showResult('sendLCDCommand (init)', 'OK');
    } catch (e) {
      showError(e);
    }
  };

  const handleLCDString = async () => {
    try {
      await sendLCDString('Hello LCD!');
      showResult('sendLCDString', 'OK');
    } catch (e) {
      showError(e);
    }
  };

  const handleLCDDoubleString = async () => {
    try {
      await sendLCDDoubleString('Top Line', 'Bottom Line');
      showResult('sendLCDDoubleString', 'OK');
    } catch (e) {
      showError(e);
    }
  };

  const handleLCDText = async () => {
    try {
      await sendLCDFillString('Hello LCD!', 24, true);
      showResult('sendLCDFillString', 'OK');
    } catch (e) {
      showError(e);
    }
  };

  const handleLCDMultiString = async () => {
    try {
      await sendLCDMultiString(['Line 1', 'Line 2'], [1, 1]);
      showResult('sendLCDMultiString', 'OK');
    } catch (e) {
      showError(e);
    }
  };

  const handleLCDBitmap = async () => {
    try {
      await sendLCDBitmap(SAMPLE_BASE64);
      showResult('sendLCDBitmap', 'OK');
    } catch (e) {
      showError(e);
    }
  };

  const handleLCDClear = async () => {
    try {
      await sendLCDCommand(4); // clear
      showResult('sendLCDCommand (clear)', 'OK');
    } catch (e) {
      showError(e);
    }
  };

  // endregion

  // region Transaction

  const handleTransactionPrint = async () => {
    try {
      await enterPrinterBuffer(true);
      await printText('--- Transaction Start ---\n');
      await printText('Buffered line 1\n');
      await printText('Buffered line 2\n');
      await printText('--- Transaction End ---\n');
      await lineWrap(3);
      await exitPrinterBuffer(true);
      showResult('Transaction Print', 'OK');
    } catch (e) {
      showError(e);
    }
  };

  const handleCommitBuffer = async () => {
    try {
      await enterPrinterBuffer(true);
      await printText('Commit buffer test\n');
      await lineWrap(3);
      await commitPrinterBuffer();
      showResult('commitPrinterBuffer', 'OK');
    } catch (e) {
      showError(e);
    }
  };

  const handleCommitBufferWithCallback = async () => {
    try {
      await enterPrinterBuffer(true);
      await printText('Commit with callback\n');
      await lineWrap(3);
      await commitPrinterBufferWithCallback();
      showResult('commitPrinterBufferWithCallback', 'OK');
    } catch (e) {
      showError(e);
    }
  };

  const handleExitBufferWithCallback = async () => {
    try {
      await enterPrinterBuffer(true);
      await printText('Exit with callback\n');
      await lineWrap(3);
      await exitPrinterBufferWithCallback(true);
      showResult('exitPrinterBufferWithCallback', 'OK');
    } catch (e) {
      showError(e);
    }
  };

  // endregion

  return (
    <ScrollView style={styles.scroll} contentContainerStyle={styles.container}>
      <Text style={styles.title}>Sunmi Printer Test</Text>

      {info !== '' && <Text style={styles.info}>{info}</Text>}

      <Section title="Printer Info">
        <Button title="Get All Info" onPress={handleGetPrinterInfo} />
        <Button title="Check State" onPress={handleGetState} />
      </Section>

      <Section title="Initialization">
        <Button title="Printer Init (Reset)" onPress={handlePrinterInit} />
        <Button title="Self Check" onPress={handleSelfCheck} />
      </Section>

      <Section title="Text">
        <Button title="Print Text" onPress={handlePrintText} />
        <Button
          title="Print Text With Font"
          onPress={handlePrintTextWithFont}
        />
        <Button title="Print Original Text" onPress={handlePrintOriginalText} />
        <Button
          title="Print Formatted Receipt"
          onPress={handlePrintFormatted}
        />
        <Button title="Set Font (Monospace)" onPress={handleSetFont} />
        <Button
          title="Set Printer Style (Bold)"
          onPress={handleSetPrinterStyle}
        />
        <Button title="Get Style Info" onPress={handleGetStyleInfo} />
      </Section>

      <Section title="Image">
        <Button title="Print Image" onPress={handlePrintImage} />
        <Button
          title="Print Bitmap Custom (Dithering)"
          onPress={handlePrintBitmapCustom}
        />
      </Section>

      <Section title="Barcode">
        <Button title="Print Barcode (CODE128)" onPress={handlePrintBarCode} />
        <Button title="Print QR Code" onPress={handlePrintQRCode} />
        <Button title="Print 2D Code (PDF417)" onPress={handlePrint2DCode} />
      </Section>

      <Section title="Table">
        <Button title="Print Columns Text" onPress={handlePrintColumnsText} />
        <Button
          title="Print Columns String"
          onPress={handlePrintColumnsString}
        />
      </Section>

      <Section title="Raw / Paper">
        <Button title="Send RAW Data (ESC/POS)" onPress={handleSendRAWData} />
        <Button title="Line Wrap (5)" onPress={handleLineWrap} />
        <Button title="Cut Paper" onPress={handleCutPaper} />
        <Button title="Auto Out Paper" onPress={handleAutoOutPaper} />
        <Button title="Get Cut Paper Times" onPress={handleGetCutPaperTimes} />
        <Button title="Get BBM Distance" onPress={handleGetBBMDistance} />
      </Section>

      <Section title="Cash Drawer">
        <Button title="Open Drawer" onPress={handleOpenDrawer} />
        <Button title="Get Drawer Status" onPress={handleGetDrawerStatus} />
        <Button
          title="Get Open Drawer Times"
          onPress={handleGetOpenDrawerTimes}
        />
      </Section>

      <Section title="Label">
        <Button title="Label Locate" onPress={handleLabelLocate} />
        <Button title="Label Output" onPress={handleLabelOutput} />
      </Section>

      <Section title="LCD Display">
        <Button title="LCD Init" onPress={handleLCDInit} />
        <Button title="LCD String" onPress={handleLCDString} />
        <Button title="LCD Double String" onPress={handleLCDDoubleString} />
        <Button title="LCD Fill String" onPress={handleLCDText} />
        <Button title="LCD Multi String" onPress={handleLCDMultiString} />
        <Button title="LCD Bitmap" onPress={handleLCDBitmap} />
        <Button title="LCD Clear" onPress={handleLCDClear} />
      </Section>

      <Section title="Transaction">
        <Button title="Transaction Print" onPress={handleTransactionPrint} />
        <Button title="Commit Buffer" onPress={handleCommitBuffer} />
        <Button
          title="Commit Buffer (Callback)"
          onPress={handleCommitBufferWithCallback}
        />
        <Button
          title="Exit Buffer (Callback)"
          onPress={handleExitBufferWithCallback}
        />
      </Section>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  scroll: {
    flex: 1,
  },
  container: {
    padding: 20,
    paddingBottom: 60,
  },
  title: {
    fontSize: 22,
    fontWeight: 'bold',
    textAlign: 'center',
    marginBottom: 10,
  },
  info: {
    fontSize: 13,
    backgroundColor: '#f0f0f0',
    padding: 10,
    borderRadius: 6,
    marginBottom: 10,
    fontFamily: 'monospace',
  },
  section: {
    marginBottom: 16,
    gap: 6,
  },
  sectionTitle: {
    fontSize: 16,
    fontWeight: '600',
    marginBottom: 4,
    color: '#333',
  },
});
