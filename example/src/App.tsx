import { Text, View, StyleSheet, Button, ScrollView, Alert } from 'react-native';
import {
  updatePrinterState,
  getPrinterSerialNo,
  getPrinterModal,
  printText,
  lineWrap,
} from 'react-native-sunmi-printer';
import { useState } from 'react';

export default function App() {
  const [status, setStatus] = useState<string>('Not connected');

  const checkPrinter = async () => {
    try {
      const state = await updatePrinterState();
      const serial = await getPrinterSerialNo();
      const model = await getPrinterModal();
      setStatus(`State: ${state}, Serial: ${serial}, Model: ${model}`);
    } catch (e: any) {
      Alert.alert('Error', e.message);
    }
  };

  const testPrintText = async () => {
    try {
      await printText('Hello from react-native-sunmi-printer!\n');
      await lineWrap(3);
    } catch (e: any) {
      Alert.alert('Error', e.message);
    }
  };

  return (
    <ScrollView contentContainerStyle={styles.container}>
      <Text style={styles.title}>Sunmi Printer Test</Text>
      <Text style={styles.status}>{status}</Text>
      <View style={styles.buttons}>
        <Button title="Check Printer" onPress={checkPrinter} />
        <Button title="Print Text" onPress={testPrintText} />
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    padding: 20,
  },
  title: {
    fontSize: 20,
    fontWeight: 'bold',
    marginBottom: 10,
  },
  status: {
    fontSize: 14,
    marginBottom: 20,
    textAlign: 'center',
  },
  buttons: {
    gap: 10,
  },
});
