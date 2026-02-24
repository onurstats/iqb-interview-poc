import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { StudentService } from '../../services/student.service';
import { Student } from '../../models/student.model';

@Component({
  selector: 'app-student-detail',
  imports: [
    RouterLink,
    FormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatSnackBarModule,
  ],
  template: `
    @if (student) {
      <div class="page-header">
        <button mat-button routerLink="/students">
          <mat-icon>arrow_back</mat-icon> Back to list
        </button>
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
                <input matInput [(ngModel)]="draft.fullName" name="fullName" required>
              </mat-form-field>
              <mat-form-field>
                <mat-label>Number</mat-label>
                <input matInput type="number" [(ngModel)]="draft.number" name="number" required>
              </mat-form-field>
              <mat-form-field>
                <mat-label>Email</mat-label>
                <input matInput type="email" [(ngModel)]="draft.email" name="email" required>
              </mat-form-field>
              <mat-form-field>
                <mat-label>Phone (GSM)</mat-label>
                <input matInput [(ngModel)]="draft.gsmNumber" name="gsmNumber">
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
            <button mat-flat-button (click)="startEdit()">
              <mat-icon>edit</mat-icon> Edit
            </button>
          }
        </mat-card-actions>
      </mat-card>
    }
  `,
  styles: `
    .page-header { margin-bottom: 1rem; }
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
  `,
})
export class StudentDetailComponent implements OnInit {
  student: Student | null = null;
  draft!: Student;
  editing = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private studentService: StudentService,
    private snackBar: MatSnackBar,
    private cdr: ChangeDetectorRef,
  ) {}

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
