import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Course } from '../models/course.model';
import { Page } from '../models/page.model';

@Injectable({ providedIn: 'root' })
export class CourseService {
  private http = inject(HttpClient);

  private readonly baseUrl = 'http://localhost:8080/api/courses';

  getAll(page = 0, size = 10, search?: string): Observable<Page<Course>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (search) {
      params = params.set('search', search);
    }
    return this.http.get<Page<Course>>(this.baseUrl, { params });
  }

  getById(id: number): Observable<Course> {
    return this.http.get<Course>(`${this.baseUrl}/${id}`);
  }

  create(course: Course): Observable<Course> {
    return this.http.post<Course>(this.baseUrl, course);
  }

  update(id: number, course: Course): Observable<Course> {
    return this.http.put<Course>(`${this.baseUrl}/${id}`, course);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
