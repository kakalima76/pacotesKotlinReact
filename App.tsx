import React from 'react';
import { SafeAreaProvider } from 'react-native-safe-area-context';
import { MainStack } from './src/router';
import { MainProvider } from './src/contexts';
import { NativeModules } from 'react-native';
const { Auth } = NativeModules;
Auth.login('nieraldo', String(123456));

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
