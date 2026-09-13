import { Text, View } from 'react-native';
import { translateInstruction } from '../../../util/translateInstructions';

interface IManeuver {
  type?: string | null;
  modifier?: string | null;
  distanceRemaining?: number | null;
  durationRemaining?: number | null;
  nextRoadName?: string | null;
}

export function ManeuverComponent({
  type,
  modifier,
  distanceRemaining,
  durationRemaining,
  nextRoadName,
}: IManeuver) {
  const direction = translateInstruction(type ?? '', modifier ?? '');

  if (durationRemaining == null) {
    return <View className="flex-1" />;
  }

  if (durationRemaining > 0) {
    return (
      <View className="flex-1 bg-gray-900">
        <View className="flex-1 flex-row">
          <View className="w-1/4 items-center justify-center">
            <Text className="text-7xl text-white font-bold">
              {direction.symbol}
            </Text>
          </View>

          <View className="flex-1 items-center justify-center">
            <Text className="text-2xl text-white font-bold">
              {nextRoadName}
            </Text>
          </View>
        </View>

        <View className="flex-1 flex-row">
          <View className="w-1/4 items-center justify-center">
            <Text className="text-3xl text-white font-bold">
              {`${distanceRemaining?.toFixed(0)}`}
            </Text>
          </View>
          <View className="flex-1 items-center justify-center">
            <Text className="text-xl text-blue-500 font-bold">
              {direction.text}
            </Text>
          </View>
        </View>
      </View>
    );
  }

  return (
    <View className="flex-1 items-center justify-center">
      <Text className="text-3xl">Você chegou</Text>
    </View>
  );
}
