/**
 * Converts a distance value from meters to the most suitable unit.
 * - Values below 1000: returns in meters (e.g., "500 m")
 * - Values 1000 or above: returns in kilometers with one decimal place (e.g., "1.5 km")
 * @param meters - Distance value in meters
 * @returns Formatted string with the appropriate unit
 */
export function formatDistance(meters: number): string {
  if (meters >= 1000) {
    const km = meters / 1000;
    return `${km.toFixed(1)} km`;
  }
  return `${Math.round(meters)} m`;
}
