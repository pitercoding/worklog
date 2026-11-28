import { Injectable } from '@angular/core';
import { BehaviorSubject, distinctUntilChanged, map, of, startWith, switchMap, timer } from 'rxjs';

/**
 * How long requests can stay pending before we tell the user the server is slow.
 * The free hosting puts the backend to sleep; waking it up takes about 1-2 minutes.
 */
export const SLOW_REQUEST_THRESHOLD_MS = 5000;

@Injectable({
  providedIn: 'root',
})
export class LoadingStateService {
  private readonly pendingRequestsSubject = new BehaviorSubject<number>(0);

  readonly isLoading$ = this.pendingRequestsSubject.pipe(map((count) => count > 0));

  /** True when there has been at least one pending request for longer than the threshold. */
  readonly isSlow$ = this.isLoading$.pipe(
    distinctUntilChanged(),
    switchMap((loading) =>
      loading ? timer(SLOW_REQUEST_THRESHOLD_MS).pipe(map(() => true), startWith(false)) : of(false)
    ),
    distinctUntilChanged()
  );

  show(): void {
    this.pendingRequestsSubject.next(this.pendingRequestsSubject.value + 1);
  }

  hide(): void {
    const nextValue = this.pendingRequestsSubject.value - 1;
    this.pendingRequestsSubject.next(nextValue < 0 ? 0 : nextValue);
  }
}
