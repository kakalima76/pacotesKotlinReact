import { DeviceEventEmitter } from 'react-native';
import { NavigationManeuver } from '../interfaces';

export function subscribeNavigationManeuvers(
  callback: (maneuvers: NavigationManeuver[]) => void,
) {
  const subscription = DeviceEventEmitter.addListener(
    'navigationManeuvers',
    callback,
  );

  return () => {
    subscription.remove();
  };
}
