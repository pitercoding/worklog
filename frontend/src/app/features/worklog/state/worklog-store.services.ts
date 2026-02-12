import { Injectable } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { BehaviorSubject, finalize } from 'rxjs';
import { LookupApiService } from '../services/lookup-api.services';
import { WorklogApiService } from '../services/worklog-api.services';
import { EmployeeItem, LookupResponse } from '../models/lookup.model';
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
    loading: false,
    error: null,
  });

  readonly state$ = this.stateSubject.asObservable();

  constructor(
    private readonly lookupApiService: LookupApiService,
    private readonly worklogApiService: WorklogApiService
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
    this.patchState({ selectedEmployeeId: employeeId, error: null });
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
          }),
        error: (error: HttpErrorResponse) => {
          if (error.status === 404) {
            this.patchState({
              workDay: null,
              entries: [],
              currentEntry: null,
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
        next: () => this.loadWorkDay(employeeId, this.todayIso()),
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
          this.patchState({
            workDay,
            entries: workDay.entries,
            currentEntry: workDay.currentEntry,
          }),
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

  private todayIso(): string {
    return new Date().toISOString().slice(0, 10);
  }
}
