//@ts-ignore
import '../../../global.css';

import React, { useState } from 'react';
import { Text, TextInput, TouchableOpacity, View } from 'react-native';

import { Button } from '../../components/button';
import { useAuth } from '../../contexts/auth';

export function LoginScreen() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [message, setMessage] = useState<string | null>(null);

  const { login } = useAuth();

  const handleLogin = async () => {
    setMessage('');

    const r = await login(username, password);

    !r ? setMessage('credenciais inválidas') : setMessage('');
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

        <View className="w-full flex-row items-center rounded-lg border border-gray-300">
          <TextInput
            value={password}
            onChangeText={setPassword}
            placeholder="Senha"
            placeholderTextColor="#999"
            secureTextEntry={!showPassword}
            className="flex-1 px-4 py-3 text-base"
          />

          <TouchableOpacity
            onPress={() => setShowPassword(!showPassword)}
            className="px-4 py-3"
          >
            <Text className="text-base">
              {showPassword ? 'Ocultar' : 'Mostrar'}
            </Text>
          </TouchableOpacity>
        </View>

        {message && (
          <Text className="text-center text-xl text-red-600">{message}</Text>
        )}

        <Button title="Entrar" onPress={handleLogin} />
      </View>
    </View>
  );
}
