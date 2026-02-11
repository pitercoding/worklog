import { NgFor } from '@angular/common';
import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { LookupResponse, SubactivityItem } from '../../models/lookup.model';
import { StartActivityRequest } from '../../models/worklog.model';

@Component({
  selector: 'app-activity-form',
  imports: [NgFor, ReactiveFormsModule],
  templateUrl: './activity-form.component.html',
  styleUrl: './activity-form.component.scss',
})
export class ActivityFormComponent implements OnInit, OnChanges {
  @Input() lookups: LookupResponse | null = null;
  @Input() loading = false;
  @Output() startActivity = new EventEmitter<StartActivityRequest>();

  filteredSubactivities: SubactivityItem[] = [];

  readonly form;

  constructor(private readonly formBuilder: FormBuilder) {
    this.form = this.formBuilder.nonNullable.group({
      programId: [0, [Validators.required, Validators.min(1)]],
      teamId: [0, [Validators.required, Validators.min(1)]],
      languageId: [0, [Validators.required, Validators.min(1)]],
      activityId: [0, [Validators.required, Validators.min(1)]],
      subactivityId: [0, [Validators.required, Validators.min(1)]],
      note: [''],
    });
  }

  ngOnInit(): void {
    this.form.controls.activityId.valueChanges.subscribe((activityId) => {
      this.updateSubactivities(activityId);
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['lookups']?.currentValue && this.lookups) {
      this.updateSubactivities(this.form.controls.activityId.value);
    }
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const value = this.form.getRawValue();
    this.startActivity.emit({
      programId: value.programId,
      teamId: value.teamId,
      languageId: value.languageId,
      activityId: value.activityId,
      subactivityId: value.subactivityId,
      note: value.note?.trim() || undefined,
    });
  }

  private updateSubactivities(activityId: number): void {
    const subactivities = this.lookups?.subactivities ?? [];
    this.filteredSubactivities = subactivities.filter((item) => item.activityId === activityId);

    if (!this.filteredSubactivities.some((item) => item.id === this.form.controls.subactivityId.value)) {
      this.form.controls.subactivityId.setValue(0);
    }
  }
}
