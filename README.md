# @hoangnh0099/react-native-sunmi-printer

[![npm version](https://img.shields.io/badge/npm-v0.1.0-CB3837.svg?style=flat&logo=npm)](https://www.npmjs.com/package/@hoangnh0099/react-native-sunmi-printer)
[![license](https://img.shields.io/badge/license-MIT-blue.svg?style=flat)](https://github.com/ngxhuyhoang/react-native-sunmi-printer/blob/main/LICENSE)
[![platform](https://img.shields.io/badge/platform-Android-3DDC84.svg?style=flat&logo=android&logoColor=white)](#supported-devices)
[![react-native](https://img.shields.io/badge/React%20Native-New%20Architecture-61DAFB.svg?style=flat&logo=react&logoColor=white)](#)

React Native library for Sunmi built-in printers. Supports Sunmi V2S and newer devices (Android 11+).

Built with **React Native New Architecture** (TurboModule).

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

### Print QR Code

```js
import { printQRCode, lineWrap } from '@hoangnh0099/react-native-sunmi-printer';

// printQRCode(data, moduleSize, errorLevel)
// moduleSize: 1-16
// errorLevel: 0=L(7%), 1=M(15%), 2=Q(25%), 3=H(30%)
await printQRCode('https://example.com', 8, 2);
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
| `getPrinterPaper()`    | `Promise<number>` | Paper size (1=58mm, other=80mm)        |
| `getPrinterMode()`     | `Promise<number>` | Mode (0=normal, 1=black mark, 2=label) |
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
| `setFontName(typeface)`       | Set font (`"gh"` for built-in monospace)  |
| `setFontSize(fontsize)`       | Set font size                             |
| `setPrinterStyle(key, value)` | Set printer style (bold, underline, etc.) |

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
| `printBitmapCustom(base64, type)` | Print with mode (0=default, 1=black&white, 2=grayscale) |

> Max width: 384px (58mm) or 576px (80mm). Call `lineWrap()` after printing to feed paper.

</details>

<details>
<summary><b>Barcode</b></summary>

| Method                                                       | Description   |
| ------------------------------------------------------------ | ------------- |
| `printBarCode(data, symbology, height, width, textPosition)` | Print barcode |
| `printQRCode(data, moduleSize, errorLevel)`                  | Print QR code |

</details>

<details>
<summary><b>Table</b></summary>

| Method                                      | Description                                    |
| ------------------------------------------- | ---------------------------------------------- |
| `printColumnsText(texts, widths, aligns)`   | Print columns (widths in character count)      |
| `printColumnsString(texts, widths, aligns)` | Print columns (widths as proportional weights) |

</details>

<details>
<summary><b>Raw / Paper</b></summary>

| Method              | Description                      |
| ------------------- | -------------------------------- |
| `sendRAWData(data)` | Send raw ESC/POS byte array      |
| `lineWrap(lines)`   | Feed paper by N lines            |
| `cutPaper()`        | Cut paper (if cutter available)  |
| `autoOutPaper()`    | Auto feed paper to tear position |

</details>

<details>
<summary><b>Cash Drawer</b></summary>

| Method         | Description                     |
| -------------- | ------------------------------- |
| `openDrawer()` | Open cash drawer (if available) |

</details>

<details>
<summary><b>Label</b></summary>

| Method          | Description                      |
| --------------- | -------------------------------- |
| `labelLocate()` | Locate label position            |
| `labelOutput()` | Push label to cutter for peeling |

</details>

<details>
<summary><b>LCD Customer Display</b></summary>

| Method                                | Description                                |
| ------------------------------------- | ------------------------------------------ |
| `sendLCDCommand(flag)`                | LCD command (1=init, 2=on, 3=off, 4=clear) |
| `sendLCDFillString(text, size, fill)` | Display text on LCD                        |
| `sendLCDMultiString(texts, align)`    | Display multi-line text on LCD             |
| `sendLCDBitmap(base64)`               | Display image on LCD                       |

</details>

<details>
<summary><b>Transaction</b></summary>

| Method                      | Description                                  |
| --------------------------- | -------------------------------------------- |
| `enterPrinterBuffer(clean)` | Start buffering commands                     |
| `exitPrinterBuffer(commit)` | Exit buffer (commit=true to print)           |
| `commitPrinterBuffer()`     | Print buffer contents (stays in buffer mode) |

</details>

---

## Supported Devices

| Device              | Android | Status    |
| ------------------- | ------- | --------- |
| Sunmi V2S           | 11      | Supported |
| Sunmi V2S Plus      | 11      | Supported |
| Newer Sunmi devices | 11+     | Supported |

---

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

[MIT](LICENSE)

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
