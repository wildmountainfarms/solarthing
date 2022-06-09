

export function getTimeMillisRounded() {
  const currentTimeMillis = Date.now();
  return Math.ceil(currentTimeMillis / 10_000) * 10_000;
}
