import React from 'react';
import { SafeAreaProvider } from 'react-native-safe-area-context';
import { MainStack } from './src/router';
import { MainProvider } from './src/contexts';

function App() {
  return (
    <MainProvider>
      <SafeAreaProvider>
        <MainStack />
      </SafeAreaProvider>
    </MainProvider>
  );
}

export default App;
