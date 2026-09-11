import { ManeuverModifier } from '../../../types';

/**
 * Traduz o modificador de uma manobra de navegação
 * para uma descrição em português.
 *
 * @param modifier - Modificador da manobra
 * @returns Descrição do modificador em português
 */
export function translateModifier(modifier: ManeuverModifier): string {
  switch (modifier) {
    // Conversão para a esquerda
    case 'left':
      return 'á esquerda';

    // Conversão para a direita
    case 'right':
      return 'á direita';

    // Seguir reto
    case 'straight':
      return 'em frente';

    // Pequena conversão para a esquerda
    case 'slight left':
      return 'levemente à esquerda';

    // Pequena conversão para a direita
    case 'slight right':
      return 'levemente à direita';

    // Curva fechada para a esquerda
    case 'sharp left':
      return 'curva fechada à esquerda';

    // Curva fechada para a direita
    case 'sharp right':
      return 'curva fechada à direita';

    // Retorno em U
    case 'uturn':
      return 'retorno';

    // Caso o modificador não esteja previsto
    default:
      return '';
  }
}
