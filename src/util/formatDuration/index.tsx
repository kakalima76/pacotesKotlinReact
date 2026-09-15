/**
 * Formats a duration in seconds into a human-readable time string.
 * - Values <= 6 seconds: returns "Xs" (e.g., "5s")
 * - Values > 6 seconds and < 1 minute: returns rounded seconds (e.g., "45s")
 * - Values >= 1 minute and < 1 hour: returns "Xmin Ymin" (e.g., "12min 30seg")
 * - Values >= 1 hour: returns "Xh Ymin" (e.g., "1h 15min")
 * @param seconds - Duration in seconds
 * @returns Formatted string with the appropriate time unit
 */
export function formatDuration(seconds: number): string {
  if (seconds <= 6) {
    return `${Math.round(seconds)}s`;
  }

  if (seconds < 60) {
    return `${Math.round(seconds)}s`;
  }

  const totalMinutes = Math.round(seconds / 60);
  const hours = Math.floor(totalMinutes / 60);
  const minutes = totalMinutes % 60;

  if (hours > 0) {
    return minutes > 0 ? `${hours}h ${minutes}min` : `${hours}h`;
  }

  return `${minutes}min`;
}
