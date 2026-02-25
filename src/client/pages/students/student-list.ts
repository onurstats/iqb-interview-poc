import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { MatChipsModule } from '@angular/material/chips';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { StudentService } from '../../services/student.service';
import { Student } from '../../models/student.model';
import { StudentFormDialogComponent } from './student-form-dialog';
import { Subject, debounceTime, distinctUntilChanged } from 'rxjs';

@Component({
  selector: 'app-student-list',
  imports: [
    RouterLink,
    FormsModule,
    MatTableModule,
    MatPaginatorModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatDialogModule,
    MatSnackBarModule,
    MatChipsModule,
    DecimalPipe,
  ],
  template: `
    <div class="page-header">
      <h2>Students</h2>
      <button mat-flat-button (click)="openForm()"><mat-icon>add</mat-icon> Add Student</button>
    </div>

    <mat-form-field class="search-field">
      <mat-label>Search students</mat-label>
      <input
        matInput
        [ngModel]="searchTerm"
        (ngModelChange)="onSearch($event)"
        placeholder="Name, number, email, or phone"
      />
      <mat-icon matSuffix>search</mat-icon>
    </mat-form-field>

    <table mat-table [dataSource]="dataSource" class="mat-elevation-z1">
      <ng-container matColumnDef="number">
        <th mat-header-cell *matHeaderCellDef>Number</th>
        <td mat-cell *matCellDef="let s">{{ s.number }}</td>
      </ng-container>

      <ng-container matColumnDef="fullName">
        <th mat-header-cell *matHeaderCellDef>Full Name</th>
        <td mat-cell *matCellDef="let s">
          <a [routerLink]="['/students', s.id]">{{ s.fullName }}</a>
        </td>
      </ng-container>

      <ng-container matColumnDef="email">
        <th mat-header-cell *matHeaderCellDef>Email</th>
        <td mat-cell *matCellDef="let s">{{ s.email }}</td>
      </ng-container>

      <ng-container matColumnDef="gsmNumber">
        <th mat-header-cell *matHeaderCellDef>Phone</th>
        <td mat-cell *matCellDef="let s">{{ s.gsmNumber }}</td>
      </ng-container>

      <ng-container matColumnDef="completedCourses">
        <th mat-header-cell *matHeaderCellDef>Completed Courses</th>
        <td mat-cell *matCellDef="let s">
          @if (s.completedCourses?.length) {
            <mat-chip-set>
              @for (cc of s.completedCourses; track cc.courseName) {
                <mat-chip [highlighted]="true">
                  {{ cc.courseName }}: {{ cc.average | number:'1.0-0' }}
                </mat-chip>
              }
            </mat-chip-set>
          } @else {
            <span class="muted">&mdash;</span>
          }
        </td>
      </ng-container>

      <ng-container matColumnDef="actions">
        <th mat-header-cell *matHeaderCellDef>Actions</th>
        <td mat-cell *matCellDef="let s">
          <button mat-icon-button (click)="openForm(s)" aria-label="Edit">
            <mat-icon>edit</mat-icon>
          </button>
          <button mat-icon-button color="warn" (click)="deleteStudent(s)" aria-label="Delete">
            <mat-icon>delete</mat-icon>
          </button>
        </td>
      </ng-container>

      <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
      <tr mat-row *matRowDef="let row; columns: displayedColumns"></tr>
    </table>

    <mat-paginator
      [length]="totalElements"
      [pageSize]="pageSize"
      [pageIndex]="pageIndex"
      [pageSizeOptions]="[5, 10, 25, 50]"
      (page)="onPage($event)"
      showFirstLastButtons
    >
    </mat-paginator>
  `,
  styles: `
    .page-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 1rem;
    }
    .search-field {
      width: 100%;
      margin-bottom: 1rem;
    }
    table {
      width: 100%;
    }
    a {
      color: var(--mat-sys-primary);
      text-decoration: none;
      &:hover {
        text-decoration: underline;
      }
    }
    .muted {
      color: #999;
    }
  `,
})
export class StudentListComponent implements OnInit {
  private studentService = inject(StudentService);
  private dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);
  private cdr = inject(ChangeDetectorRef);

  dataSource = new MatTableDataSource<Student>();
  displayedColumns = ['number', 'fullName', 'email', 'gsmNumber', 'completedCourses', 'actions'];
  searchTerm = '';
  totalElements = 0;
  pageSize = 10;
  pageIndex = 0;
  private searchSubject = new Subject<string>();

  constructor() {
    this.searchSubject.pipe(debounceTime(300), distinctUntilChanged()).subscribe(() => {
      this.pageIndex = 0;
      this.loadStudents();
    });
  }

  ngOnInit() {
    this.loadStudents();
  }

  loadStudents() {
    this.studentService.getAll(this.pageIndex, this.pageSize, this.searchTerm || undefined).subscribe({
      next: (page) => {
        this.dataSource.data = page.content;
        this.totalElements = page.totalElements;
        this.cdr.markForCheck();
      },
      error: () => this.snackBar.open('Failed to load students', 'Close', { duration: 3000 }),
    });
  }

  onSearch(term: string) {
    this.searchTerm = term;
    this.searchSubject.next(term);
  }

  onPage(event: PageEvent) {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadStudents();
  }

  openForm(student?: Student) {
    const dialogRef = this.dialog.open(StudentFormDialogComponent, {
      width: '480px',
      data: student ? { ...student } : null,
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result) this.loadStudents();
    });
  }

  deleteStudent(student: Student) {
    if (!confirm(`Delete student "${student.fullName}"?`)) return;
    this.studentService.delete(student.id!).subscribe({
      next: () => {
        this.snackBar.open('Student deleted', 'Close', { duration: 3000 });
        this.loadStudents();
      },
      error: () => this.snackBar.open('Failed to delete student', 'Close', { duration: 3000 }),
    });
  }
}
