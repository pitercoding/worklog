import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { NotificationService } from '../ui/notification.services';
import { errorInterceptor } from './error-interceptor';

describe('errorInterceptor', () => {
  let http: HttpClient;
  let controller: HttpTestingController;
  let notifications: jasmine.SpyObj<NotificationService>;

  beforeEach(() => {
    notifications = jasmine.createSpyObj<NotificationService>('NotificationService', ['error']);
    spyOn(console, 'error');

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([errorInterceptor])),
        provideHttpClientTesting(),
        { provide: NotificationService, useValue: notifications },
      ],
    });
    http = TestBed.inject(HttpClient);
    controller = TestBed.inject(HttpTestingController);
  });

  afterEach(() => controller.verify());

  it('shows the backend message in a toast', () => {
    http.get('/api/lookups').subscribe({ error: () => undefined });
    controller
      .expectOne('/api/lookups')
      .flush({ message: 'Workday is already finished' }, { status: 409, statusText: 'Conflict' });

    expect(notifications.error).toHaveBeenCalledOnceWith('Workday is already finished');
  });

  it('falls back to the HTTP error message when the body has no message', () => {
    http.get('/api/lookups').subscribe({ error: () => undefined });
    controller.expectOne('/api/lookups').flush('boom', { status: 500, statusText: 'Server Error' });

    expect(notifications.error).toHaveBeenCalledTimes(1);
  });

  it('does not show a toast for the expected "no workday" 404', () => {
    let status = 0;
    http.get('/api/worklog/1').subscribe({ error: (e) => (status = e.status) });
    controller.expectOne('/api/worklog/1').flush(null, { status: 404, statusText: 'Not Found' });

    expect(status).toBe(404);
    expect(notifications.error).not.toHaveBeenCalled();
  });
});
