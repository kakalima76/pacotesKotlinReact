import { NewAppScreen } from '@react-native/new-app-screen';
import { useEffect , useState} from 'react';
import {
  NativeModules,
  StatusBar,
  StyleSheet,
  useColorScheme,
  View,
  Text,

} from 'react-native';
import {
  SafeAreaProvider,
  useSafeAreaInsets,
} from 'react-native-safe-area-context';



function App() {
  const isDarkMode = useColorScheme() === 'dark';


  return (
    <SafeAreaProvider>
      <StatusBar barStyle={isDarkMode ? 'light-content' : 'dark-content'} />
      <AppContent />
    </SafeAreaProvider>
  );
}

function AppContent() {


  return (
    <View style={styles.container}>
      <Text style={styles.text}>
        Resultado :
      </Text>
    </View>
  );
}


const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  text: {
    fontSize: 30,
  },
});


export default App;
