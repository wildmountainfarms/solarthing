

export function getTimeMillisRounded() {
  // NOTE: When we use this, it's a little bit hacky.
  //   First off, we still have to explicitly tell our client to have a refetch interval because this is not a hook.
  //   Second off, if that fetch takes longer than 10 seconds, this will update in that time, causing an immediate refetch.
  const currentTimeMillis = Date.now();
  return Math.ceil(currentTimeMillis / 10_000) * 10_000;
}
