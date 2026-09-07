import React from 'react';
import { Pressable, Text } from 'react-native';

interface ButtonProps {
  title: string;
  onPress: () => void;
}

export function Button({ title, onPress }: ButtonProps) {
  return (
    <Pressable
      onPress={onPress}
      className="w-full items-center justify-center rounded-lg bg-blue-600 px-5 py-3"
    >
      <Text className="text-base font-semibold text-white">{title}</Text>
    </Pressable>
  );
}
