import { ActivityEntryResponse } from '../../models/worklog.model';
import { DaySummaryComponent } from './day-summary.component';

const item = { id: 1, name: 'Item' };

function entry(overrides: Partial<ActivityEntryResponse>): ActivityEntryResponse {
  return {
    id: 1,
    program: item,
    team: item,
    language: item,
    activity: item,
    subactivity: { ...item, activityId: 1 },
    startedAt: '2026-03-10T08:00:00Z',
    finishedAt: null,
    durationSeconds: null,
    note: null,
    ...overrides,
  };
}

describe('DaySummaryComponent', () => {
  // Not rendered, so the component's 1-second timer is never started.
  let component: DaySummaryComponent;

  beforeEach(() => {
    component = new DaySummaryComponent();
  });

  it('counts the activities', () => {
    component.entries = [entry({ id: 1 }), entry({ id: 2 })];

    expect(component.totalActivities).toBe(2);
  });

  it('sums the durations of finished activities', () => {
    component.entries = [
      entry({ id: 1, finishedAt: '2026-03-10T09:00:00Z', durationSeconds: 3600 }),
      entry({ id: 2, finishedAt: '2026-03-10T09:30:00Z', durationSeconds: 1800 }),
    ];

    expect(component.totalSeconds).toBe(5400);
  });

  it('adds the elapsed time of the running activity', () => {
    component.nowEpoch = Date.parse('2026-03-10T10:00:00Z');
    component.entries = [
      entry({ id: 1, finishedAt: '2026-03-10T09:00:00Z', durationSeconds: 600 }),
      entry({ id: 2, startedAt: '2026-03-10T09:59:15Z' }),
    ];

    expect(component.totalSeconds).toBe(600 + 45);
  });

  it('never returns a negative elapsed time', () => {
    component.nowEpoch = Date.parse('2026-03-10T08:00:00Z');
    component.entries = [entry({ startedAt: '2026-03-10T09:00:00Z' })];

    expect(component.totalSeconds).toBe(0);
  });
});
