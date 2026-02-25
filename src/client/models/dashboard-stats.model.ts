export interface DashboardStats {
  totalStudents: number;
  totalCourses: number;
  totalExamResults: number;
  averageScore: number;
  completedPairs: number;
  inProgressPairs: number;
  topStudents: TopStudent[];
  recentResults: RecentResult[];
  scoreDistribution: ScoreDistribution;
}

export interface TopStudent {
  studentId: number;
  fullName: string;
  averageScore: number;
}

export interface RecentResult {
  id: number;
  studentName: string;
  courseName: string;
  score: number;
  createdAt: string;
}

export interface ScoreDistribution {
  range0to20: number;
  range21to40: number;
  range41to60: number;
  range61to80: number;
  range81to100: number;
}
