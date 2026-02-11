import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { EmployeeItem, LookupResponse, SubactivityItem } from '../models/lookup.model';
import { API_ENDPOINTS } from '../../../core/constants/api-endpoints';

@Injectable({
  providedIn: 'root',
})
export class LookupApiService {
  private http = inject(HttpClient);

  getAllLookups(): Observable<LookupResponse> {
    return this.http.get<LookupResponse>(API_ENDPOINTS.LOOKUPS);
  }

  getSubactivities(activityId: number): Observable<SubactivityItem[]> {
    return this.http.get<SubactivityItem[]>(API_ENDPOINTS.lookupSubactivitiesByActivity(activityId));
  }

  getEmployees(): Observable<EmployeeItem[]> {
    return this.http.get<EmployeeItem[]>(API_ENDPOINTS.EMPLOYEES);
  }
}
