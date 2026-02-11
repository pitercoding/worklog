import { Routes } from '@angular/router';
import { WorklogPageComponent } from './features/worklog/pages/worklog-page/worklog-page.component';

export const routes: Routes = [
  { path: '', redirectTo: '/worklog', pathMatch: 'full'},
  { path: 'worklog', component: WorklogPageComponent }
];
