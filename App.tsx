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

  useEffect(() => {

      }, [])

  
  return (
    <SafeAreaProvider>
      <StatusBar barStyle={isDarkMode ? 'light-content' : 'dark-content'} />
      <AppContent />
    </SafeAreaProvider>
  );
}

function AppContent() {
  const [resultado, setResultado] = useState<number | null>(null);


  useEffect(() => {
  NativeModules.PersonsFunctions.somar(200, 400)
    .then((valor: number) => {
      setResultado(valor);
    })
    .catch((erro: unknown) => {
      console.error('Erro ao chamar Kotlin:', erro);
    });
}, []);

  return (
    <View style={styles.container}>
      <Text style={styles.text}>
        Resultado : {resultado}
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
