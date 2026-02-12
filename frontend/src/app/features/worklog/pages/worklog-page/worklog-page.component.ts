import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { StartActivityRequest } from '../../models/worklog.model';
import { WorklogStoreService } from '../../state/worklog-store.services';
import { ActivityFormComponent } from '../../components/activity-form/activity-form.component';
import { ActivityTableComponent } from '../../components/activity-table/activity-table.component';
import { DaySummaryComponent } from '../../components/day-summary/day-summary.component';
import { RunningTimerComponent } from '../../components/running-timer/running-timer.component';
import { todayApiDate } from '../../../../shared/utils/date.utils';

@Component({
  selector: 'app-worklog-page',
  imports: [
    NgIf,
    NgFor,
    AsyncPipe,
    ActivityFormComponent,
    ActivityTableComponent,
    RunningTimerComponent,
    DaySummaryComponent,
  ],
  templateUrl: './worklog-page.component.html',
  styleUrl: './worklog-page.component.scss',
})
export class WorklogPageComponent implements OnInit {
  readonly state$;

  constructor(private readonly worklogStore: WorklogStoreService) {
    this.state$ = this.worklogStore.state$;
  }

  ngOnInit(): void {
    this.worklogStore.loadLookups();
    this.worklogStore.loadEmployees();
  }

  onStartActivity(payload: StartActivityRequest): void {
    const selectedEmployeeId = this.worklogStore.getSelectedEmployeeId();
    if (selectedEmployeeId == null) {
      return;
    }
    this.worklogStore.startActivity(selectedEmployeeId, payload);
  }

  onSaveDay(): void {
    const selectedEmployeeId = this.worklogStore.getSelectedEmployeeId();
    if (selectedEmployeeId == null) {
      return;
    }
    this.worklogStore.saveDay(selectedEmployeeId);
  }

  onEmployeeChange(employeeId: string): void {
    const parsedEmployeeId = Number(employeeId);
    if (!Number.isFinite(parsedEmployeeId) || parsedEmployeeId <= 0) {
      return;
    }
    this.worklogStore.selectEmployee(parsedEmployeeId, todayApiDate());
  }

  selectedEmployeeName(state: {
    employees: { id: number; name: string }[];
    selectedEmployeeId: number | null;
  }): string {
    if (state.selectedEmployeeId == null) {
      return '';
    }
    return state.employees.find((employee) => employee.id === state.selectedEmployeeId)?.name ?? '';
  }

  showNoWorkdayMessage(state: {
    selectedEmployeeId: number | null;
    workDay: unknown;
    entries: unknown[];
    loading: boolean;
    error: string | null;
  }): boolean {
    return (
      state.selectedEmployeeId != null &&
      !state.loading &&
      !state.error &&
      !state.workDay &&
      state.entries.length === 0
    );
  }
}
