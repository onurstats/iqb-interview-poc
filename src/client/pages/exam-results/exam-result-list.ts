import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ExamResultService } from '../../services/exam-result.service';
import { ExamResultRow } from '../../models/exam-result.model';
import { Subject, debounceTime, distinctUntilChanged } from 'rxjs';

@Component({
  selector: 'app-exam-result-list',
  imports: [
    RouterLink,
    FormsModule,
    MatTableModule,
    MatPaginatorModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatSnackBarModule,
  ],
  template: `
    <div class="header-row">
      <h2>Exam Results</h2>
      <button mat-flat-button routerLink="/exam-results/add"><mat-icon>add</mat-icon> Add Scores</button>
    </div>

    <mat-form-field class="search-field">
      <mat-label>Search results</mat-label>
      <input
        matInput
        [ngModel]="searchTerm"
        (ngModelChange)="onSearch($event)"
        placeholder="Student, course, or score"
      />
      <mat-icon matSuffix>search</mat-icon>
    </mat-form-field>

    <table mat-table [dataSource]="dataSource" class="mat-elevation-z1">
      <ng-container matColumnDef="studentName">
        <th mat-header-cell *matHeaderCellDef>Student</th>
        <td mat-cell *matCellDef="let row">{{ row.studentName }}</td>
      </ng-container>

      <ng-container matColumnDef="studentNumber">
        <th mat-header-cell *matHeaderCellDef>Number</th>
        <td mat-cell *matCellDef="let row">{{ row.studentNumber }}</td>
      </ng-container>

      <ng-container matColumnDef="courseName">
        <th mat-header-cell *matHeaderCellDef>Course</th>
        <td mat-cell *matCellDef="let row">{{ row.courseName }}</td>
      </ng-container>

      <ng-container matColumnDef="score">
        <th mat-header-cell *matHeaderCellDef>Score</th>
        <td mat-cell *matCellDef="let row">
          <span
            class="score-badge"
            [class.high]="row.score >= 80"
            [class.mid]="row.score >= 50 && row.score < 80"
            [class.low]="row.score < 50"
          >
            {{ row.score }}
          </span>
        </td>
      </ng-container>

      <ng-container matColumnDef="actions">
        <th mat-header-cell *matHeaderCellDef></th>
        <td mat-cell *matCellDef="let row">
          <a mat-icon-button [routerLink]="['/exam-results/add', row.studentId]" aria-label="Edit scores">
            <mat-icon>edit</mat-icon>
          </a>
        </td>
      </ng-container>

      <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
      <tr mat-row *matRowDef="let row; columns: displayedColumns"></tr>
    </table>

    <mat-paginator
      [length]="totalElements"
      [pageSize]="pageSize"
      [pageIndex]="pageIndex"
      [pageSizeOptions]="[10, 25, 50]"
      (page)="onPage($event)"
      showFirstLastButtons
    >
    </mat-paginator>
  `,
  styles: `
    .header-row {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 0.5rem;
    }
    .header-row h2 {
      margin: 0;
    }
    .search-field {
      width: 100%;
      margin-bottom: 1rem;
    }
    table {
      width: 100%;
    }
    .score-badge {
      font-weight: 600;
      padding: 0.2rem 0.6rem;
      border-radius: 4px;
    }
    .score-badge.high {
      color: #2e7d32;
      background: #e8f5e9;
    }
    .score-badge.mid {
      color: #ef6c00;
      background: #fff3e0;
    }
    .score-badge.low {
      color: #c62828;
      background: #ffebee;
    }
  `,
})
export class ExamResultListComponent implements OnInit {
  private examResultService = inject(ExamResultService);
  private snackBar = inject(MatSnackBar);
  private cdr = inject(ChangeDetectorRef);

  dataSource = new MatTableDataSource<ExamResultRow>();
  displayedColumns = ['studentName', 'studentNumber', 'courseName', 'score', 'actions'];
  searchTerm = '';
  totalElements = 0;
  pageSize = 10;
  pageIndex = 0;
  private searchSubject = new Subject<string>();

  constructor() {
    this.searchSubject.pipe(debounceTime(300), distinctUntilChanged()).subscribe(() => {
      this.pageIndex = 0;
      this.loadResults();
    });
  }

  ngOnInit() {
    this.loadResults();
  }

  loadResults() {
    this.examResultService.getAll(this.pageIndex, this.pageSize, this.searchTerm || undefined).subscribe({
      next: (page) => {
        this.dataSource.data = page.content;
        this.totalElements = page.totalElements;
        this.cdr.markForCheck();
      },
      error: () => {
        this.snackBar.open('Failed to load exam results', 'Close', { duration: 3000 });
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
    this.loadResults();
  }
}
