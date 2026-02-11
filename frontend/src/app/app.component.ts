import { AsyncPipe, NgIf } from '@angular/common';
import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { LoadingStateService } from './core/http/loading-state.services';
import { ToastContainerComponent } from './core/ui/toast-container.component';

@Component({
  selector: 'app-root',
  imports: [NgIf, AsyncPipe, RouterOutlet, ToastContainerComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent {
  readonly isLoading$;

  constructor(private readonly loadingStateService: LoadingStateService) {
    this.isLoading$ = this.loadingStateService.isLoading$;
  }
}
