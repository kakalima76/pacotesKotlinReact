import { NativeModules, Text, View } from 'react-native';
import { Button } from '../../components/button';

const { Navigation } = NativeModules;

export function NavigationScreen() {
  console.log('Navigation:', Navigation);
  console.log('startNavigation:', Navigation?.startNavigation);

  return (
    <View className="flex-1 justify-center items-center">
      <Button
        title="Iniciar Navigation"
        onPress={() => {
          console.log('Navigation:', Navigation);
          console.log('startNavigation:', Navigation?.startNavigation);

          Navigation?.startNavigation?.();
        }}
      />
    </View>
  );
}
