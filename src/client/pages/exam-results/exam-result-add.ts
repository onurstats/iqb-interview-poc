import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { StudentService } from '../../services/student.service';
import { Student } from '../../models/student.model';
import { Subject, debounceTime, distinctUntilChanged } from 'rxjs';

@Component({
  selector: 'app-exam-result-add',
  imports: [
    FormsModule,
    MatTableModule,
    MatPaginatorModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
  ],
  template: `
    <div class="header-row">
      <button mat-icon-button (click)="goBack()">
        <mat-icon>arrow_back</mat-icon>
      </button>
      <h2>Select Student</h2>
    </div>
    <p class="subtitle">Choose a student to enter or edit exam scores.</p>

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
        <td mat-cell *matCellDef="let s">{{ s.fullName }}</td>
      </ng-container>

      <ng-container matColumnDef="email">
        <th mat-header-cell *matHeaderCellDef>Email</th>
        <td mat-cell *matCellDef="let s">{{ s.email }}</td>
      </ng-container>

      <ng-container matColumnDef="actions">
        <th mat-header-cell *matHeaderCellDef></th>
        <td mat-cell *matCellDef="let s">
          <button mat-flat-button (click)="selectStudent(s)"><mat-icon>arrow_forward</mat-icon> Select</button>
        </td>
      </ng-container>

      <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
      <tr mat-row *matRowDef="let row; columns: displayedColumns"></tr>
    </table>

    <mat-paginator
      [length]="totalElements"
      [pageSize]="pageSize"
      [pageIndex]="pageIndex"
      [pageSizeOptions]="[5, 10, 25]"
      (page)="onPage($event)"
      showFirstLastButtons
    >
    </mat-paginator>
  `,
  styles: `
    .header-row {
      display: flex;
      align-items: center;
      gap: 0.5rem;
    }
    .header-row h2 {
      margin: 0;
    }
    .subtitle {
      color: var(--mat-sys-on-surface-variant);
      margin: 0 0 1rem;
    }
    .search-field {
      width: 100%;
      margin-bottom: 1rem;
    }
    table {
      width: 100%;
    }
  `,
})
export class ExamResultAddComponent implements OnInit {
  private studentService = inject(StudentService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  dataSource = new MatTableDataSource<Student>();
  displayedColumns = ['number', 'fullName', 'email', 'actions'];
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

  goBack() {
    this.router.navigate(['/exam-results']);
  }

  loadStudents() {
    this.studentService.getAll(this.pageIndex, this.pageSize, this.searchTerm || undefined).subscribe({
      next: (page) => {
        this.dataSource.data = page.content;
        this.totalElements = page.totalElements;
        this.cdr.markForCheck();
      },
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

  selectStudent(student: Student) {
    this.router.navigate(['/exam-results/add', student.id]);
  }
}
