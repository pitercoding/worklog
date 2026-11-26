import { DurationPipe } from './duration.pipe';

describe('DurationPipe', () => {
  const pipe = new DurationPipe();

  it('formats seconds as HH:MM:SS', () => {
    expect(pipe.transform(0)).toBe('00:00:00');
    expect(pipe.transform(59)).toBe('00:00:59');
    expect(pipe.transform(61)).toBe('00:01:01');
    expect(pipe.transform(3661)).toBe('01:01:01');
  });

  it('supports durations longer than a day', () => {
    expect(pipe.transform(100 * 3600)).toBe('100:00:00');
  });

  it('ignores the fractional part', () => {
    expect(pipe.transform(59.9)).toBe('00:00:59');
  });

  it('falls back to zero for missing or invalid values', () => {
    expect(pipe.transform(null)).toBe('00:00:00');
    expect(pipe.transform(undefined)).toBe('00:00:00');
    expect(pipe.transform(NaN)).toBe('00:00:00');
    expect(pipe.transform(-5)).toBe('00:00:00');
  });
});
