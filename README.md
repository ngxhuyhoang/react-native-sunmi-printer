# @hoangnh0099/react-native-sunmi-printer

[![npm version](https://img.shields.io/npm/v/@hoangnh0099/react-native-sunmi-printer?style=flat&logo=npm&color=CB3837)](https://www.npmjs.com/package/@hoangnh0099/react-native-sunmi-printer)
[![license](https://img.shields.io/badge/license-MIT-blue.svg?style=flat)](https://github.com/ngxhuyhoang/react-native-sunmi-printer/blob/main/LICENSE)
[![platform](https://img.shields.io/badge/platform-Android-3DDC84.svg?style=flat&logo=android&logoColor=white)](#supported-devices)
[![react-native](https://img.shields.io/badge/React%20Native-New%20Architecture-61DAFB.svg?style=flat&logo=react&logoColor=white)](#)

React Native library for Sunmi built-in printers. Supports Sunmi V2S and newer devices (Android 11+).

Built with **React Native New Architecture** (TurboModule) and **Sunmi PrinterX SDK**.

---

## Installation

```sh
npm install @hoangnh0099/react-native-sunmi-printer
# or
yarn add @hoangnh0099/react-native-sunmi-printer
```

> **Note:** This library only supports Android. Sunmi devices are Android-based hardware.

---

## Usage

### Print Text

```js
import { printText, lineWrap } from '@hoangnh0099/react-native-sunmi-printer';

await printText('Hello, Sunmi!\n');
await lineWrap(3);
```

### Print Image (Base64)

```js
import { printImage, lineWrap } from '@hoangnh0099/react-native-sunmi-printer';

await printImage(base64String);
await lineWrap(3);
```

### Print with Styles (Bold, Italic, Underline)

```js
import {
  setPrinterStyle,
  printText,
  lineWrap,
  printerInit,
} from '@hoangnh0099/react-native-sunmi-printer';

// setPrinterStyle(key, value)
// Keys: 0=Bold, 1=Underline, 2=AntiWhite, 3=Strikethrough,
//       4=Italic, 5=Invert, 14=DoubleWidth, 15=DoubleHeight
// Value: 1=Enable, 0=Disable
await setPrinterStyle(0, 1); // Bold ON
await setPrinterStyle(1, 1); // Underline ON
await printText('Bold & Underlined\n');
await printerInit(); // Reset all styles
await lineWrap(3);
```

### Print QR Code

```js
import { printQRCode, lineWrap } from '@hoangnh0099/react-native-sunmi-printer';

// printQRCode(data, moduleSize, errorLevel)
// moduleSize: 4-16
// errorLevel: 0=L(7%), 1=M(15%), 2=Q(25%), 3=H(30%)
await printQRCode('https://example.com', 8, 2);
await lineWrap(3);
```

### Print 2D Barcode (PDF417 / DataMatrix)

```js
import { print2DCode, lineWrap } from '@hoangnh0099/react-native-sunmi-printer';

// print2DCode(data, symbology, moduleSize, errorLevel)
// symbology: 1=QR, 2=PDF417, 3=DataMatrix
await print2DCode('Hello PDF417', 2, 2, 2);
await lineWrap(3);
```

### Print Barcode

```js
import {
  printBarCode,
  lineWrap,
} from '@hoangnh0099/react-native-sunmi-printer';

// printBarCode(data, symbology, height, width, textPosition)
// symbology: 0=UPC-A, 1=UPC-E, 2=EAN13, 3=EAN8, 4=CODE39,
//            5=ITF, 6=CODABAR, 7=CODE93, 8=CODE128
// height: 1-255 | width: 2-6
// textPosition: 0=none, 1=above, 2=below, 3=both
await printBarCode('1234567890', 8, 162, 2, 2);
await lineWrap(3);
```

### Print Table

```js
import {
  printColumnsString,
  lineWrap,
} from '@hoangnh0099/react-native-sunmi-printer';

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
  printText,
  printerInit,
} from '@hoangnh0099/react-native-sunmi-printer';

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
} from '@hoangnh0099/react-native-sunmi-printer';

await enterPrinterBuffer(true);
await printText('Buffered print\n');
await lineWrap(3);
await exitPrinterBuffer(true); // resolves when printing completes
```

### Printer Status

```js
import { updatePrinterState } from '@hoangnh0099/react-native-sunmi-printer';

const state = await updatePrinterState();
// 1=normal, 2=initializing, 3=hardware error, 4=out of paper,
// 5=overheating, 6=cover open, 7=cutter error, 505=no printer
```

---

## API Reference

<details>
<summary><b>Printer Info</b></summary>

| Method                 | Return            | Description                            |
| ---------------------- | ----------------- | -------------------------------------- |
| `getPrinterSerialNo()` | `Promise<string>` | Printer serial number                  |
| `getPrinterVersion()`  | `Promise<string>` | Firmware version                       |
| `getPrinterModal()`    | `Promise<string>` | Device model                           |
| `getPrinterPaper()`    | `Promise<number>` | Paper width in mm (58 or 80)           |
| `getPrinterMode()`     | `Promise<number>` | Mode (0=thermal, 1=black mark, 2=label) |
| `getServiceVersion()`  | `Promise<string>` | Print service version                  |
| `getFirmwareStatus()`  | `Promise<number>` | Firmware status                        |
| `updatePrinterState()` | `Promise<number>` | Current printer state                  |
| `getPrintedLength()`   | `Promise<string>` | Print distance since boot              |
| `getPrinterFactory()`  | `Promise<string>` | Printer head manufacturer              |

</details>

<details>
<summary><b>Initialization</b></summary>

| Method                  | Description                         |
| ----------------------- | ----------------------------------- |
| `printerInit()`         | Reset all printer styles to default |
| `printerSelfChecking()` | Print self-test page                |

</details>

<details>
<summary><b>Formatting</b></summary>

| Method                        | Description                               |
| ----------------------------- | ----------------------------------------- |
| `setAlignment(alignment)`     | Set alignment (0=left, 1=center, 2=right) |
| `setFontName(typeface)`       | Set custom vector font name               |
| `setFontSize(fontsize)`       | Set font size                             |
| `setPrinterStyle(key, value)` | Set print style (see table below)         |

**`setPrinterStyle` Keys:**

| Key | Constant             | Values            |
| --- | -------------------- | ----------------- |
| 0   | `ENABLE_BOLD`        | 0=off, 1=on       |
| 1   | `ENABLE_UNDERLINE`   | 0=off, 1=on       |
| 2   | `ENABLE_ANTI_WHITE`  | 0=off, 1=on       |
| 3   | `ENABLE_STRIKETHROUGH` | 0=off, 1=on     |
| 4   | `ENABLE_ITALIC`      | 0=off, 1=on       |
| 5   | `ENABLE_INVERT`      | 0=off, 1=on       |
| 6   | `SET_TEXT_RIGHT_SPACING` | pixels         |
| 9   | `SET_LINE_SPACING`   | pixels             |
| 14  | `ENABLE_DOUBLE_WIDTH` | 0=off, 1=on      |
| 15  | `ENABLE_DOUBLE_HEIGHT` | 0=off, 1=on     |

</details>

<details>
<summary><b>Print Style Query</b></summary>

| Method                | Return              | Description                                       |
| --------------------- | ------------------- | ------------------------------------------------- |
| `getForcedDouble()`   | `Promise<number>`   | 0=none, 1=width, 2=height, 3=both                |
| `isForcedBold()`      | `Promise<boolean>`  | Whether bold is currently enabled                 |
| `isForcedUnderline()` | `Promise<boolean>`  | Whether underline is currently enabled            |
| `isForcedAntiWhite()` | `Promise<boolean>`  | Whether anti-white is currently enabled           |
| `getForcedRowHeight()` | `Promise<number>`  | Current line spacing (-1=default)                 |
| `getCurrentFontName()` | `Promise<string>`  | Current font name (empty=default)                 |
| `getPrinterDensity()` | `Promise<number>`   | Print density level                               |

</details>

<details>
<summary><b>Text</b></summary>

| Method                                        | Description                               |
| --------------------------------------------- | ----------------------------------------- |
| `printText(text)`                             | Print text (include `\n` to flush)        |
| `printTextWithFont(text, typeface, fontsize)` | Print text with specific font and size    |
| `printOriginalText(text)`                     | Print text with variable-width characters |

</details>

<details>
<summary><b>Image</b></summary>

| Method                            | Description                                             |
| --------------------------------- | ------------------------------------------------------- |
| `printImage(base64)`              | Print base64-encoded image                              |
| `printBitmapCustom(base64, type)` | Print with mode (0=binarization, 1=dithering) |

> Max width: 384px (58mm) or 576px (80mm). Call `lineWrap()` after printing to feed paper.

</details>

<details>
<summary><b>Barcode</b></summary>

| Method                                                       | Description                                     |
| ------------------------------------------------------------ | ----------------------------------------------- |
| `printBarCode(data, symbology, height, width, textPosition)` | Print 1D barcode                                |
| `printQRCode(data, moduleSize, errorLevel)`                  | Print QR code                                   |
| `print2DCode(data, symbology, moduleSize, errorLevel)`       | Print 2D barcode (1=QR, 2=PDF417, 3=DataMatrix) |

</details>

<details>
<summary><b>Table</b></summary>

| Method                                      | Description                                    |
| ------------------------------------------- | ---------------------------------------------- |
| `printColumnsText(texts, widths, aligns)`   | Print columns (widths as proportional weights) |
| `printColumnsString(texts, widths, aligns)` | Alias for `printColumnsText`                   |

</details>

<details>
<summary><b>Raw / Paper</b></summary>

| Method                  | Return            | Description                       |
| ----------------------- | ----------------- | --------------------------------- |
| `sendRAWData(data)`     | `Promise<void>`   | Send raw ESC/POS byte array       |
| `lineWrap(lines)`       | `Promise<void>`   | Feed paper by N lines             |
| `cutPaper()`            | `Promise<void>`   | Cut paper (if cutter available)   |
| `autoOutPaper()`        | `Promise<void>`   | Auto feed paper to tear position  |
| `getCutPaperTimes()`    | `Promise<number>` | Get total cutter usage count      |
| `getPrinterBBMDistance()` | `Promise<number>` | Get black mark paper feed distance |

</details>

<details>
<summary><b>Cash Drawer</b></summary>

| Method               | Return            | Description                         |
| -------------------- | ----------------- | ----------------------------------- |
| `openDrawer()`       | `Promise<void>`   | Open cash drawer (if available)     |
| `getDrawerStatus()`  | `Promise<number>` | Drawer state (0=closed, 1=open)     |
| `getOpenDrawerTimes()` | `Promise<number>` | Total drawer open count             |

</details>

<details>
<summary><b>Label</b></summary>

| Method          | Description                                      |
| --------------- | ------------------------------------------------ |
| `labelLocate()` | Position the next label (set device to Label Mode first) |
| `labelOutput()` | Output the label to the cutting position          |

> **Note:** Enable Label Mode in device settings: Print Settings > Built-in Setting > Printer Mode > Label Mode.

</details>

<details>
<summary><b>LCD Customer Display</b></summary>

| Method                                    | Description                                         |
| ----------------------------------------- | --------------------------------------------------- |
| `sendLCDCommand(flag)`                    | LCD command (1=init, 2=wake, 3=sleep, 4=clear)      |
| `sendLCDString(text)`                     | Display single-line text on LCD                     |
| `sendLCDDoubleString(topText, bottomText)` | Display two lines on LCD                           |
| `sendLCDFillString(text, size, fill)`     | Display text with custom size (fill=stretch to fit) |
| `sendLCDMultiString(texts, align)`        | Display multi-line text (auto-sized by weight)      |
| `sendLCDBitmap(base64)`                   | Display image on LCD (max 128x40px)                 |

> Only available on mini-series desktop devices with a customer display (T1mini, T2mini, etc.).

</details>

<details>
<summary><b>Transaction</b></summary>

| Method                                  | Description                                  |
| --------------------------------------- | -------------------------------------------- |
| `enterPrinterBuffer(clean)`             | Start buffering commands                     |
| `exitPrinterBuffer(commit)`             | Exit buffer (commit=true to print)           |
| `commitPrinterBuffer()`                 | Print buffer contents (stays in buffer mode) |
| `exitPrinterBufferWithCallback(commit)` | Exit buffer with print result callback       |
| `commitPrinterBufferWithCallback()`     | Commit buffer with print result callback     |

</details>

---

## Supported Devices

| Device              | Android | Status    |
| ------------------- | ------- | --------- |
| Sunmi V2S           | 11      | Supported |
| Sunmi V2S Plus      | 11      | Supported |
| Newer Sunmi devices | 11+     | Supported |

---

## Sponsors

Support this project by becoming a sponsor. Your logo will show up here with a link to your website.

[![Sponsor](https://img.shields.io/badge/Sponsor-GitHub%20Sponsors-EA4AAA.svg?style=flat&logo=githubsponsors&logoColor=white)](https://github.com/sponsors/ngxhuyhoang)

<a href="https://buymeacoffee.com/hoangnh0099" target="_blank"><img src="https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png" alt="Buy Me a Beer" height="40"></a>

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

[MIT](LICENSE)
