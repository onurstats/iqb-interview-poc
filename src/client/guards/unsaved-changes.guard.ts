import { CanDeactivateFn } from '@angular/router';
import { ExamResultDetailComponent } from '../pages/exam-results/exam-result-detail';

export const unsavedChangesGuard: CanDeactivateFn<ExamResultDetailComponent> = (component) => {
  if (component.hasUnsavedChanges) {
    return confirm('You have unsaved changes. Are you sure you want to leave?');
  }
  return true;
};
