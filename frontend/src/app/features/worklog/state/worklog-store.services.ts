import { Injectable } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { BehaviorSubject, finalize } from 'rxjs';
import { LookupApiService } from '../services/lookup-api.services';
import { WorklogApiService } from '../services/worklog-api.services';
import { EmployeeItem, LookupResponse } from '../models/lookup.model';
import { NotificationService } from '../../../core/ui/notification.services';
import {
  ActivityEntryResponse,
  StartActivityRequest,
  WorkDayResponse,
} from '../models/worklog.model';

interface WorklogState {
  employees: EmployeeItem[];
  selectedEmployeeId: number | null;
  lookups: LookupResponse | null;
  workDay: WorkDayResponse | null;
  entries: ActivityEntryResponse[];
  currentEntry: ActivityEntryResponse | null;
  noWorkdayForSelectedDate: boolean;
  loading: boolean;
  error: string | null;
}

@Injectable({
  providedIn: 'root',
})
export class WorklogStoreService {
  private readonly stateSubject = new BehaviorSubject<WorklogState>({
    employees: [],
    selectedEmployeeId: null,
    lookups: null,
    workDay: null,
    entries: [],
    currentEntry: null,
    noWorkdayForSelectedDate: false,
    loading: false,
    error: null,
  });

  readonly state$ = this.stateSubject.asObservable();

  constructor(
    private readonly lookupApiService: LookupApiService,
    private readonly worklogApiService: WorklogApiService,
    private readonly notificationService: NotificationService
  ) {}

  loadLookups(): void {
    this.patchState({ loading: true, error: null });

    this.lookupApiService
      .getAllLookups()
      .pipe(finalize(() => this.patchState({ loading: false })))
      .subscribe({
        next: (lookups) => this.patchState({ lookups }),
        error: (error: HttpErrorResponse) =>
          this.patchState({ error: this.resolveErrorMessage(error) }),
      });
  }

  loadEmployees(): void {
    this.patchState({ loading: true, error: null });

    this.lookupApiService
      .getEmployees()
      .pipe(finalize(() => this.patchState({ loading: false })))
      .subscribe({
        next: (employees) =>
          this.patchState({
            employees,
            selectedEmployeeId: null,
          }),
        error: (error: HttpErrorResponse) =>
          this.patchState({ error: this.resolveErrorMessage(error) }),
      });
  }

  selectEmployee(employeeId: number, date: string): void {
    this.patchState({
      selectedEmployeeId: employeeId,
      workDay: null,
      entries: [],
      currentEntry: null,
      noWorkdayForSelectedDate: false,
      error: null,
    });
    this.loadWorkDay(employeeId, date);
  }

  getSelectedEmployeeId(): number | null {
    return this.stateSubject.value.selectedEmployeeId;
  }

  loadWorkDay(employeeId: number, date: string): void {
    this.patchState({ loading: true, error: null });

    this.worklogApiService
      .getWorkDay(employeeId, date)
      .pipe(finalize(() => this.patchState({ loading: false })))
      .subscribe({
        next: (workDay) =>
          this.patchState({
            workDay,
            entries: workDay.entries,
            currentEntry: workDay.currentEntry,
            noWorkdayForSelectedDate: false,
          }),
        error: (error: HttpErrorResponse) => {
          if (error.status === 404) {
            this.patchState({
              workDay: null,
              entries: [],
              currentEntry: null,
              noWorkdayForSelectedDate: true,
              error: null,
            });
            return;
          }
          this.patchState({ error: this.resolveErrorMessage(error) });
        },
      });
  }

  startActivity(employeeId: number, payload: StartActivityRequest): void {
    this.patchState({ loading: true, error: null });

    this.worklogApiService
      .startActivity(employeeId, payload)
      .pipe(finalize(() => this.patchState({ loading: false })))
      .subscribe({
        next: (startedEntry) => {
          const nowIso = new Date().toISOString();
          const currentEntries = this.stateSubject.value.entries;
          const currentOpenEntry = this.stateSubject.value.currentEntry;

          const closedEntries = currentEntries.map((entry) => {
            if (currentOpenEntry && entry.id === currentOpenEntry.id && entry.finishedAt == null) {
              const elapsedSeconds = Math.max(
                0,
                Math.floor((new Date(nowIso).getTime() - new Date(entry.startedAt).getTime()) / 1000)
              );
              return {
                ...entry,
                finishedAt: nowIso,
                durationSeconds: elapsedSeconds,
              };
            }
            return entry;
          });

          this.patchState({
            entries: [...closedEntries, startedEntry],
            currentEntry: startedEntry,
            noWorkdayForSelectedDate: false,
            error: null,
          });
        },
        error: (error: HttpErrorResponse) =>
          this.patchState({ error: this.resolveErrorMessage(error) }),
      });
  }

  saveDay(employeeId: number): void {
    this.patchState({ loading: true, error: null });

    this.worklogApiService
      .saveDay(employeeId)
      .pipe(finalize(() => this.patchState({ loading: false })))
      .subscribe({
        next: (workDay) =>
          {
            this.patchState({
              workDay,
              entries: workDay.entries,
              currentEntry: workDay.currentEntry,
              noWorkdayForSelectedDate: false,
            });
            this.notificationService.success('Workday saved successfully.');
          },
        error: (error: HttpErrorResponse) =>
          this.patchState({ error: this.resolveErrorMessage(error) }),
      });
  }

  private patchState(partial: Partial<WorklogState>): void {
    this.stateSubject.next({
      ...this.stateSubject.value,
      ...partial,
    });
  }

  private resolveErrorMessage(error: HttpErrorResponse): string {
    const apiMessage = error.error?.message;
    if (typeof apiMessage === 'string' && apiMessage.length > 0) {
      return apiMessage;
    }
    return error.message || 'Unexpected error';
  }
}
