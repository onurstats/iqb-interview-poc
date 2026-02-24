import { Routes } from '@angular/router';
import { StudentListComponent } from './pages/students/student-list';
import { StudentDetailComponent } from './pages/students/student-detail';
import { CourseListComponent } from './pages/courses/course-list';
import { ExamScoreEntryComponent } from './pages/exam-results/exam-score-entry';

export const routes: Routes = [
  { path: '', redirectTo: 'students', pathMatch: 'full' },
  { path: 'students', component: StudentListComponent },
  { path: 'students/:id', component: StudentDetailComponent },
  { path: 'courses', component: CourseListComponent },
  { path: 'exam-results', component: ExamScoreEntryComponent },
];
