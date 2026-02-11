import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

type ToastType = 'error' | 'success' | 'info';

export interface ToastMessage {
  id: number;
  type: ToastType;
  text: string;
}

@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  private readonly toastsSubject = new BehaviorSubject<ToastMessage[]>([]);
  private nextId = 1;

  readonly toasts$ = this.toastsSubject.asObservable();

  error(text: string): void {
    this.push('error', text);
  }

  success(text: string): void {
    this.push('success', text);
  }

  info(text: string): void {
    this.push('info', text);
  }

  remove(id: number): void {
    this.toastsSubject.next(this.toastsSubject.value.filter((toast) => toast.id !== id));
  }

  private push(type: ToastType, text: string): void {
    const message: ToastMessage = {
      id: this.nextId++,
      type,
      text,
    };

    this.toastsSubject.next([...this.toastsSubject.value, message]);

    setTimeout(() => {
      this.remove(message.id);
    }, 3500);
  }
}
