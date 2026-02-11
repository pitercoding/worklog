export interface LookupItem {
  id: number;
  name: string;
}

export interface SubactivityItem {
  id: number;
  name: string;
  activityId: number;
}

export interface LookupResponse {
  programs: LookupItem[];
  teams: LookupItem[];
  languages: LookupItem[];
  activities: LookupItem[];
  subactivities: SubactivityItem[];
}

export interface EmployeeItem {
  id: number;
  name: string;
  email: string;
}
