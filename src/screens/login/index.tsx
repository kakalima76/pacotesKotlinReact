//@ts-ignore
import '../../../global.css';
import React, { useState } from 'react';
import { NativeModules, View, TextInput } from 'react-native';
import { Button } from '../../components/button';
import { getValidAccessToken } from '../../services/auth';

const { Auth } = NativeModules;

export function LoginScreen() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');

  const handleLogin = async () => {
    console.log(username, password);

    await Auth.login(username, password);

    const accessToken = await getValidAccessToken();
    console.log(accessToken);
  };

  return (
    <View className="flex-1 items-center justify-center px-6">
      <View className="w-full max-w-sm gap-4">
        <TextInput
          value={username}
          onChangeText={setUsername}
          placeholder="Usuário"
          placeholderTextColor="#999"
          className="w-full rounded-lg border border-gray-300 px-4 py-3 text-base"
          autoCapitalize="none"
        />

        <TextInput
          value={password}
          onChangeText={setPassword}
          placeholder="Senha"
          placeholderTextColor="#999"
          className="w-full rounded-lg border border-gray-300 px-4 py-3 text-base"
          secureTextEntry
        />

        <Button title="Entrar" onPress={handleLogin} />
      </View>
    </View>
  );
}
