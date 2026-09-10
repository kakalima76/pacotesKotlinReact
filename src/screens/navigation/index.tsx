import {
  NativeModules,
  View,
  ViewStyle,
  requireNativeComponent,
} from 'react-native';
import { Button } from '../../components/button';
import NavigationMapView from '../../config/NavigationMapView';
const { Navigation } = NativeModules;

export function NavigationScreen() {
  console.log('Navigation:', Navigation);
  console.log('startNavigation:', Navigation?.startNavigation);

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
          title="Iniciar Navigation"
          onPress={() => {
            console.log('Navigation:', Navigation);
            console.log('startNavigation:', Navigation?.startNavigation);

            Navigation?.startNavigation?.();
          }}
        />

        <Button
          title="Testar Rota"
          onPress={() => {
            Navigation?.setRoute?.(
              -22.9647162,
              -42.9292636,
              -22.963879,
              -42.995022,
            );
          }}
        />
      </View>
    </View>
  );
}
