# react-native-sunmi-printer

React Native library for Sunmi built-in printers. Supports Sunmi V2S and newer devices (Android 11+).

Built with React Native New Architecture (TurboModule).

## Installation

```sh
npm install react-native-sunmi-printer
# or
yarn add react-native-sunmi-printer
```

> **Note:** This library only supports Android. Sunmi devices are Android-based hardware.

## Usage

### Print Text

```js
import { printText, lineWrap } from 'react-native-sunmi-printer';

await printText('Hello, Sunmi!\n');
await lineWrap(3);
```

### Print Image (Base64)

```js
import { printImage, lineWrap } from 'react-native-sunmi-printer';

await printImage(base64String);
await lineWrap(3);
```

### Print QR Code

```js
import { printQRCode, lineWrap } from 'react-native-sunmi-printer';

// printQRCode(data, moduleSize, errorLevel)
// moduleSize: 1-16
// errorLevel: 0=L(7%), 1=M(15%), 2=Q(25%), 3=H(30%)
await printQRCode('https://example.com', 8, 2);
await lineWrap(3);
```

### Print Barcode

```js
import { printBarCode, lineWrap } from 'react-native-sunmi-printer';

// printBarCode(data, symbology, height, width, textPosition)
// symbology: 0=UPC-A, 1=UPC-E, 2=EAN13, 3=EAN8, 4=CODE39, 5=ITF, 6=CODABAR, 7=CODE93, 8=CODE128
// height: 1-255
// width: 2-6
// textPosition: 0=none, 1=above, 2=below, 3=both
await printBarCode('1234567890', 8, 162, 2, 2);
await lineWrap(3);
```

### Print Table

```js
import { printColumnsString, lineWrap } from 'react-native-sunmi-printer';

// printColumnsString(texts, widths, aligns)
// widths: proportional weights (e.g., [1, 1] = equal columns)
// aligns: 0=left, 1=center, 2=right
await printColumnsString(['Item', 'Price'], [1, 1], [0, 2]);
await printColumnsString(['Coffee', '$3.00'], [1, 1], [0, 2]);
await lineWrap(3);
```

### Text Formatting

```js
import {
  setAlignment,
  setFontSize,
  setPrinterStyle,
  printText,
  printerInit,
} from 'react-native-sunmi-printer';

await setAlignment(1); // 0=left, 1=center, 2=right
await setFontSize(28);
await printText('Centered Title\n');

// Reset formatting
await printerInit();
```

### Transaction Mode

Use transaction mode to get actual print completion status:

```js
import {
  enterPrinterBuffer,
  printText,
  lineWrap,
  exitPrinterBuffer,
} from 'react-native-sunmi-printer';

await enterPrinterBuffer(true);
await printText('Buffered print\n');
await lineWrap(3);
await exitPrinterBuffer(true); // resolves when printing completes
```

### Printer Status

```js
import { updatePrinterState } from 'react-native-sunmi-printer';

const state = await updatePrinterState();
// 1=normal, 2=initializing, 3=hardware error, 4=out of paper,
// 5=overheating, 6=cover open, 7=cutter error, 505=no printer
```

## API Reference

### Printer Info

| Method | Return | Description |
|---|---|---|
| `getPrinterSerialNo()` | `Promise<string>` | Printer serial number |
| `getPrinterVersion()` | `Promise<string>` | Firmware version |
| `getPrinterModal()` | `Promise<string>` | Device model |
| `getPrinterPaper()` | `Promise<number>` | Paper size (1=58mm, other=80mm) |
| `getPrinterMode()` | `Promise<number>` | Mode (0=normal, 1=black mark, 2=label) |
| `getServiceVersion()` | `Promise<string>` | Print service version |
| `getFirmwareStatus()` | `Promise<number>` | Firmware status |
| `updatePrinterState()` | `Promise<number>` | Current printer state |
| `getPrintedLength()` | `Promise<string>` | Print distance since boot |
| `getPrinterFactory()` | `Promise<string>` | Printer head manufacturer |

### Initialization

| Method | Description |
|---|---|
| `printerInit()` | Reset all printer styles to default |
| `printerSelfChecking()` | Print self-test page |

### Formatting

| Method | Description |
|---|---|
| `setAlignment(alignment)` | Set alignment (0=left, 1=center, 2=right) |
| `setFontName(typeface)` | Set font (`"gh"` for built-in monospace) |
| `setFontSize(fontsize)` | Set font size |
| `setPrinterStyle(key, value)` | Set printer style (bold, underline, etc.) |

### Text

| Method | Description |
|---|---|
| `printText(text)` | Print text (include `\n` to flush) |
| `printTextWithFont(text, typeface, fontsize)` | Print text with specific font and size |
| `printOriginalText(text)` | Print text with variable-width characters |

### Image

| Method | Description |
|---|---|
| `printImage(base64)` | Print base64-encoded image |
| `printBitmapCustom(base64, type)` | Print with mode (0=default, 1=black&white, 2=grayscale) |

> Max width: 384px (58mm) or 576px (80mm). Call `lineWrap()` after printing to feed paper.

### Barcode

| Method | Description |
|---|---|
| `printBarCode(data, symbology, height, width, textPosition)` | Print barcode |
| `printQRCode(data, moduleSize, errorLevel)` | Print QR code |

### Table

| Method | Description |
|---|---|
| `printColumnsText(texts, widths, aligns)` | Print columns (widths in character count) |
| `printColumnsString(texts, widths, aligns)` | Print columns (widths as proportional weights) |

### Raw / Paper

| Method | Description |
|---|---|
| `sendRAWData(data)` | Send raw ESC/POS byte array |
| `lineWrap(lines)` | Feed paper by N lines |
| `cutPaper()` | Cut paper (if cutter available) |
| `autoOutPaper()` | Auto feed paper to tear position |

### Cash Drawer

| Method | Description |
|---|---|
| `openDrawer()` | Open cash drawer (if available) |

### Label

| Method | Description |
|---|---|
| `labelLocate()` | Locate label position |
| `labelOutput()` | Push label to cutter for peeling |

### LCD Customer Display

| Method | Description |
|---|---|
| `sendLCDCommand(flag)` | LCD command (1=init, 2=on, 3=off, 4=clear) |
| `sendLCDFillString(text, size, fill)` | Display text on LCD |
| `sendLCDMultiString(texts, align)` | Display multi-line text on LCD |
| `sendLCDBitmap(base64)` | Display image on LCD |

### Transaction

| Method | Description |
|---|---|
| `enterPrinterBuffer(clean)` | Start buffering commands |
| `exitPrinterBuffer(commit)` | Exit buffer (commit=true to print) |
| `commitPrinterBuffer()` | Print buffer contents (stays in buffer mode) |

## Supported Devices

- Sunmi V2S
- Sunmi V2S Plus
- And newer Sunmi devices with Android 11+

## Contributing

- [Development workflow](CONTRIBUTING.md#development-workflow)
- [Sending a pull request](CONTRIBUTING.md#sending-a-pull-request)
- [Code of conduct](CODE_OF_CONDUCT.md)

## License

MIT
