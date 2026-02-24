import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ExamResultRow, StudentScores, SaveScoresRequest } from '../models/exam-result.model';
import { Page } from '../models/page.model';

@Injectable({ providedIn: 'root' })
export class ExamResultService {
  private http = inject(HttpClient);

  private readonly baseUrl = 'http://localhost:8080/api/exam-results';

  getAll(page = 0, size = 10, search?: string): Observable<Page<ExamResultRow>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (search) {
      params = params.set('search', search);
    }
    return this.http.get<Page<ExamResultRow>>(this.baseUrl, { params });
  }

  getStudentScores(studentId: number): Observable<StudentScores> {
    return this.http.get<StudentScores>(`${this.baseUrl}/student/${studentId}`);
  }

  saveStudentScores(studentId: number, request: SaveScoresRequest): Observable<StudentScores> {
    return this.http.put<StudentScores>(`${this.baseUrl}/student/${studentId}`, request);
  }
}
