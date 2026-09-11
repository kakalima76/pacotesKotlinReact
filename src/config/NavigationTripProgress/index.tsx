import { DeviceEventEmitter } from 'react-native';
import { NavigationTripProgress } from '../../interfaces';

/**
 * Inscreve um callback para receber o progresso da viagem.
 *
 * @param callback - Função chamada sempre que o progresso for atualizado
 * @returns Função para cancelar a inscrição
 */
export function subscribeNavigationTripProgress(
  callback: (tripProgress: NavigationTripProgress) => void,
) {
  const subscription = DeviceEventEmitter.addListener(
    'navigationTripProgress',
    callback,
  );

  return () => {
    subscription.remove();
  };
}
