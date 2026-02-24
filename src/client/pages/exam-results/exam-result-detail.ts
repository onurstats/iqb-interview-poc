import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatChipsModule } from '@angular/material/chips';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { ExamResultService } from '../../services/exam-result.service';
import { CourseService } from '../../services/course.service';
import { StudentService } from '../../services/student.service';
import { Student } from '../../models/student.model';
import { Course } from '../../models/course.model';
import { CourseScores, SaveScoresRequest } from '../../models/exam-result.model';

@Component({
  selector: 'app-exam-result-detail',
  imports: [
    FormsModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatSnackBarModule,
    MatProgressBarModule,
    MatChipsModule,
    MatAutocompleteModule,
  ],
  template: `
    @if (student) {
      <div class="selected-header">
        <button mat-icon-button (click)="goBack()">
          <mat-icon>arrow_back</mat-icon>
        </button>
        <h2>Scores for {{ student.fullName }}</h2>
        <span class="spacer"></span>
        <button mat-flat-button (click)="saveScores()" [disabled]="saving"><mat-icon>save</mat-icon> Save All</button>
      </div>

      @if (loading) {
        <mat-progress-bar mode="indeterminate"></mat-progress-bar>
      }

      @if (courseScores.length > 0) {
        <table mat-table [dataSource]="courseScores" class="mat-elevation-z1 scores-table">
          <ng-container matColumnDef="course">
            <th mat-header-cell *matHeaderCellDef>Course</th>
            <td mat-cell *matCellDef="let row">
              <span class="course-name">{{ row.courseName }}</span>
              @if (isCompleted(row)) {
                <mat-chip class="completed-chip" highlighted>Completed</mat-chip>
              }
            </td>
          </ng-container>

          <ng-container matColumnDef="score1">
            <th mat-header-cell *matHeaderCellDef>Score 1</th>
            <td mat-cell *matCellDef="let row; let i = index">
              <mat-form-field class="score-input">
                <input
                  matInput
                  type="number"
                  min="0"
                  max="100"
                  [ngModel]="row.scores[0].score"
                  (ngModelChange)="onScoreChange(i, 0, $event)"
                  placeholder="-"
                  [disabled]="saving"
                />
              </mat-form-field>
            </td>
          </ng-container>

          <ng-container matColumnDef="score2">
            <th mat-header-cell *matHeaderCellDef>Score 2</th>
            <td mat-cell *matCellDef="let row; let i = index">
              <mat-form-field class="score-input">
                <input
                  matInput
                  type="number"
                  min="0"
                  max="100"
                  [ngModel]="row.scores[1].score"
                  (ngModelChange)="onScoreChange(i, 1, $event)"
                  placeholder="-"
                  [disabled]="saving"
                />
              </mat-form-field>
            </td>
          </ng-container>

          <ng-container matColumnDef="score3">
            <th mat-header-cell *matHeaderCellDef>Score 3</th>
            <td mat-cell *matCellDef="let row; let i = index">
              <mat-form-field class="score-input">
                <input
                  matInput
                  type="number"
                  min="0"
                  max="100"
                  [ngModel]="row.scores[2].score"
                  (ngModelChange)="onScoreChange(i, 2, $event)"
                  placeholder="-"
                  [disabled]="saving"
                />
              </mat-form-field>
            </td>
          </ng-container>

          <ng-container matColumnDef="average">
            <th mat-header-cell *matHeaderCellDef>Average</th>
            <td mat-cell *matCellDef="let row">
              <span class="average-score" [class.complete]="isCompleted(row)">
                {{ getAverage(row) }}
              </span>
            </td>
          </ng-container>

          <ng-container matColumnDef="remove">
            <th mat-header-cell *matHeaderCellDef></th>
            <td mat-cell *matCellDef="let row; let i = index">
              <button
                mat-icon-button
                color="warn"
                (click)="removeCourse(i)"
                [disabled]="saving"
                aria-label="Remove course"
              >
                <mat-icon>close</mat-icon>
              </button>
            </td>
          </ng-container>

          <tr mat-header-row *matHeaderRowDef="scoreColumns"></tr>
          <tr mat-row *matRowDef="let row; columns: scoreColumns"></tr>
        </table>
      } @else if (!loading) {
        <p class="no-courses">No courses added yet. Use the field below to add a course.</p>
      }

      @if (availableCourses.length > 0) {
        <div class="add-course-row">
          <mat-form-field class="add-course-field">
            <mat-label>Add course</mat-label>
            <input
              matInput
              [ngModel]="courseSearchText"
              (ngModelChange)="onCourseSearch($event)"
              [matAutocomplete]="auto"
              placeholder="Type to search courses..."
            />
            <mat-icon matSuffix>add</mat-icon>
            <mat-autocomplete
              #auto="matAutocomplete"
              (optionSelected)="addCourse($event.option.value)"
              [displayWith]="displayCourse"
            >
              @for (course of filteredCourses; track course.id) {
                <mat-option [value]="course">{{ course.name }}</mat-option>
              }
            </mat-autocomplete>
          </mat-form-field>
        </div>
      }
    } @else if (!loading) {
      <p>Student not found.</p>
    }
  `,
  styles: `
    .selected-header {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      margin-bottom: 1rem;
    }
    .selected-header h2 {
      margin: 0;
    }
    .spacer {
      flex: 1;
    }
    .scores-table {
      width: 100%;
      margin-top: 0.5rem;
    }
    .course-name {
      font-weight: 500;
    }
    .completed-chip {
      font-size: 0.7rem;
      margin-left: 0.5rem;
    }
    .score-input {
      width: 5rem;
    }
    .average-score {
      font-weight: 600;
      font-size: 1rem;
      color: var(--mat-sys-on-surface-variant);
    }
    .average-score.complete {
      color: var(--mat-sys-primary);
    }
    .no-courses {
      color: var(--mat-sys-on-surface-variant);
      text-align: center;
      padding: 2rem 0;
    }
    .add-course-row {
      margin-top: 1rem;
    }
    .add-course-field {
      width: 20rem;
    }
  `,
})
export class ExamResultDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private examResultService = inject(ExamResultService);
  private courseService = inject(CourseService);
  private studentService = inject(StudentService);
  private snackBar = inject(MatSnackBar);
  private cdr = inject(ChangeDetectorRef);

  student: Student | null = null;
  studentId!: number;

  courseScores: CourseScores[] = [];
  scoreColumns = ['course', 'score1', 'score2', 'score3', 'average', 'remove'];
  loading = true;
  saving = false;
  private savedSnapshot = '';

  get hasUnsavedChanges(): boolean {
    return JSON.stringify(this.courseScores) !== this.savedSnapshot;
  }

  private takeSnapshot() {
    this.savedSnapshot = JSON.stringify(this.courseScores);
  }

  allCourses: Course[] = [];
  availableCourses: Course[] = [];
  filteredCourses: Course[] = [];
  courseSearchText = '';

  ngOnInit() {
    this.studentId = Number(this.route.snapshot.paramMap.get('studentId'));
    this.studentService.getById(this.studentId).subscribe({
      next: (student) => {
        this.student = student;
        this.cdr.markForCheck();
        this.loadScores();
        this.loadAllCourses();
      },
      error: () => {
        this.loading = false;
        this.cdr.markForCheck();
      },
    });
  }

  goBack() {
    this.router.navigate(['/exam-results/add']);
  }

  loadAllCourses() {
    this.courseService.getAll(0, 100).subscribe({
      next: (page) => {
        this.allCourses = page.content;
        this.updateAvailableCourses();
        this.cdr.markForCheck();
      },
    });
  }

  loadScores() {
    this.examResultService.getStudentScores(this.studentId).subscribe({
      next: (data) => {
        this.courseScores = data.courses.map((c) => ({
          ...c,
          scores: c.scores.map((s) => ({ ...s })),
        }));
        this.loading = false;
        this.updateAvailableCourses();
        this.takeSnapshot();
        this.cdr.markForCheck();
      },
      error: () => {
        this.snackBar.open('Failed to load scores', 'Close', { duration: 3000 });
        this.loading = false;
        this.cdr.markForCheck();
      },
    });
  }

  updateAvailableCourses() {
    const usedIds = new Set(this.courseScores.map((c) => c.courseId));
    this.availableCourses = this.allCourses.filter((c) => !usedIds.has(c.id!));
    this.filteredCourses = [...this.availableCourses];
  }

  onCourseSearch(text: string) {
    this.courseSearchText = text;
    const term = text.toLowerCase();
    this.filteredCourses = this.availableCourses.filter((c) => c.name.toLowerCase().includes(term));
  }

  displayCourse(course: Course): string {
    return course?.name ?? '';
  }

  addCourse(course: Course) {
    this.courseScores = [
      ...this.courseScores,
      {
        courseId: course.id!,
        courseName: course.name,
        scores: [
          { id: null, score: null },
          { id: null, score: null },
          { id: null, score: null },
        ],
      },
    ];
    this.courseSearchText = '';
    this.updateAvailableCourses();
    this.cdr.markForCheck();
  }

  removeCourse(index: number) {
    const row = this.courseScores[index];
    const hasScores = row.scores.some((s) => s.id !== null);
    if (hasScores && !confirm(`Remove ${row.courseName}? Existing scores will be deleted on save.`)) {
      return;
    }
    if (hasScores) {
      row.scores = row.scores.map((s) => ({ id: s.id, score: null }));
      this.saving = true;
      this.cdr.markForCheck();
      const request: SaveScoresRequest = {
        courses: [{ courseId: row.courseId, scores: row.scores }],
      };
      this.examResultService.saveStudentScores(this.studentId, request).subscribe({
        next: () => {
          this.courseScores.splice(index, 1);
          this.courseScores = [...this.courseScores];
          this.saving = false;
          this.updateAvailableCourses();
          this.takeSnapshot();
          this.snackBar.open('Course removed', 'Close', { duration: 3000 });
          this.cdr.markForCheck();
        },
        error: () => {
          this.saving = false;
          this.snackBar.open('Failed to remove course', 'Close', { duration: 3000 });
          this.cdr.markForCheck();
        },
      });
    } else {
      this.courseScores.splice(index, 1);
      this.courseScores = [...this.courseScores];
      this.updateAvailableCourses();
      this.cdr.markForCheck();
    }
  }

  onScoreChange(courseIndex: number, scoreIndex: number, value: string | number | null) {
    const numValue = value === '' || value === null ? null : Number(value);
    this.courseScores[courseIndex].scores[scoreIndex] = {
      ...this.courseScores[courseIndex].scores[scoreIndex],
      score: numValue,
    };
  }

  isCompleted(row: CourseScores): boolean {
    return row.scores.filter((s) => s.score !== null).length === 3;
  }

  getAverage(row: CourseScores): string {
    const filled = row.scores.filter((s) => s.score !== null);
    if (filled.length === 0) return '-';
    const avg = filled.reduce((sum, s) => sum + s.score!, 0) / filled.length;
    return avg.toFixed(1);
  }

  saveScores() {
    this.saving = true;
    this.cdr.markForCheck();

    const request: SaveScoresRequest = {
      courses: this.courseScores.map((c) => ({
        courseId: c.courseId,
        scores: c.scores.map((s) => ({ id: s.id, score: s.score })),
      })),
    };

    this.examResultService.saveStudentScores(this.studentId, request).subscribe({
      next: (data) => {
        this.courseScores = data.courses.map((c) => ({
          ...c,
          scores: c.scores.map((s) => ({ ...s })),
        }));
        this.saving = false;
        this.updateAvailableCourses();
        this.takeSnapshot();
        this.snackBar.open('Scores saved successfully', 'Close', { duration: 3000 });
        this.cdr.markForCheck();
      },
      error: () => {
        this.saving = false;
        this.snackBar.open('Failed to save scores', 'Close', { duration: 3000 });
        this.cdr.markForCheck();
      },
    });
  }
}
