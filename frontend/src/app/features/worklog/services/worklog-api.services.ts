import { API_ENDPOINTS } from './../../../core/constants/api-endpoints';
import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ActivityEntryResponse, StartActivityRequest, WorkDayResponse } from '../models/worklog.model';

@Injectable({
  providedIn: 'root',
})
export class WorklogApiService {
  private http = inject(HttpClient);

  startActivity(employeeId: number, request: StartActivityRequest): Observable<ActivityEntryResponse> {
    return this.http.post<ActivityEntryResponse>(API_ENDPOINTS.worklogStartByEmployee(employeeId), request);
  }

  saveDay(employeeId: number): Observable<WorkDayResponse> {
    return this.http.post<WorkDayResponse>(API_ENDPOINTS.worklogSaveByEmployee(employeeId), null);
  }

  getWorkDay(employeeId: number, date: string): Observable<WorkDayResponse> {
    const params = new HttpParams().set('date', date);

    return this.http.get<WorkDayResponse>(API_ENDPOINTS.worklogDayByEmployee(employeeId), { params });
  };
}
