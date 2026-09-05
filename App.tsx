import "./global.css"
import { NewAppScreen } from '@react-native/new-app-screen';
import { useEffect , useState} from 'react';
import {
  NativeModules,
  NativeEventEmitter,
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

const { GpsService } = NativeModules;

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
const [location, setLocation] = useState();

  useEffect(() => {
    const gpsEmitter = new NativeEventEmitter(GpsService);

    const subscription = gpsEmitter.addListener(
      'gpsLocation',
      (location) => {
          console.log(location);
        setLocation({
          latitude: location.latitude,
          longitude: location.longitude,
        });
      }
    );

    return () => {
      subscription.remove();
    };
  }, []);


  return (
    <View style={styles.container}>
      <Text className="text-red-500 text-3xl">
       {location?.latitude}
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
