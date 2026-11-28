import { AsyncPipe, NgIf } from '@angular/common';
import { Component } from '@angular/core';
import { LoadingStateService } from '../http/loading-state.services';

/**
 * Shown when the API takes a long time to answer. On the free hosting the backend sleeps
 * after a period without traffic, so the first request can take up to 2 minutes.
 */
@Component({
  selector: 'app-slow-server-notice',
  standalone: true,
  imports: [NgIf, AsyncPipe],
  template: `
    <aside class="slow-notice" role="status" aria-live="polite" *ngIf="isSlow$ | async">
      <strong>Waking up the server...</strong>
      <span>
        This demo runs on free hosting that sleeps when idle, so the first request can take up to
        2 minutes. Thanks for waiting!
      </span>
    </aside>
  `,
  styles: `
    .slow-notice {
      position: fixed;
      left: 50%;
      bottom: 20px;
      transform: translateX(-50%);
      z-index: 1250;
      display: grid;
      gap: 4px;
      width: min(520px, calc(100vw - 24px));
      padding: 12px 16px;
      border-radius: var(--radius-md);
      border: 1px solid var(--surface-border);
      background: rgba(8, 15, 27, 0.96);
      color: var(--text-main);
      box-shadow: 0 10px 30px rgba(0, 0, 0, 0.35);
      font-size: 0.9rem;
      line-height: 1.4;
    }

    .slow-notice strong {
      color: var(--accent);
    }

    .slow-notice span {
      color: var(--text-muted);
    }
  `,
})
export class SlowServerNoticeComponent {
  readonly isSlow$;

  constructor(loadingStateService: LoadingStateService) {
    this.isSlow$ = loadingStateService.isSlow$;
  }
}
