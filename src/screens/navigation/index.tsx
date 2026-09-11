import {
  NativeModules,
  View,
  ViewStyle,
  requireNativeComponent,
} from 'react-native';
import { Button } from '../../components/button';
import NavigationMapView from '../../config/NavigationMapView';
import { useEffect } from 'react';
const { Navigation } = NativeModules;

export function NavigationScreen() {
  useEffect(() => {
    Navigation.initializeNavigation();
  }, []);

  return (
    <View className="flex-1">
      <NavigationMapView
        style={{
          position: 'absolute',
          top: 0,
          left: 0,
          right: 0,
          bottom: '10%',
        }}
      ></NavigationMapView>

      <View className="absolute bottom-10 left-0 right-0 items-center">
        <Button
          title="Testar Rota"
          onPress={() => {
            Navigation?.setRoute?.(
              -22.967161,
              -42.974734,
              -22.94911846379087,
              -42.98114469339183,
            );
          }}
        />
      </View>
    </View>
  );
}
