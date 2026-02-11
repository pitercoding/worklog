import { AsyncPipe, JsonPipe, NgIf } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { StartActivityRequest } from '../../models/worklog.model';
import { WorklogStoreService } from '../../state/worklog-store.services';

@Component({
  selector: 'app-worklog-page',
  imports: [NgIf, AsyncPipe, JsonPipe],
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
    const today = this.todayIso();
    this.worklogStore.loadLookups();
    this.worklogStore.loadWorkDay(this.employeeId, today);
  }

  onStartActivity(payload: StartActivityRequest): void {
    this.worklogStore.startActivity(this.employeeId, payload);
  }

  onSaveDay(): void {
    this.worklogStore.saveDay(this.employeeId);
  }

  private todayIso(): string {
    return new Date().toISOString().slice(0, 10);
  }
}
