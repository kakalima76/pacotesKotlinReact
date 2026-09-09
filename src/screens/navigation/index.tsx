import {
  NativeModules,
  View,
  ViewStyle,
  requireNativeComponent,
} from 'react-native';
import { Button } from '../../components/button';
const { Navigation } = NativeModules;

interface NavigationMapViewProps {
  style?: ViewStyle;
}

const NavigationMapView =
  requireNativeComponent<NavigationMapViewProps>('NavigationMapView');

export function NavigationScreen() {
  console.log('Navigation:', Navigation);
  console.log('startNavigation:', Navigation?.startNavigation);

  return (
    <View className="flex-1">
      <NavigationMapView style={{ flex: 1 }} />

      <View className="absolute bottom-10 left-0 right-0 items-center">
        <Button
          title="Iniciar Navigation"
          onPress={() => {
            console.log('Navigation:', Navigation);
            console.log('startNavigation:', Navigation?.startNavigation);

            Navigation?.startNavigation?.();
          }}
        />
      </View>
    </View>
  );
}
