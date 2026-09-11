import { NativeModules, View } from 'react-native';
import { Button } from '../../components/button';
import NavigationMapView from '../../config/NavigationMapView';
import { useEffect, useState } from 'react';
import { NavigationManeuver, NavigationTripProgress } from '../../interfaces';
import { subscribeNavigationManeuvers } from '../../config/NavigationEvents';
import { subscribeNavigationTripProgress } from '../../config/NavigationTripProgress';
import { ManeuverComponent } from './maneuver';
const { Navigation } = NativeModules;

export function NavigationScreen() {
  const [maneuvers, setManeuvers] = useState<NavigationManeuver[]>([]);
  const [tripProgress, setTripProgress] =
    useState<NavigationTripProgress | null>(null);

  useEffect(() => {
    Navigation.initializeNavigation();
  }, []);

  useEffect(() => {
    return subscribeNavigationManeuvers(maneuvers => {
      console.log('manobras RN:', maneuvers);
      console.log('teste RN:', maneuvers[2]);

      setManeuvers(maneuvers);
    });
  }, []);

  useEffect(() => {
    return subscribeNavigationTripProgress(progress => {
      console.log('TRIP PROGRESS RN:', progress);

      setTripProgress(progress);
    });
  }, []);

  return (
    <View className="flex-1">
      <View className="h-[10%] bg-pink-500"></View>
      <View className="h-[15%] bg-red-500">
        <ManeuverComponent
          text={maneuvers[0]?.text}
          distanceRemaining={maneuvers[0]?.distanceRemaining}
        ></ManeuverComponent>
      </View>

      <View className="h-[70%] bg-blue-500">
        <NavigationMapView
          style={{
            position: 'absolute',
            top: 0,
            left: 0,
            right: 0,
            bottom: 0,
          }}
        ></NavigationMapView>
      </View>

      <View className="h-[5%] bg-green-500 items-center justify-center">
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
