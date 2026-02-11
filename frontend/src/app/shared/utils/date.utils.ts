export function toApiDate(value: Date): string {
  return value.toISOString().slice(0, 10);
}

export function todayApiDate(): string {
  return toApiDate(new Date());
}
