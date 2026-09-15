import { NativeModules } from 'react-native';

const { Auth } = NativeModules;

export async function getValidAccessToken(): Promise<string> {
  return Auth.getValidAccessToken();
}
