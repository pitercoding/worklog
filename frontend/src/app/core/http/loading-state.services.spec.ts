import { fakeAsync, tick, discardPeriodicTasks } from '@angular/core/testing';
import { LoadingStateService, SLOW_REQUEST_THRESHOLD_MS } from './loading-state.services';

describe('LoadingStateService', () => {
  let service: LoadingStateService;
  let loading: boolean[];
  let slow: boolean[];

  beforeEach(() => {
    service = new LoadingStateService();
    loading = [];
    slow = [];
    service.isLoading$.subscribe((value) => loading.push(value));
    service.isSlow$.subscribe((value) => slow.push(value));
  });

  it('is loading while at least one request is pending', () => {
    service.show();
    service.show();
    service.hide();
    expect(loading[loading.length - 1]).toBeTrue();

    service.hide();
    expect(loading[loading.length - 1]).toBeFalse();
  });

  it('never goes below zero pending requests', () => {
    service.hide();
    service.hide();
    service.show();

    expect(loading[loading.length - 1]).toBeTrue();
  });

  it('is not slow for requests that finish quickly', fakeAsync(() => {
    service.show();
    tick(SLOW_REQUEST_THRESHOLD_MS - 1);
    service.hide();
    tick(SLOW_REQUEST_THRESHOLD_MS * 2);

    expect(slow).not.toContain(true);
    expect(slow[slow.length - 1]).toBeFalse();
  }));

  it('becomes slow when a request stays pending past the threshold', fakeAsync(() => {
    service.show();
    tick(SLOW_REQUEST_THRESHOLD_MS - 1);
    expect(slow[slow.length - 1]).toBeFalse();

    tick(1);
    expect(slow[slow.length - 1]).toBeTrue();
  }));

  it('stops being slow as soon as all requests finish', fakeAsync(() => {
    service.show();
    tick(SLOW_REQUEST_THRESHOLD_MS);
    expect(slow[slow.length - 1]).toBeTrue();

    service.hide();
    expect(slow[slow.length - 1]).toBeFalse();
  }));

  it('does not restart the timer when another request starts while loading', fakeAsync(() => {
    service.show();
    tick(SLOW_REQUEST_THRESHOLD_MS - 1000);
    service.show(); // a second request must not postpone the notice
    tick(1000);

    expect(slow[slow.length - 1]).toBeTrue();
    discardPeriodicTasks();
  }));
});
