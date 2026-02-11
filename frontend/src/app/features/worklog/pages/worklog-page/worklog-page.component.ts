import { AsyncPipe, NgIf } from '@angular/common';
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
  readonly employeeId = 1;
  readonly state$;

  constructor(private readonly worklogStore: WorklogStoreService) {
    this.state$ = this.worklogStore.state$;
  }

  ngOnInit(): void {
    const today = todayApiDate();
    this.worklogStore.loadLookups();
    this.worklogStore.loadWorkDay(this.employeeId, today);
  }

  onStartActivity(payload: StartActivityRequest): void {
    this.worklogStore.startActivity(this.employeeId, payload);
  }

  onSaveDay(): void {
    this.worklogStore.saveDay(this.employeeId);
  }
}
