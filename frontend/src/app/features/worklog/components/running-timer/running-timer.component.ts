import { NgIf } from '@angular/common';
import { Component, Input, OnChanges, OnDestroy, OnInit, SimpleChanges } from '@angular/core';
import { interval, Subscription } from 'rxjs';
import { DurationPipe } from '../../../../shared/pipes/duration.pipe';
import { ActivityEntryResponse } from '../../models/worklog.model';

@Component({
  selector: 'app-running-timer',
  imports: [NgIf, DurationPipe],
  templateUrl: './running-timer.component.html',
  styleUrl: './running-timer.component.scss',
})
export class RunningTimerComponent implements OnInit, OnChanges, OnDestroy {
  @Input() startedAt: string | null = null;
  @Input() currentEntry: ActivityEntryResponse | null = null;
  elapsedSeconds = 0;

  private timerSubscription?: Subscription;

  ngOnInit(): void {
    this.timerSubscription = interval(1000).subscribe(() => {
      this.elapsedSeconds = this.calculateElapsedSeconds();
    });
    this.elapsedSeconds = this.calculateElapsedSeconds();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['startedAt']) {
      this.elapsedSeconds = this.calculateElapsedSeconds();
    }
  }

  ngOnDestroy(): void {
    this.timerSubscription?.unsubscribe();
  }

  private calculateElapsedSeconds(): number {
    if (!this.startedAt) {
      return 0;
    }

    const started = new Date(this.startedAt).getTime();
    const now = Date.now();
    return Math.max(0, Math.floor((now - started) / 1000));
  }
}
