import { DatePipe, NgFor, NgIf } from '@angular/common';
import { Component, Input } from '@angular/core';
import { ActivityEntryResponse } from '../../models/worklog.model';
import { DurationPipe } from '../../../../shared/pipes/duration.pipe';

@Component({
  selector: 'app-activity-table',
  imports: [NgIf, NgFor, DatePipe, DurationPipe],
  templateUrl: './activity-table.component.html',
  styleUrl: './activity-table.component.scss',
})
export class ActivityTableComponent {
  @Input() entries: ActivityEntryResponse[] = [];

  trackByEntryId(index: number, item: ActivityEntryResponse): number {
    return item.id ?? index;
  }
}
