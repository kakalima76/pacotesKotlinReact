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
              -22.967161,
              -42.974734,
              -22.956268671822837,
              -42.98874803674782,
            );
          }}
        />
      </View>
    </View>
  );
}
