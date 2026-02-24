import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatDividerModule } from '@angular/material/divider';
import { MatButtonModule } from '@angular/material/button';
import { DecimalPipe } from '@angular/common';
import { DashboardService } from '../../services/dashboard.service';
import { DashboardStats } from '../../models/dashboard-stats.model';

@Component({
  selector: 'app-dashboard',
  imports: [
    RouterLink,
    MatCardModule,
    MatIconModule,
    MatListModule,
    MatProgressBarModule,
    MatDividerModule,
    MatButtonModule,
    DecimalPipe,
  ],
  template: `
    @if (stats) {
      <div class="welcome-section">
        <div class="welcome-text">
          <h2>Welcome to IQB Interview POC</h2>
          <p>Manage students, courses, and exam scores from one place.</p>
        </div>
        <div class="shortcuts">
          <a mat-flat-button routerLink="/exam-results">
            <mat-icon>edit_note</mat-icon>
            Enter Exam Scores
          </a>
          <a mat-stroked-button routerLink="/students">
            <mat-icon>person_add</mat-icon>
            Manage Students
          </a>
          <a mat-stroked-button routerLink="/courses">
            <mat-icon>library_add</mat-icon>
            Manage Courses
          </a>
        </div>
      </div>

      <div class="stats-grid">
        <mat-card class="stat-card" [routerLink]="['/students']">
          <mat-card-content>
            <div class="stat-icon"><mat-icon>people</mat-icon></div>
            <div class="stat-value">{{ stats.totalStudents }}</div>
            <div class="stat-label">Total Students</div>
          </mat-card-content>
        </mat-card>

        <mat-card class="stat-card" [routerLink]="['/courses']">
          <mat-card-content>
            <div class="stat-icon"><mat-icon>menu_book</mat-icon></div>
            <div class="stat-value">{{ stats.totalCourses }}</div>
            <div class="stat-label">Total Courses</div>
          </mat-card-content>
        </mat-card>

        <mat-card class="stat-card">
          <mat-card-content>
            <div class="stat-icon"><mat-icon>assignment</mat-icon></div>
            <div class="stat-value">{{ stats.totalExamResults }}</div>
            <div class="stat-label">Exam Results</div>
          </mat-card-content>
        </mat-card>

        <mat-card class="stat-card">
          <mat-card-content>
            <div class="stat-icon"><mat-icon>trending_up</mat-icon></div>
            <div class="stat-value">{{ stats.averageScore | number:'1.1-1' }}</div>
            <div class="stat-label">Average Score</div>
          </mat-card-content>
        </mat-card>
      </div>

      <div class="detail-grid">
        <mat-card>
          <mat-card-header>
            <mat-icon mat-card-avatar>school</mat-icon>
            <mat-card-title>Course Completion</mat-card-title>
          </mat-card-header>
          <mat-card-content>
            <div class="completion-row">
              <mat-icon class="completed">check_circle</mat-icon>
              <span class="completion-value">{{ stats.completedPairs }}</span>
              <span class="completion-label">Completed</span>
            </div>
            <mat-divider></mat-divider>
            <div class="completion-row">
              <mat-icon class="in-progress">schedule</mat-icon>
              <span class="completion-value">{{ stats.inProgressPairs }}</span>
              <span class="completion-label">In Progress</span>
            </div>
          </mat-card-content>
        </mat-card>

        <mat-card>
          <mat-card-header>
            <mat-icon mat-card-avatar>bar_chart</mat-icon>
            <mat-card-title>Score Distribution</mat-card-title>
          </mat-card-header>
          <mat-card-content>
            <div class="dist-row">
              <span class="dist-label">81-100</span>
              <mat-progress-bar mode="determinate" [value]="distributionPercent(stats.scoreDistribution.range81to100)" class="dist-excellent"></mat-progress-bar>
              <span class="dist-count">{{ stats.scoreDistribution.range81to100 }}</span>
            </div>
            <div class="dist-row">
              <span class="dist-label">61-80</span>
              <mat-progress-bar mode="determinate" [value]="distributionPercent(stats.scoreDistribution.range61to80)" class="dist-good"></mat-progress-bar>
              <span class="dist-count">{{ stats.scoreDistribution.range61to80 }}</span>
            </div>
            <div class="dist-row">
              <span class="dist-label">41-60</span>
              <mat-progress-bar mode="determinate" [value]="distributionPercent(stats.scoreDistribution.range41to60)" class="dist-average"></mat-progress-bar>
              <span class="dist-count">{{ stats.scoreDistribution.range41to60 }}</span>
            </div>
            <div class="dist-row">
              <span class="dist-label">21-40</span>
              <mat-progress-bar mode="determinate" [value]="distributionPercent(stats.scoreDistribution.range21to40)" class="dist-poor"></mat-progress-bar>
              <span class="dist-count">{{ stats.scoreDistribution.range21to40 }}</span>
            </div>
            <div class="dist-row">
              <span class="dist-label">0-20</span>
              <mat-progress-bar mode="determinate" [value]="distributionPercent(stats.scoreDistribution.range0to20)" class="dist-fail"></mat-progress-bar>
              <span class="dist-count">{{ stats.scoreDistribution.range0to20 }}</span>
            </div>
          </mat-card-content>
        </mat-card>
      </div>

      <div class="detail-grid">
        <mat-card>
          <mat-card-header>
            <mat-icon mat-card-avatar>emoji_events</mat-icon>
            <mat-card-title>Top Performing Students</mat-card-title>
          </mat-card-header>
          <mat-card-content>
            <mat-list>
              @for (student of stats.topStudents; track student.studentId; let i = $index) {
                <a mat-list-item [routerLink]="['/students', student.studentId]">
                  <span matListItemTitle>
                    <span class="rank">#{{ i + 1 }}</span>
                    {{ student.fullName }}
                  </span>
                  <span matListItemMeta class="student-score">{{ student.averageScore | number:'1.1-1' }}</span>
                </a>
                @if (i < stats.topStudents.length - 1) {
                  <mat-divider></mat-divider>
                }
              }
            </mat-list>
          </mat-card-content>
        </mat-card>

        <mat-card>
          <mat-card-header>
            <mat-icon mat-card-avatar>history</mat-icon>
            <mat-card-title>Recent Exam Results</mat-card-title>
          </mat-card-header>
          <mat-card-content>
            <mat-list>
              @for (result of stats.recentResults; track result.id; let i = $index) {
                <mat-list-item>
                  <span matListItemTitle>{{ result.studentName }}</span>
                  <span matListItemLine>{{ result.courseName }}</span>
                  <span matListItemMeta class="result-score">{{ result.score }}</span>
                </mat-list-item>
                @if (i < stats.recentResults.length - 1) {
                  <mat-divider></mat-divider>
                }
              }
            </mat-list>
          </mat-card-content>
        </mat-card>
      </div>
    } @else {
      <div class="loading">
        <mat-progress-bar mode="indeterminate"></mat-progress-bar>
        <p>Loading dashboard...</p>
      </div>
    }
  `,
  styles: `
    .welcome-section {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 1.5rem;
      gap: 1rem;
    }

    .welcome-text h2 {
      margin: 0;
      font-weight: 400;
      font-size: 1.5rem;
    }

    .welcome-text p {
      margin: 0.25rem 0 0;
      color: var(--mat-sys-on-surface-variant);
      font-size: 0.95rem;
    }

    .shortcuts {
      display: flex;
      gap: 0.75rem;
      flex-shrink: 0;
    }

    .shortcuts a mat-icon {
      margin-right: 0.25rem;
    }

    .stats-grid {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 1rem;
      margin-bottom: 1.5rem;
    }

    .stat-card {
      cursor: pointer;
      transition: box-shadow 0.2s;
      &:hover {
        box-shadow: var(--mat-sys-level3);
      }
    }

    .stat-card mat-card-content {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 1.5rem 1rem;
    }

    .stat-icon mat-icon {
      font-size: 2rem;
      width: 2rem;
      height: 2rem;
      color: var(--mat-sys-primary);
      margin-bottom: 0.5rem;
    }

    .stat-value {
      font-size: 2rem;
      font-weight: 700;
      color: var(--mat-sys-on-surface);
    }

    .stat-label {
      font-size: 0.875rem;
      color: var(--mat-sys-on-surface-variant);
      margin-top: 0.25rem;
    }

    .detail-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 1rem;
      margin-bottom: 1.5rem;
    }

    mat-card-header {
      margin-bottom: 1rem;
    }

    .completion-row {
      display: flex;
      align-items: center;
      gap: 0.75rem;
      padding: 0.75rem 0;
    }

    .completion-value {
      font-size: 1.5rem;
      font-weight: 600;
    }

    .completion-label {
      font-size: 0.95rem;
      color: var(--mat-sys-on-surface-variant);
    }

    .completed { color: #4caf50; }
    .in-progress { color: #ff9800; }

    .dist-row {
      display: flex;
      align-items: center;
      gap: 0.75rem;
      margin: 0.5rem 0;
    }

    .dist-label {
      width: 3.5rem;
      text-align: right;
      font-size: 0.875rem;
      color: var(--mat-sys-on-surface-variant);
    }

    .dist-count {
      width: 2rem;
      text-align: right;
      font-size: 0.875rem;
      font-weight: 500;
    }

    mat-progress-bar {
      flex: 1;
      border-radius: 4px;
    }

    .dist-excellent ::ng-deep .mdc-linear-progress__bar-inner { border-color: #4caf50; }
    .dist-good ::ng-deep .mdc-linear-progress__bar-inner { border-color: #8bc34a; }
    .dist-average ::ng-deep .mdc-linear-progress__bar-inner { border-color: #ff9800; }
    .dist-poor ::ng-deep .mdc-linear-progress__bar-inner { border-color: #ff5722; }
    .dist-fail ::ng-deep .mdc-linear-progress__bar-inner { border-color: #f44336; }

    .rank {
      font-weight: 600;
      color: var(--mat-sys-primary);
      margin-right: 0.5rem;
      min-width: 1.5rem;
    }

    .student-score {
      font-weight: 600;
      color: var(--mat-sys-primary);
    }

    a[mat-list-item] {
      text-decoration: none;
      color: inherit;
    }

    .result-score {
      font-weight: 600;
      font-size: 1.1rem;
      color: var(--mat-sys-primary);
    }

    .loading {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      padding: 4rem;
      gap: 1rem;

      mat-progress-bar {
        width: 200px;
      }
    }

    @media (max-width: 900px) {
      .welcome-section { flex-direction: column; align-items: flex-start; }
      .shortcuts { flex-wrap: wrap; }
      .stats-grid { grid-template-columns: repeat(2, 1fr); }
      .detail-grid { grid-template-columns: 1fr; }
    }
  `,
})
export class DashboardComponent implements OnInit {
  stats: DashboardStats | null = null;

  constructor(
    private dashboardService: DashboardService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    this.dashboardService.getStats().subscribe({
      next: (data) => {
        this.stats = data;
        this.cdr.markForCheck();
      },
    });
  }

  get maxDistribution(): number {
    if (!this.stats) return 1;
    const d = this.stats.scoreDistribution;
    return Math.max(d.range0to20, d.range21to40, d.range41to60, d.range61to80, d.range81to100, 1);
  }

  distributionPercent(value: number): number {
    return (value / this.maxDistribution) * 100;
  }
}
