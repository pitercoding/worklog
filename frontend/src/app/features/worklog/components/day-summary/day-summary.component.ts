import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { interval, Subscription } from 'rxjs';
import { ActivityEntryResponse } from '../../models/worklog.model';
import { DurationPipe } from '../../../../shared/pipes/duration.pipe';

@Component({
  selector: 'app-day-summary',
  imports: [DurationPipe],
  templateUrl: './day-summary.component.html',
  styleUrl: './day-summary.component.scss',
})
export class DaySummaryComponent implements OnInit, OnDestroy {
  @Input() entries: ActivityEntryResponse[] = [];
  nowEpoch = Date.now();

  private timerSubscription?: Subscription;

  get totalActivities(): number {
    return this.entries.length;
  }

  get totalSeconds(): number {
    return this.entries.reduce((sum, entry) => {
      if (entry.durationSeconds != null) {
        return sum + entry.durationSeconds;
      }
      if (entry.startedAt && !entry.finishedAt) {
        const startedEpoch = new Date(entry.startedAt).getTime();
        return sum + Math.max(0, Math.floor((this.nowEpoch - startedEpoch) / 1000));
      }
      return sum;
    }, 0);
  }

  ngOnInit(): void {
    this.timerSubscription = interval(1000).subscribe(() => {
      this.nowEpoch = Date.now();
    });
  }

  ngOnDestroy(): void {
    this.timerSubscription?.unsubscribe();
  }

}
