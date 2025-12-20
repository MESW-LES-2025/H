import { Component, OnInit, ViewChild, TemplateRef } from '@angular/core';
import { Router } from '@angular/router';
import { AdminService, Analytics, UniversityUpsert } from './admin.service';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { AuthService } from '../auth/auth.service';
import { DatePipe, CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';


@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  standalone: true,
  styleUrls: ['./admin-dashboard.component.css'],
  imports: [DatePipe, CommonModule, FormsModule],
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

  editing: boolean = false;
  uniForm: UniversityUpsert = { name: '', description: '', contactInfo: '', website: '', address: '', logo: '', location: null };
  selectedUniversityId: number | null = null;
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
  @ViewChild('universityFormModal') universityFormModal!: TemplateRef<any>;
  @ViewChild('confirmUniversityDeleteModal') confirmUniversityDeleteModal!: TemplateRef<any>;

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

  // Universities
  openCreateUniversity(): void {
    this.editing = false;
    this.selectedUniversityId = null;
    this.uniForm = { name: '', description: '', contactInfo: '', website: '', address: '', logo: '', location: null };
    this.modalService.open(this.universityFormModal, { centered: true, size: 'lg' });
  }

  openEditUniversity(u: any): void {
    this.editing = true;
    this.selectedUniversityId = u.id;
    this.uniForm = {
      id: u.id,
      name: u.name,
      description: u.description,
      contactInfo: u.contactInfo,
      website: u.website,
      address: u.address,
      logo: u.logo,
      location: u.location?.id ? { id: u.location.id } : null,
    };
    this.modalService.open(this.universityFormModal, { centered: true, size: 'lg' });
  }

  saveUniversity(modalRef: NgbModalRef): void {
    if (!this.uniForm.name || !this.uniForm.name.trim()) {
      return;
    }
    const payload: UniversityUpsert = {
      name: this.uniForm.name.trim(),
      description: this.uniForm.description,
      contactInfo: this.uniForm.contactInfo,
      website: this.uniForm.website,
      address: this.uniForm.address,
      logo: this.uniForm.logo,
      location: this.uniForm.location?.id ? { id: this.uniForm.location.id } : null,
    };

    const req$ = this.editing && this.selectedUniversityId
      ? this.adminService.updateUniversity(this.selectedUniversityId, payload)
      : this.adminService.createUniversity(payload);

    req$.subscribe({
      next: () => {
        modalRef.close();
        this.loadUniversitiesOnly();
      },
      error: (err) => {
        console.error('Failed to save university', err);
        alert('Failed to save university');
      },
    });
  }

  confirmDeleteUniversity(id: number): void {
    this.pendingDeleteId = id;
    const modalRef = this.modalService.open(this.confirmUniversityDeleteModal, { centered: true });
    modalRef.result.then(
      (res) => {
        if (res === 'confirm' && this.pendingDeleteId != null) {
          this.deleteUniversity(this.pendingDeleteId);
        }
      },
      () => { this.pendingDeleteId = null; }
    );
  }

  deleteUniversity(id: number): void {
    this.adminService.deleteUniversity(id).subscribe({
      next: () => {
        this.universities = this.universities.filter((x) => x.id !== id);
        this.pendingDeleteId = null;
      },
      error: (err) => {
        console.error('Failed to delete university', err);
        alert(err?.error?.message || 'Failed to delete university');
        this.pendingDeleteId = null;
      },
    });
  }

  private loadUniversitiesOnly(): void {
    this.adminService.getUniversities().subscribe({
      next: (list) => this.universities = list || [],
      error: (err) => console.error('Failed to reload universities', err),
    });
  }
}
