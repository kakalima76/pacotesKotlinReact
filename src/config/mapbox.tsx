import Config from 'react-native-config';

const token = Config.MAPBOX_ACCESS_TOKEN;

if (!token) {
  throw new Error('MAPBOX_ACCESS_TOKEN não foi configurado no arquivo .env');
}

export const MAPBOX_ACCESS_TOKEN = token;
