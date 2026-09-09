import Mapbox from '@rnmapbox/maps';
import { Image, View } from 'react-native';
import { MAPBOX_ACCESS_TOKEN } from '../../config/mapbox';
import { useMainContext } from '../../contexts';


Mapbox.setAccessToken(MAPBOX_ACCESS_TOKEN);

export function HomeScreen() {
  const { location } = useMainContext();

  if (!location) {
    return <View className="flex-1" />;
  }

  return (
    <View className="flex-1">
      <Mapbox.MapView style={{ flex: 1 }}>
        <Mapbox.Camera
          centerCoordinate={[location.longitude, location.latitude]}
          zoomLevel={15}
        />

        <Mapbox.MarkerView
          id="vehicle"
          coordinate={[location.longitude, location.latitude]}
        >
          <Image
            source={require('../../assets/car.png')}
            style={{
              width: 72,
              height: 72,
            }}
            resizeMode="contain"
          />
        </Mapbox.MarkerView>
      </Mapbox.MapView>
    </View>
  );
}
