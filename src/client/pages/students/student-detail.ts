import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatTableModule } from '@angular/material/table';
import { MatChipsModule } from '@angular/material/chips';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { StudentService } from '../../services/student.service';
import { ExamResultService } from '../../services/exam-result.service';
import { Student } from '../../models/student.model';
import { CourseScores } from '../../models/exam-result.model';

@Component({
  selector: 'app-student-detail',
  imports: [
    RouterLink,
    FormsModule,
    DecimalPipe,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatTableModule,
    MatChipsModule,
    MatSnackBarModule,
  ],
  template: `
    @if (student) {
      <div class="page-header">
        <button mat-button routerLink="/students"><mat-icon>arrow_back</mat-icon> Back to list</button>
      </div>

      <mat-card>
        <mat-card-header>
          <mat-card-title>{{ editing ? 'Edit Student' : student.fullName }}</mat-card-title>
        </mat-card-header>
        <mat-card-content>
          @if (editing) {
            <form #editForm="ngForm" class="form-grid">
              <mat-form-field>
                <mat-label>Full Name</mat-label>
                <input matInput [(ngModel)]="draft.fullName" name="fullName" required />
              </mat-form-field>
              <mat-form-field>
                <mat-label>Number</mat-label>
                <input matInput type="number" [(ngModel)]="draft.number" name="number" required />
              </mat-form-field>
              <mat-form-field>
                <mat-label>Email</mat-label>
                <input matInput type="email" [(ngModel)]="draft.email" name="email" required />
              </mat-form-field>
              <mat-form-field>
                <mat-label>Phone (GSM)</mat-label>
                <input matInput [(ngModel)]="draft.gsmNumber" name="gsmNumber" />
              </mat-form-field>
            </form>
          } @else {
            <div class="detail-grid">
              <div class="detail-row"><strong>Number:</strong> {{ student.number }}</div>
              <div class="detail-row"><strong>Email:</strong> {{ student.email }}</div>
              <div class="detail-row"><strong>Phone:</strong> {{ student.gsmNumber || '—' }}</div>
            </div>
          }
        </mat-card-content>
        <mat-card-actions align="end">
          @if (editing) {
            <button mat-button (click)="cancelEdit()">Cancel</button>
            <button mat-flat-button (click)="saveEdit()">Save</button>
          } @else {
            <button mat-flat-button (click)="startEdit()"><mat-icon>edit</mat-icon> Edit</button>
          }
        </mat-card-actions>
      </mat-card>

      @if (examRows.length) {
        <div class="section-header">
          <h3>Exam Results</h3>
          <button mat-flat-button [routerLink]="['/exam-results/add', student.id]">
            <mat-icon>edit</mat-icon> Edit Scores
          </button>
        </div>
        <table mat-table [dataSource]="examRows" class="mat-elevation-z1">
          <ng-container matColumnDef="courseName">
            <th mat-header-cell *matHeaderCellDef>Course</th>
            <td mat-cell *matCellDef="let r">{{ r.courseName }}</td>
          </ng-container>

          <ng-container matColumnDef="score1">
            <th mat-header-cell *matHeaderCellDef>Score 1</th>
            <td mat-cell *matCellDef="let r">{{ r.scores[0] ?? '—' }}</td>
          </ng-container>

          <ng-container matColumnDef="score2">
            <th mat-header-cell *matHeaderCellDef>Score 2</th>
            <td mat-cell *matCellDef="let r">{{ r.scores[1] ?? '—' }}</td>
          </ng-container>

          <ng-container matColumnDef="score3">
            <th mat-header-cell *matHeaderCellDef>Score 3</th>
            <td mat-cell *matCellDef="let r">{{ r.scores[2] ?? '—' }}</td>
          </ng-container>

          <ng-container matColumnDef="average">
            <th mat-header-cell *matHeaderCellDef>Average</th>
            <td mat-cell *matCellDef="let r">
              @if (r.average !== null) {
                <mat-chip [highlighted]="true">{{ r.average | number: '1.0-1' }}</mat-chip>
              } @else {
                <span class="muted">—</span>
              }
            </td>
          </ng-container>

          <tr mat-header-row *matHeaderRowDef="examColumns"></tr>
          <tr mat-row *matRowDef="let row; columns: examColumns"></tr>
        </table>
      }
    }
  `,
  styles: `
    .page-header {
      margin-bottom: 1rem;
    }
    .form-grid {
      display: flex;
      flex-direction: column;
      padding-top: 1rem;
    }
    .detail-grid {
      padding-top: 1rem;
    }
    .detail-row {
      padding: 0.5rem 0;
    }
    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-top: 2rem;
      margin-bottom: 1rem;
    }
    .section-header h3 {
      margin: 0;
    }
    table {
      width: 100%;
    }
    .muted {
      color: #999;
    }
  `,
})
export class StudentDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private studentService = inject(StudentService);
  private examResultService = inject(ExamResultService);
  private snackBar = inject(MatSnackBar);
  private cdr = inject(ChangeDetectorRef);

  student: Student | null = null;
  draft!: Student;
  editing = false;
  examRows: { courseName: string; scores: (number | null)[]; average: number | null }[] = [];
  examColumns = ['courseName', 'score1', 'score2', 'score3', 'average'];

  ngOnInit() {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.studentService.getById(id).subscribe({
      next: (s) => {
        this.student = s;
        this.cdr.markForCheck();
      },
      error: () => {
        this.snackBar.open('Student not found', 'Close', { duration: 3000 });
        this.router.navigate(['/students']);
      },
    });
    this.examResultService.getStudentScores(id).subscribe({
      next: (data) => {
        this.examRows = data.courses.map((c) => this.mapCourseRow(c));
        this.cdr.markForCheck();
      },
    });
  }

  private mapCourseRow(c: CourseScores) {
    const scores = c.scores.map((s) => s.score);
    const valid = scores.filter((s): s is number => s !== null);
    const average = valid.length === 3 ? Math.round((valid.reduce((a, b) => a + b, 0) / 3) * 10) / 10 : null;
    return { courseName: c.courseName, scores, average };
  }

  startEdit() {
    this.draft = { ...this.student! };
    this.editing = true;
  }

  cancelEdit() {
    this.editing = false;
  }

  saveEdit() {
    this.studentService.update(this.student!.id!, this.draft).subscribe({
      next: (updated) => {
        this.student = updated;
        this.editing = false;
        this.snackBar.open('Student updated', 'Close', { duration: 3000 });
        this.cdr.markForCheck();
      },
      error: () => this.snackBar.open('Failed to update student', 'Close', { duration: 3000 }),
    });
  }
}
