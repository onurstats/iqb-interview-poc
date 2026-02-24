export interface ExamResultRow {
  id: number;
  studentId: number;
  studentName: string;
  studentNumber: number;
  courseId: number;
  courseName: string;
  score: number;
}

export interface StudentScores {
  courses: CourseScores[];
}

export interface CourseScores {
  courseId: number;
  courseName: string;
  scores: ScoreEntry[];
}

export interface ScoreEntry {
  id: number | null;
  score: number | null;
}

export interface SaveScoresRequest {
  courses: {
    courseId: number;
    scores: { id: number | null; score: number | null }[];
  }[];
}
