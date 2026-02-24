import { Routes } from '@angular/router';
import { DashboardComponent } from './pages/dashboard/dashboard';
import { StudentListComponent } from './pages/students/student-list';
import { StudentDetailComponent } from './pages/students/student-detail';
import { CourseListComponent } from './pages/courses/course-list';
import { ExamResultListComponent } from './pages/exam-results/exam-result-list';
import { ExamResultAddComponent } from './pages/exam-results/exam-result-add';
import { ExamResultDetailComponent } from './pages/exam-results/exam-result-detail';
import { unsavedChangesGuard } from './guards/unsaved-changes.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'students', component: StudentListComponent },
  { path: 'students/:id', component: StudentDetailComponent },
  { path: 'courses', component: CourseListComponent },
  { path: 'exam-results', component: ExamResultListComponent },
  { path: 'exam-results/add', component: ExamResultAddComponent },
  { path: 'exam-results/add/:studentId', component: ExamResultDetailComponent, canDeactivate: [unsavedChangesGuard] },
];
