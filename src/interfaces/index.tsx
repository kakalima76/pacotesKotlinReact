export interface GpsTelemetry {
  latitude: number;
  longitude: number;
  accuracy: number;
  altitude: number;
  speed: number;
  bearing: number;
  time: number;
}

export interface NavigationManeuver {
  id: string;
  text: string;
  type: string | null;
  modifier: string | null;
  distanceRemaining: number;
  totalDistance: number;
  latitude: number;
  longitude: number;
}

export interface NavigationTripProgress {
  distanceRemaining: number;
  durationRemaining: number;
}
