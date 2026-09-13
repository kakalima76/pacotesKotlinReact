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
  secondaryText: string | null;
  secondaryType: string | null;
  secondaryModifier: string | null;
  subText: string | null;
  subType: string | null;
  subModifier: string | null;
  distanceRemaining: number | null;
  totalDistance: number;
  latitude: number;
  longitude: number;
  nextRoadName: string | null; // ← campo que vem do Kotlin
}

export interface NavigationTripProgress {
  distanceRemaining: number;
  durationRemaining: number;
}
