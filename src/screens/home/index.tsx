import Mapbox from '@rnmapbox/maps';
import { View } from 'react-native';
import { MAPBOX_ACCESS_TOKEN } from '../../config/mapbox';

Mapbox.setAccessToken(MAPBOX_ACCESS_TOKEN);

export function HomeScreen() {
  return (
    <View className="flex-1">
      <Mapbox.MapView style={{ flex: 1 }} />
    </View>
  );
}
