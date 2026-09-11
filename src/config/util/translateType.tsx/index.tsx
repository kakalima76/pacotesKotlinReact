import { ManeuverType } from '../../../types';

/**
 * Traduz o tipo de uma manobra de navegação
 * para uma descrição em português.
 *
 * @param type - Tipo da manobra
 * @returns Descrição do tipo da manobra em português
 */
export function translateType(type: ManeuverType): string {
  switch (type) {
    // Conversão
    case 'turn':
      return 'conversão';

    // Chegada ao destino
    case 'arrive':
      return 'chegada';

    // Saída ou início da navegação
    case 'depart':
      return 'partida';

    // Continuar seguindo pela via
    case 'continue':
      return 'continue';

    // Junção com outra via
    case 'merge':
      return 'junção';

    // Entrada em uma rotatória
    case 'roundabout':
      return 'rotatória';

    // Bifurcação da via
    case 'fork':
      return 'bifurcação';

    // Entrada em uma via de acesso
    case 'on ramp':
      return 'entrada';

    // Saída de uma via de acesso
    case 'off ramp':
      return 'saída';

    // Caso o tipo não esteja previsto
    default:
      return '';
  }
}
