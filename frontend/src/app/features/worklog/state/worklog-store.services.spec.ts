import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { firstValueFrom } from 'rxjs';
import { API_ENDPOINTS } from '../../../core/constants/api-endpoints';
import { NotificationService } from '../../../core/ui/notification.services';
import { ActivityEntryResponse, StartActivityRequest, WorkDayResponse } from '../models/worklog.model';
import { WorklogStoreService } from './worklog-store.services';

const item = { id: 1, name: 'Item' };

function entry(id: number, overrides: Partial<ActivityEntryResponse> = {}): ActivityEntryResponse {
  return {
    id,
    program: item,
    team: item,
    language: item,
    activity: item,
    subactivity: { ...item, activityId: 1 },
    startedAt: new Date(Date.now() - 60_000).toISOString(),
    finishedAt: null,
    durationSeconds: null,
    note: null,
    ...overrides,
  };
}

function workDay(entries: ActivityEntryResponse[], overrides: Partial<WorkDayResponse> = {}): WorkDayResponse {
  return {
    id: 10,
    date: '2026-03-10',
    startedAt: '2026-03-10T08:00:00Z',
    finishedAt: null,
    status: 'OPEN',
    currentEntry: entries.find((e) => e.finishedAt == null) ?? null,
    entries,
    ...overrides,
  };
}

const request: StartActivityRequest = {
  programId: 1,
  teamId: 1,
  languageId: 1,
  activityId: 1,
  subactivityId: 1,
};

describe('WorklogStoreService', () => {
  let store: WorklogStoreService;
  let http: HttpTestingController;
  let notifications: jasmine.SpyObj<NotificationService>;

  const state = () => firstValueFrom(store.state$);

  beforeEach(() => {
    notifications = jasmine.createSpyObj<NotificationService>('NotificationService', ['success', 'error', 'info']);

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: NotificationService, useValue: notifications },
      ],
    });
    store = TestBed.inject(WorklogStoreService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('loads the selected employee workday', async () => {
    const running = entry(1);
    store.selectEmployee(5);
    http.expectOne(API_ENDPOINTS.worklogDayByEmployee(5)).flush(workDay([running]));

    const s = await state();
    expect(s.selectedEmployeeId).toBe(5);
    expect(s.entries).toEqual([running]);
    expect(s.currentEntry).toEqual(running);
    expect(s.noWorkdayForSelectedDate).toBeFalse();
    expect(s.loading).toBeFalse();
  });

  it('marks "no workday" on 404 without setting an error', async () => {
    store.selectEmployee(5);
    http.expectOne(API_ENDPOINTS.worklogDayByEmployee(5)).flush(null, { status: 404, statusText: 'Not Found' });

    const s = await state();
    expect(s.noWorkdayForSelectedDate).toBeTrue();
    expect(s.error).toBeNull();
    expect(s.entries).toEqual([]);
    expect(s.loading).toBeFalse();
  });

  it('stores the backend message for other errors', async () => {
    store.selectEmployee(5);
    http
      .expectOne(API_ENDPOINTS.worklogDayByEmployee(5))
      .flush({ message: 'Something broke' }, { status: 500, statusText: 'Server Error' });

    const s = await state();
    expect(s.error).toBe('Something broke');
    expect(s.noWorkdayForSelectedDate).toBeFalse();
  });

  it('closes the running entry locally and appends the new one when starting an activity', async () => {
    const previous = entry(1);
    store.selectEmployee(5);
    http.expectOne(API_ENDPOINTS.worklogDayByEmployee(5)).flush(workDay([previous]));

    const started = entry(2);
    store.startActivity(5, request);
    const req = http.expectOne(API_ENDPOINTS.worklogStartByEmployee(5));
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(request);
    req.flush(started);

    const s = await state();
    expect(s.entries.length).toBe(2);
    expect(s.entries[0].finishedAt).not.toBeNull();
    expect(s.entries[0].durationSeconds).toBeGreaterThanOrEqual(59);
    expect(s.entries[1]).toEqual(started);
    expect(s.currentEntry).toEqual(started);
  });

  it('keeps the entries and stores the message when starting an activity fails', async () => {
    const previous = entry(1);
    store.selectEmployee(5);
    http.expectOne(API_ENDPOINTS.worklogDayByEmployee(5)).flush(workDay([previous]));

    store.startActivity(5, request);
    http
      .expectOne(API_ENDPOINTS.worklogStartByEmployee(5))
      .flush({ message: 'Workday is already finished' }, { status: 409, statusText: 'Conflict' });

    const s = await state();
    expect(s.entries).toEqual([previous]);
    expect(s.error).toBe('Workday is already finished');
    expect(s.loading).toBeFalse();
  });

  it('replaces the workday and shows a success toast when saving the day', async () => {
    const finished = entry(1, { finishedAt: new Date().toISOString(), durationSeconds: 60 });
    store.saveDay(5);
    http
      .expectOne(API_ENDPOINTS.worklogSaveByEmployee(5))
      .flush(workDay([finished], { status: 'FINISHED', finishedAt: new Date().toISOString() }));

    const s = await state();
    expect(s.workDay?.status).toBe('FINISHED');
    expect(s.currentEntry).toBeNull();
    expect(notifications.success).toHaveBeenCalledOnceWith('Workday saved successfully.');
  });
});
