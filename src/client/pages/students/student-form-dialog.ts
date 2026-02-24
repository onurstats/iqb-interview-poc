import { Component, Inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { StudentService } from '../../services/student.service';
import { Student } from '../../models/student.model';

@Component({
  selector: 'app-student-form-dialog',
  imports: [
    FormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSnackBarModule,
  ],
  template: `
    <h2 mat-dialog-title>{{ isEdit ? 'Edit' : 'Add' }} Student</h2>
    <mat-dialog-content>
      <form #studentForm="ngForm" class="form-grid">
        <mat-form-field>
          <mat-label>Full Name</mat-label>
          <input matInput [(ngModel)]="student.fullName" name="fullName" required>
        </mat-form-field>

        <mat-form-field>
          <mat-label>Number</mat-label>
          <input matInput type="number" [(ngModel)]="student.number" name="number" required>
        </mat-form-field>

        <mat-form-field>
          <mat-label>Email</mat-label>
          <input matInput type="email" [(ngModel)]="student.email" name="email" required>
        </mat-form-field>

        <mat-form-field>
          <mat-label>Phone (GSM)</mat-label>
          <input matInput [(ngModel)]="student.gsmNumber" name="gsmNumber">
        </mat-form-field>
      </form>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Cancel</button>
      <button mat-flat-button (click)="save()" [disabled]="!studentForm.valid">Save</button>
    </mat-dialog-actions>
  `,
  styles: `
    .form-grid {
      display: flex;
      flex-direction: column;
      min-width: 320px;
    }
  `,
})
export class StudentFormDialogComponent {
  student: Student;
  isEdit: boolean;

  constructor(
    private dialogRef: MatDialogRef<StudentFormDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: Student | null,
    private studentService: StudentService,
    private snackBar: MatSnackBar,
  ) {
    this.isEdit = !!data;
    this.student = data ?? { fullName: '', number: 0, email: '' };
  }

  save() {
    const op = this.isEdit
      ? this.studentService.update(this.student.id!, this.student)
      : this.studentService.create(this.student);

    op.subscribe({
      next: () => {
        this.snackBar.open(`Student ${this.isEdit ? 'updated' : 'created'}`, 'Close', { duration: 3000 });
        this.dialogRef.close(true);
      },
      error: () => this.snackBar.open('Failed to save student', 'Close', { duration: 3000 }),
    });
  }
}
