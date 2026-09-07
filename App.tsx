import React from 'react';
import { SafeAreaProvider } from 'react-native-safe-area-context';
import { MainStack } from './src/router';

function App() {
  return (
    <SafeAreaProvider>
      <MainStack />
    </SafeAreaProvider>
  );
}

export default App;
