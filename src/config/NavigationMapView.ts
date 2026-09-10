import { requireNativeComponent, ViewStyle } from 'react-native';

export interface NavigationMapViewProps {
  style?: ViewStyle;
}

const NavigationMapView =
  requireNativeComponent<NavigationMapViewProps>('NavigationMapView');

export default NavigationMapView;
