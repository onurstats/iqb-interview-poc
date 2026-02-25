import { Routes } from '@angular/router';
import { unsavedChangesGuard } from './guards/unsaved-changes.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'dashboard', loadComponent: () => import('./pages/dashboard/dashboard').then((m) => m.DashboardComponent) },
  {
    path: 'students',
    loadComponent: () => import('./pages/students/student-list').then((m) => m.StudentListComponent),
  },
  {
    path: 'students/:id',
    loadComponent: () => import('./pages/students/student-detail').then((m) => m.StudentDetailComponent),
  },
  { path: 'courses', loadComponent: () => import('./pages/courses/course-list').then((m) => m.CourseListComponent) },
  {
    path: 'exam-results',
    loadComponent: () => import('./pages/exam-results/exam-result-list').then((m) => m.ExamResultListComponent),
  },
  {
    path: 'exam-results/add',
    loadComponent: () => import('./pages/exam-results/exam-result-add').then((m) => m.ExamResultAddComponent),
  },
  {
    path: 'exam-results/add/:studentId',
    loadComponent: () => import('./pages/exam-results/exam-result-detail').then((m) => m.ExamResultDetailComponent),
    canDeactivate: [unsavedChangesGuard],
  },
];
