import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LookupResponse } from '../../models/lookup.model';
import { StartActivityRequest } from '../../models/worklog.model';
import { ActivityFormComponent } from './activity-form.component';

const lookups: LookupResponse = {
  programs: [{ id: 1, name: 'Program' }],
  teams: [{ id: 1, name: 'Team' }],
  languages: [{ id: 1, name: 'Java' }],
  activities: [
    { id: 1, name: 'Development' },
    { id: 2, name: 'Support' },
  ],
  subactivities: [
    { id: 11, name: 'Feature', activityId: 1 },
    { id: 12, name: 'Bug fix', activityId: 1 },
    { id: 21, name: 'Incident', activityId: 2 },
  ],
};

describe('ActivityFormComponent', () => {
  let fixture: ComponentFixture<ActivityFormComponent>;
  let component: ActivityFormComponent;
  let emitted: StartActivityRequest[];

  beforeEach(() => {
    fixture = TestBed.createComponent(ActivityFormComponent);
    component = fixture.componentInstance;
    emitted = [];
    component.startActivity.subscribe((payload) => emitted.push(payload));
    fixture.componentRef.setInput('lookups', lookups);
    fixture.detectChanges();
  });

  function fillValid(note = ''): void {
    component.form.setValue({
      programId: 1,
      teamId: 1,
      languageId: 1,
      activityId: 1,
      subactivityId: 11,
      note,
    });
  }

  it('does not emit while the form is invalid', () => {
    component.onSubmit();

    expect(emitted).toEqual([]);
    expect(component.form.controls.programId.touched).toBeTrue();
  });

  it('only lists the subactivities of the selected activity', () => {
    component.form.controls.activityId.setValue(1);
    expect(component.filteredSubactivities.map((s) => s.id)).toEqual([11, 12]);

    component.form.controls.activityId.setValue(2);
    expect(component.filteredSubactivities.map((s) => s.id)).toEqual([21]);
  });

  it('clears the subactivity when it does not belong to the new activity', () => {
    component.form.controls.activityId.setValue(1);
    component.form.controls.subactivityId.setValue(11);

    component.form.controls.activityId.setValue(2);

    expect(component.form.controls.subactivityId.value).toBe(0);
  });

  it('emits the payload with a trimmed note and resets the form', () => {
    fillValid('  fixing a bug  ');

    component.onSubmit();

    expect(emitted).toEqual([
      { programId: 1, teamId: 1, languageId: 1, activityId: 1, subactivityId: 11, note: 'fixing a bug' },
    ]);
    expect(component.form.getRawValue().activityId).toBe(0);
    expect(component.filteredSubactivities).toEqual([]);
  });

  it('omits the note when it is blank', () => {
    fillValid('   ');

    component.onSubmit();

    expect(emitted[0].note).toBeUndefined();
  });

  it('locks the form when the day is finished and unlocks it again', () => {
    fixture.componentRef.setInput('dayFinished', true);
    fixture.detectChanges();
    expect(component.form.disabled).toBeTrue();

    fixture.componentRef.setInput('dayFinished', false);
    fixture.detectChanges();
    expect(component.form.enabled).toBeTrue();
  });
});
