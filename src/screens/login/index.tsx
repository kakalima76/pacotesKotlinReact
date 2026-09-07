//@ts-ignore
import '../../../global.css';
import React from 'react';
import { View, TextInput } from 'react-native';
import { Button } from '../../components/button';
import { useNavigation } from '@react-navigation/native';

export function LoginScreen() {
  const navigation = useNavigation<any>();

  return (
    <View className="flex-1 items-center justify-center px-6">
      <View className="w-full max-w-sm gap-4">
        <TextInput
          placeholder="Usuário"
          placeholderTextColor="#999"
          className="w-full rounded-lg border border-gray-300 px-4 py-3 text-base"
          autoCapitalize="none"
        />

        <TextInput
          placeholder="Senha"
          placeholderTextColor="#999"
          className="w-full rounded-lg border border-gray-300 px-4 py-3 text-base"
          secureTextEntry
        />

        <Button title="Entrar" onPress={() => navigation.navigate('Home')} />
      </View>
    </View>
  );
}
