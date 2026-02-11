const BASE_API = 'http://localhost:8080/api';

export const API_ENDPOINTS = {
  WORKLOG: `${BASE_API}/worklog`,
  LOOKUPS: `${BASE_API}/lookups`,
  SUBACTIVITIES: `${BASE_API}/lookups/subactivities`,

  worklogStartByEmployee: (employeeId: number) => `${BASE_API}/worklog/${employeeId}/start`,

  worklogSaveByEmployee: (employeeId: number) => `${BASE_API}/worklog/${employeeId}/save`,

  worklogDayByEmployee: (employeeId: number) => `${BASE_API}/worklog/${employeeId}`,

  lookupSubactivitiesByActivity: (activityId: number) => `${BASE_API}/lookups/subactivities?activityId=${activityId}`,
};
