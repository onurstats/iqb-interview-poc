import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { MenuItem } from 'primeng/api';
import { Menubar } from 'primeng/menubar';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Menubar],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {
  items: MenuItem[] = [
    { label: 'Students', icon: 'pi pi-users', routerLink: '/students' },
    { label: 'Courses', icon: 'pi pi-book', routerLink: '/courses' },
    { label: 'Exam Scores', icon: 'pi pi-chart-bar', routerLink: '/exam-results' },
  ];
}
