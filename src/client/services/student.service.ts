import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Student } from '../models/student.model';
import { Page } from '../models/page.model';
import { environment } from '../environments/environment';

@Injectable({ providedIn: 'root' })
export class StudentService {
  private http = inject(HttpClient);

  private readonly baseUrl = `${environment.apiUrl}/api/students`;

  getAll(page = 0, size = 10, search?: string): Observable<Page<Student>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (search) {
      params = params.set('search', search);
    }
    return this.http.get<Page<Student>>(this.baseUrl, { params });
  }

  getById(id: number): Observable<Student> {
    return this.http.get<Student>(`${this.baseUrl}/${id}`);
  }

  create(student: Student): Observable<Student> {
    return this.http.post<Student>(this.baseUrl, student);
  }

  update(id: number, student: Student): Observable<Student> {
    return this.http.put<Student>(`${this.baseUrl}/${id}`, student);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
