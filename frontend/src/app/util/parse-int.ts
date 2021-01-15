/**
 * Parses a string into a number
 * @param str the number as a stirng
 * @returns a number or NaN if the string is not a valid number
 */
export function parsePositiveInteger(str: string): number {
  const num: number = parseInt(str, 10);

  // the secound check is necessary because parseInt ignores all chars after the first
  // valid number, e.g '43dd' would parse to 43
  if (isNaN(num) || num.toString() !== str || num < 0) {
    return NaN;
  }
  return num;
}
