export interface ManeuverInstruction {
  text: string;
  symbol: string;
}

export function translateInstruction(
  type: string | null,
  modifier: string | null,
): ManeuverInstruction {
  if (type === 'turn') {
    switch (modifier) {
      case 'left':
        return { text: 'Vire à esquerda', symbol: '↰' };
      case 'right':
        return { text: 'Vire à direita', symbol: '↱' };
      case 'straight':
        return { text: 'Siga em frente', symbol: '↑' };
      case 'slight left':
        return { text: 'Vire levemente à esquerda', symbol: '↖' };
      case 'slight right':
        return { text: 'Vire levemente à direita', symbol: '↗' };
      case 'sharp left':
        return { text: 'Faça uma curva fechada à esquerda', symbol: '↰' };
      case 'sharp right':
        return { text: 'Faça uma curva fechada à direita', symbol: '↱' };
      case 'uturn':
        return { text: 'Faça um retorno', symbol: '↩' };
    }
  }

  if (type === 'continue') {
    return { text: 'Siga em frente', symbol: '↑' };
  }

  if (type === 'roundabout') {
    return { text: 'Entre na rotatória', symbol: '⟳' };
  }

  if (type === 'fork') {
    switch (modifier) {
      case 'left':
        return { text: 'Pegue a bifurcação à esquerda', symbol: '↖' };
      case 'right':
        return { text: 'Pegue a bifurcação à direita', symbol: '↗' };
    }
  }

  if (type === 'merge') {
    return { text: 'Entre na faixa', symbol: '⤴' };
  }

  if (type === 'on ramp') {
    return { text: 'Entre na via de acesso', symbol: '↗' };
  }

  if (type === 'off ramp') {
    return { text: 'Saia pela via de acesso', symbol: '↘' };
  }

  if (type === 'depart') {
    return { text: 'Siga em frente', symbol: '◉' };
  }

  if (type === 'arrive') {
    return { text: 'Você chegou ao destino', symbol: '⌖' };
  }

  return { text: 'Siga em frente', symbol: '↑' };
}
