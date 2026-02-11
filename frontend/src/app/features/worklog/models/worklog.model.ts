import { LookupItem, SubactivityItem } from './lookup.model';

export interface StartActivityRequest {
  programId: number;
  teamId: number;
  languageId: number;
  activityId: number;
  subactivityId: number;
  note?: string;
}

export interface WorkDayResponse {
  id: number;
  date: string;
  startedAt: string;
  finishedAt: string | null;
  status: WorkDayStatus;
  currentEntry: ActivityEntryResponse | null;
  entries: ActivityEntryResponse[];
}

export interface ActivityEntryResponse {
  id: number;
  program: LookupItem;
  team: LookupItem;
  language: LookupItem;
  activity: LookupItem;
  subactivity: SubactivityItem;
  startedAt: string;
  finishedAt: string | null;
  durationSeconds: number | null;
  note: string | null;
}

export type WorkDayStatus = 'OPEN' | 'FINISHED';
