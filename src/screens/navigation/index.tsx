import { DeviceEventEmitter, NativeModules, View } from 'react-native';
import { Button } from '../../components/button';
import NavigationMapView from '../../config/NavigationMapView';
import { SetStateAction, useEffect, useState } from 'react';
import { NavigationManeuver, NavigationTripProgress } from '../../interfaces';
import { subscribeNavigationManeuvers } from '../../config/NavigationEvents';
import { ManeuverComponent } from './maneuver';
import { subscribeNavigationTripProgress } from '../../util/subscribeNavigationTripProgress';
import { useMainContext } from '../../contexts';
const { Navigation } = NativeModules;

export function NavigationScreen() {
  const [maneuver, setManeuver] = useState<NavigationManeuver | null>(null);
  const [nextRoadName, setNextRoadName] = useState<string | undefined | null>(
    null,
  );
  const [tripProgress, setTripProgress] =
    useState<NavigationTripProgress | null>(null);
  const { location } = useMainContext();

  useEffect(() => {
    Navigation.initializeNavigation();
  }, []);

  useEffect(() => {
    setManeuver(null);
    return subscribeNavigationManeuvers(maneuvers => {
      setManeuver(maneuvers[0]);
    });
  }, []);

  useEffect(() => {
    setTripProgress(null);
    return subscribeNavigationTripProgress((progress: any) => {
      setTripProgress(progress);
    });
  }, []);

  useEffect(() => {
    const subscription = DeviceEventEmitter.addListener(
      'navigationManeuvers', // ← corrigido
      (maneuvers: NavigationManeuver[]) => {
        if (maneuvers.length > 0) {
          const maneuver = maneuvers[0];
          console.log('NextRoad', maneuver.nextRoadName);
          setNextRoadName(maneuver.nextRoadName);
        }
      },
    );

    return () => {
      subscription.remove();
    };
  }, []);

  return (
    <View className="flex-1">
      <View className="h-[10%]"></View>
      <View className="h-[15%]">
        <ManeuverComponent
          type={maneuver?.type}
          modifier={maneuver?.modifier}
          distanceRemaining={maneuver?.distanceRemaining}
          durationRemaining={tripProgress?.durationRemaining}
          nextRoadName={nextRoadName}
          subModifier={maneuver?.subType}
          subType={maneuver?.subModifier}
          subText={maneuver?.subText}
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

//-22.967161,-42.974734,
