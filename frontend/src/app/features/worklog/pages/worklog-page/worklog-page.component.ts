import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
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
export class WorklogPageComponent implements OnInit, OnDestroy {
  readonly state$;
  private stateSubscription?: Subscription;
  private lastSelectedEmployeeId: number | null = null;

  constructor(private readonly worklogStore: WorklogStoreService) {
    this.state$ = this.worklogStore.state$;
  }

  ngOnInit(): void {
    this.worklogStore.loadLookups();
    this.worklogStore.loadEmployees();

    this.stateSubscription = this.worklogStore.state$.subscribe((state) => {
      if (state.selectedEmployeeId != null && state.selectedEmployeeId !== this.lastSelectedEmployeeId) {
        this.lastSelectedEmployeeId = state.selectedEmployeeId;
        this.worklogStore.loadWorkDay(state.selectedEmployeeId, todayApiDate());
      }
    });
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

  ngOnDestroy(): void {
    this.stateSubscription?.unsubscribe();
  }
}
