export const ManeuverType = {
  TURN: 'turn',
  ARRIVE: 'arrive',
  DEPART: 'depart',
  CONTINUE: 'continue',
  MERGE: 'merge',
  ROUNDABOUT: 'roundabout',
  FORK: 'fork',
  ON_RAMP: 'on ramp',
  OFF_RAMP: 'off ramp',
} as const;

export type ManeuverType = (typeof ManeuverType)[keyof typeof ManeuverType];

export const ManeuverModifier = {
  LEFT: 'left',
  RIGHT: 'right',
  STRAIGHT: 'straight',
  SLIGHT_LEFT: 'slight left',
  SLIGHT_RIGHT: 'slight right',
  SHARP_LEFT: 'sharp left',
  SHARP_RIGHT: 'sharp right',
  UTURN: 'uturn',
} as const;

export type ManeuverModifier =
  (typeof ManeuverModifier)[keyof typeof ManeuverModifier];
