import { Text, View } from 'react-native';

interface IManeuver {
  text: string | undefined | null;
  distanceRemaining: number | undefined | null;
}

export function ManeuverComponent({ text, distanceRemaining }: IManeuver) {
  return (
    <View className="flex-1">
      <View className="flex-1 items-center justify-center bg-yellow-500">
        {/* Linha 1 */}
        <Text>{text}</Text>
        <Text>{distanceRemaining}</Text>
      </View>

      <View className="flex-1 bg-blue-500">{/* Linha 2 */}</View>
    </View>
  );
}
