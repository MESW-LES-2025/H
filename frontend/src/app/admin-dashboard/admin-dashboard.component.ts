import { Component, OnInit, ViewChild, TemplateRef } from '@angular/core';
import { Router } from '@angular/router';
import { AdminService } from './admin.service';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css'],
})
export class AdminDashboardComponent implements OnInit {
  loading = true;
  error: string | null = null;
  currentUserId: number | null = null;
  pendingDeleteId: number | null = null;

  users: any[] = [];
  universities: any[] = [];
  courses: any[] = [];

  activeTab: 'users' | 'universities' | 'courses' = 'users';

  constructor(
    private router: Router,
    private adminService: AdminService,
    private modalService: NgbModal,
  ) {}

  ngOnInit(): void {
    const role = localStorage.getItem('userRole');
    if (role !== 'ADMIN') {
      this.router.navigate(['/']);
      return;
    }
    this.currentUserId = Number(localStorage.getItem('userId'));
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
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load admin data';
        console.error(err);
        this.loading = false;
      },
    });
  }

  setActiveTab(tab: 'users' | 'universities' | 'courses'): void {
    this.activeTab = tab;
  }

  @ViewChild('confirmModal') confirmModal!: TemplateRef<any>;

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
        // dismissed
        this.pendingDeleteId = null;
      },
    );
  }

  // separated for easier testing
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
}
