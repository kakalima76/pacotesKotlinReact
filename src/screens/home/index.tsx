import React from 'react';
import { Text, View } from 'react-native';
import { useMainContext } from '../../contexts';

export function HomeScree() {
  const { location } = useMainContext();

  return (
    <View className="flex-1 items-center justify-center">
      <Text className="text-3xl bg-red-600 p-4 rounded-full text-white">
        {location?.latitude}
      </Text>
    </View>
  );
}
