import { DeviceEventEmitter, NativeModules, Text, View } from 'react-native';
import { Button } from '../../components/button';
import NavigationMapView from '../../config/NavigationMapView';
import { SetStateAction, useEffect, useState } from 'react';
import { NavigationManeuver, NavigationTripProgress } from '../../interfaces';
import { subscribeNavigationManeuvers } from '../../config/NavigationEvents';
import { ManeuverComponent } from './maneuver';
import { subscribeNavigationTripProgress } from '../../util/subscribeNavigationTripProgress';
import { useMainContext } from '../../contexts';
import { formatDistance } from '../../util/formatDistance';
import { formatDuration } from '../../util/formatDuration';
import { useNavigation, useRoute, RouteProp } from '@react-navigation/native';
type NavigationScreenRoute = RouteProp<
  { Navigation: INavigationComponent },
  'Navigation'
>;
const { Navigation } = NativeModules;

interface INavigationComponent {
  lat: number;
  lng: number;
}

export function NavigationScreen() {
  const [maneuver, setManeuver] = useState<NavigationManeuver | null>(null);
  const [nextRoadName, setNextRoadName] = useState<string | undefined | null>(
    null,
  );
  const [tripProgress, setTripProgress] =
    useState<NavigationTripProgress | null>(null);
  const { location } = useMainContext();
  const route = useRoute<NavigationScreenRoute>();
  const { lat, lng } = route.params;

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
    if (lat == null || lng == null) return;
    Navigation?.setRoute?.(-22.967161, -42.974734, lat, lng);
  }, [lat, lng]);

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

      <View className="h-[65%] bg-blue-500">
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

      <View className="flex-row h-[10%] bg-black flex ">
        <View className="flex-1 p-2 items-center justify-center">
          <Text className="text-white  text-2xl font-bold ">
            {`${formatDuration(Number(tripProgress?.durationRemaining ?? 0))}`}
          </Text>
        </View>

        <View className="flex-1 p-2 items-center justify-center">
          <Text className="text-white text-2xl font-bold ">
            {`${formatDistance(tripProgress?.distanceRemaining ?? 0)}`}
          </Text>
        </View>
      </View>
    </View>
  );
}

//-22.967161,-42.974734,
