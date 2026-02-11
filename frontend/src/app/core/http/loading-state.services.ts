import { Injectable } from '@angular/core';
import { BehaviorSubject, map } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class LoadingStateService {
  private readonly pendingRequestsSubject = new BehaviorSubject<number>(0);

  readonly isLoading$ = this.pendingRequestsSubject.pipe(map((count) => count > 0));

  show(): void {
    this.pendingRequestsSubject.next(this.pendingRequestsSubject.value + 1);
  }

  hide(): void {
    const nextValue = this.pendingRequestsSubject.value - 1;
    this.pendingRequestsSubject.next(nextValue < 0 ? 0 : nextValue);
  }
}
