import { AsyncPipe, NgClass, NgFor, NgIf } from '@angular/common';
import { Component } from '@angular/core';
import { NotificationService, ToastMessage } from './notification.services';

@Component({
  selector: 'app-toast-container',
  standalone: true,
  imports: [NgIf, NgFor, NgClass, AsyncPipe],
  template: `
    <section class="toast-container" *ngIf="notificationService.toasts$ | async as toasts">
      <article class="toast" *ngFor="let toast of toasts; trackBy: trackByToastId" [ngClass]="toast.type">
        <span>{{ toast.text }}</span>
        <button type="button" (click)="notificationService.remove(toast.id)">x</button>
      </article>
    </section>
  `,
  styles: `
    .toast-container {
      position: fixed;
      top: 18px;
      right: 18px;
      z-index: 1200;
      display: grid;
      gap: 10px;
      width: min(420px, calc(100vw - 24px));
    }

    .toast {
      display: flex;
      align-items: center;
      justify-content: space-between;
      gap: 12px;
      border-radius: 10px;
      padding: 12px 14px;
      border: 1px solid rgba(111, 168, 255, 0.35);
      background: rgba(8, 15, 27, 0.95);
      color: var(--text-main);
      box-shadow: 0 10px 30px rgba(0, 0, 0, 0.35);
    }

    .toast.error {
      border-color: rgba(255, 127, 150, 0.45);
    }

    .toast.success {
      border-color: rgba(87, 255, 162, 0.45);
    }

    .toast.info {
      border-color: rgba(86, 212, 255, 0.45);
    }

    button {
      border: 0;
      background: transparent;
      color: var(--text-muted);
      cursor: pointer;
      font-size: 13px;
      line-height: 1;
      padding: 0;
    }
  `,
})
export class ToastContainerComponent {
  constructor(public readonly notificationService: NotificationService) {}

  trackByToastId(index: number, toast: ToastMessage): number {
    return toast.id ?? index;
  }
}
