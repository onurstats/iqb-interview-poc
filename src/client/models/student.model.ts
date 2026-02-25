export interface Student {
  id?: number;
  fullName: string;
  number: number;
  email: string;
  gsmNumber?: string;
  completedCourses?: { courseName: string; average: number }[];
}
