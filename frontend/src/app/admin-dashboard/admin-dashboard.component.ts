import { Component, OnInit, ViewChild, TemplateRef } from '@angular/core';
import { Router } from '@angular/router';
import { AdminService, Analytics } from './admin.service';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { AuthService } from '../auth/auth.service';
import { DatePipe } from '@angular/common';


@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  standalone: true,
  styleUrls: ['./admin-dashboard.component.css'],
  imports: [DatePipe],
})
export class AdminDashboardComponent implements OnInit {
  loading = true;
  error: string | null = null;
  currentUserId: number | null = null;
  pendingDeleteId: number | null = null;
  pendingResetId: number | null = null;
  resetSuccessMessage: string | null = null;
  resetErrorMessage: string | null = null;
  pendingDeleteReviewId: number | null = null;

  users: any[] = [];
  universities: any[] = [];
  courses: any[] = [];
  reviews: any[] = [];
  analytics: Analytics | null = null;

  activeTab: 'users' | 'universities' | 'courses' | 'reviews' | 'analytics' = 'users';

  constructor(
    private router: Router,
    private adminService: AdminService,
    private modalService: NgbModal,
    private authService: AuthService,
  ) { }

  ngOnInit(): void {
    if (!this.authService.isAdmin()) {
      this.router.navigate(['/']);
      return;
    }
    this.currentUserId = this.authService.getCurrentUserId();
    this.loadAll();
  }

  loadAll(): void {
    this.loading = true;
    this.error = null;
    this.adminService.getAll().subscribe({
      next: (res) => {
        this.users = res.users || [];
        this.universities = res.universities || [];
        this.courses = res.courses || [];
        this.reviews = res.reviews || [];
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load admin data';
        console.error(err);
        this.loading = false;
      },
    });
  }

  setActiveTab(tab: 'users' | 'universities' | 'courses' | 'reviews' | 'analytics'): void {
    this.activeTab = tab;
    if (tab === 'analytics' && !this.analytics) {
      this.loadAnalytics();
    }
  }

  loadAnalytics(): void {
    this.adminService.getAnalytics().subscribe({
      next: (data) => {
        this.analytics = data;
      },
      error: (err) => {
        console.error('Failed to load analytics', err);
      },
    });
  }

  @ViewChild('confirmModal') confirmModal!: TemplateRef<any>;
  @ViewChild('resetPasswordModal') resetPasswordModal!: TemplateRef<any>;

  confirmDelete(id: number): void {
    this.pendingDeleteId = id;
    const modalRef: NgbModalRef = this.modalService.open(this.confirmModal, {
      centered: true,
    });
    modalRef.result.then(
      (res) => {
        if (res === 'confirm' && this.pendingDeleteId != null) {
          this.performDelete(this.pendingDeleteId);
        }
      },
      () => {
        this.pendingDeleteId = null;
      },
    );
  }

  performDelete(id: number): void {
    this.adminService.deleteUser(id).subscribe({
      next: () => {
        this.users = this.users.filter((u) => u.id !== id);
        this.pendingDeleteId = null;
      },
      error: (err) => {
        console.error('Failed to delete user', err);
        alert('Failed to delete user');
        this.pendingDeleteId = null;
      },
    });
  }

  confirmResetPassword(id: number): void {
    this.pendingResetId = id;
    this.resetSuccessMessage = null;
    this.resetErrorMessage = null;
    const modalRef: NgbModalRef = this.modalService.open(this.resetPasswordModal, {
      centered: true,
    });
    modalRef.result.then(
      (res) => {
        if (res === 'confirm' && this.pendingResetId != null) {
          this.performResetPassword(this.pendingResetId);
        }
      },
      () => {
        this.pendingResetId = null;
      },
    );
  }

  performResetPassword(id: number): void {
    this.adminService.resetUserPassword(id).subscribe({
      next: (res) => {
        this.resetSuccessMessage = res.message || 'Password reset email sent successfully';
        this.pendingResetId = null;
        setTimeout(() => {
          this.resetSuccessMessage = null;
        }, 5000);
      },
      error: (err) => {
        console.error('Failed to reset password', err);
        this.resetErrorMessage = err.error?.message || 'Failed to reset password';
        this.pendingResetId = null;
        setTimeout(() => {
          this.resetErrorMessage = null;
        }, 5000);
      },
    });
  }

  openDeleteReviewModal(reviewId: number, modalTemplate: TemplateRef<any>) {
    this.pendingDeleteReviewId = reviewId;
    this.modalService.open(modalTemplate, { centered: true }).result.then(result => {
      if (result === 'confirm') {
        this.deleteReviewConfirmed();
      }
    }, () => {
      this.pendingDeleteReviewId = null;
    });
  }

  deleteReviewConfirmed() {
    if (this.pendingDeleteReviewId != null) {
      this.adminService.deleteReview(this.pendingDeleteReviewId).subscribe(() => {
        this.reviews = this.reviews.filter(r => r.id !== this.pendingDeleteReviewId);
        this.pendingDeleteReviewId = null;
      });
    }
  }

  getCourseName(courseId: number): string {
    const course = this.courses.find(c => c.id === courseId);
    return course ? course.name || course.title : courseId?.toString() || '-';
  }

  getUniversityName(universityId: number): string {
    const uni = this.universities.find(u => u.id === universityId);
    return uni ? uni.name : universityId?.toString() || '-';
  }
}
