import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivityEntryResponse } from '../../models/worklog.model';
import { ActivityTableComponent } from './activity-table.component';

const item = { id: 1, name: 'Item' };

function entry(id: number, startedAt: string, overrides: Partial<ActivityEntryResponse> = {}): ActivityEntryResponse {
  return {
    id,
    program: { id: 1, name: 'Orion Platform' },
    team: { id: 1, name: 'Core Backend' },
    language: { id: 1, name: 'Java' },
    activity: item,
    subactivity: { ...item, activityId: 1 },
    startedAt,
    finishedAt: null,
    durationSeconds: null,
    note: null,
    ...overrides,
  };
}

describe('ActivityTableComponent', () => {
  let fixture: ComponentFixture<ActivityTableComponent>;
  let element: HTMLElement;

  function render(entries: ActivityEntryResponse[]): void {
    fixture.componentRef.setInput('entries', entries);
    fixture.detectChanges();
  }

  beforeEach(() => {
    fixture = TestBed.createComponent(ActivityTableComponent);
    element = fixture.nativeElement;
  });

  it('shows the empty state when there are no activities', () => {
    render([]);

    expect(element.querySelector('table')).toBeNull();
    expect(element.textContent).toContain('No activities logged yet');
  });

  it('lists the newest activity first and highlights the running one', () => {
    render([
      entry(1, '2026-03-10T08:00:00Z', { finishedAt: '2026-03-10T09:00:00Z', durationSeconds: 3600 }),
      entry(2, '2026-03-10T09:00:00Z'),
    ]);

    const rows = element.querySelectorAll('tbody tr');
    expect(rows.length).toBe(2);
    expect(rows[0].classList).toContain('row-running');
    expect(rows[1].classList).not.toContain('row-running');
  });

  it('labels every cell with its column name (used by the card layout on phones)', () => {
    render([entry(1, '2026-03-10T08:00:00Z')]);

    const headers = Array.from(element.querySelectorAll('thead th')).map((th) => th.textContent?.trim());
    const labels = Array.from(element.querySelectorAll('tbody tr:first-child td')).map((td) =>
      td.getAttribute('data-label')
    );

    expect(labels).toEqual(headers as string[]);
  });
});
