import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CourseService } from '../../services/course.service';
import { Course } from '../../models/course.model';

@Component({
  selector: 'app-course-list',
  imports: [
    FormsModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatSnackBarModule,
  ],
  template: `
    <div class="page-header">
      <h2>Courses</h2>
      <button mat-flat-button (click)="startAdd()">
        <mat-icon>add</mat-icon> Add Course
      </button>
    </div>

    <table mat-table [dataSource]="dataSource" class="mat-elevation-z1">
      <ng-container matColumnDef="name">
        <th mat-header-cell *matHeaderCellDef>Name</th>
        <td mat-cell *matCellDef="let c">
          @if (editingId === c.id) {
            <mat-form-field class="inline-field">
              <input matInput [(ngModel)]="editName" (keyup.enter)="saveEdit(c)" (keyup.escape)="cancelEdit()">
            </mat-form-field>
          } @else {
            {{ c.name }}
          }
        </td>
      </ng-container>

      <ng-container matColumnDef="actions">
        <th mat-header-cell *matHeaderCellDef>Actions</th>
        <td mat-cell *matCellDef="let c">
          @if (editingId === c.id) {
            <button mat-icon-button (click)="saveEdit(c)" aria-label="Save">
              <mat-icon>check</mat-icon>
            </button>
            <button mat-icon-button (click)="cancelEdit()" aria-label="Cancel">
              <mat-icon>close</mat-icon>
            </button>
          } @else {
            <button mat-icon-button (click)="startEdit(c)" aria-label="Edit">
              <mat-icon>edit</mat-icon>
            </button>
            <button mat-icon-button color="warn" (click)="deleteCourse(c)" aria-label="Delete">
              <mat-icon>delete</mat-icon>
            </button>
          }
        </td>
      </ng-container>

      <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
      <tr mat-row *matRowDef="let row; columns: displayedColumns"></tr>
    </table>

    @if (adding) {
      <div class="add-row">
        <mat-form-field>
          <mat-label>Course Name</mat-label>
          <input matInput [(ngModel)]="newName" (keyup.enter)="saveNew()" (keyup.escape)="cancelAdd()">
        </mat-form-field>
        <button mat-icon-button (click)="saveNew()" aria-label="Save">
          <mat-icon>check</mat-icon>
        </button>
        <button mat-icon-button (click)="cancelAdd()" aria-label="Cancel">
          <mat-icon>close</mat-icon>
        </button>
      </div>
    }
  `,
  styles: `
    .page-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 1rem;
    }
    table { width: 100%; }
    .inline-field { width: 100%; }
    .add-row {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      margin-top: 1rem;
    }
  `,
})
export class CourseListComponent implements OnInit {
  dataSource = new MatTableDataSource<Course>();
  displayedColumns = ['name', 'actions'];

  editingId: number | null = null;
  editName = '';

  adding = false;
  newName = '';

  constructor(
    private courseService: CourseService,
    private snackBar: MatSnackBar,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    this.loadCourses();
  }

  loadCourses() {
    this.courseService.getAll().subscribe({
      next: (data) => {
        this.dataSource.data = data;
        this.cdr.markForCheck();
      },
      error: () => this.snackBar.open('Failed to load courses', 'Close', { duration: 3000 }),
    });
  }

  startEdit(course: Course) {
    this.editingId = course.id!;
    this.editName = course.name;
  }

  cancelEdit() {
    this.editingId = null;
  }

  saveEdit(course: Course) {
    if (!this.editName.trim()) return;
    this.courseService.update(course.id!, { name: this.editName.trim() }).subscribe({
      next: () => {
        this.editingId = null;
        this.snackBar.open('Course updated', 'Close', { duration: 3000 });
        this.loadCourses();
        this.cdr.markForCheck();
      },
      error: () => this.snackBar.open('Failed to update course', 'Close', { duration: 3000 }),
    });
  }

  startAdd() {
    this.adding = true;
    this.newName = '';
  }

  cancelAdd() {
    this.adding = false;
  }

  saveNew() {
    if (!this.newName.trim()) return;
    this.courseService.create({ name: this.newName.trim() }).subscribe({
      next: () => {
        this.adding = false;
        this.snackBar.open('Course created', 'Close', { duration: 3000 });
        this.loadCourses();
        this.cdr.markForCheck();
      },
      error: () => this.snackBar.open('Failed to create course', 'Close', { duration: 3000 }),
    });
  }

  deleteCourse(course: Course) {
    if (!confirm(`Delete course "${course.name}"?`)) return;
    this.courseService.delete(course.id!).subscribe({
      next: () => {
        this.snackBar.open('Course deleted', 'Close', { duration: 3000 });
        this.loadCourses();
      },
      error: () => this.snackBar.open('Failed to delete course', 'Close', { duration: 3000 }),
    });
  }
}
