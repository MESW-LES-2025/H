import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { AdminDashboardComponent } from './admin-dashboard.component';
import { AdminService, Analytics } from './admin.service';
import { of, throwError } from 'rxjs';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { AuthService } from '../auth/auth.service';

describe('AdminDashboardComponent', () => {
  let component: AdminDashboardComponent;
  let fixture: ComponentFixture<AdminDashboardComponent>;
  let adminServiceSpy: jasmine.SpyObj<AdminService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let modalStub: any;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    const spy = jasmine.createSpyObj('AdminService', [
      'deleteUser',
      'getAll',
      'getAnalytics',
      'resetUserPassword',
      'deleteReview',
      'getUniversities',
      'createUniversity',
      'updateUniversity',
      'deleteUniversity',
    ]);
    const authSpy = jasmine.createSpyObj('AuthService', [
      'isAdmin',
      'getCurrentUserId',
    ]);
    modalStub = jasmine.createSpyObj('NgbModal', ['open']);
    modalStub.open.and.returnValue({ result: Promise.resolve('confirm') });

    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        AdminDashboardComponent,
        HttpClientTestingModule,
      ],
      providers: [
        { provide: AdminService, useValue: spy },
        { provide: AuthService, useValue: authSpy },
        { provide: NgbModal, useValue: modalStub },
      ],
    }).compileComponents();

    adminServiceSpy = TestBed.inject(
      AdminService,
    ) as jasmine.SpyObj<AdminService>;
    authServiceSpy = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    httpMock = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(AdminDashboardComponent);
    component = fixture.componentInstance;
    // do not call detectChanges here — tests will call lifecycle methods explicitly when needed
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('ngOnInit should read userId from localStorage and call loadAll when role is ADMIN', (done) => {
    // prepare
    spyOn(component, 'loadAll').and.callThrough();
    authServiceSpy.isAdmin.and.returnValue(true);
    authServiceSpy.getCurrentUserId.and.returnValue(42);
    adminServiceSpy.getAll.and.returnValue(
      of({ users: [], universities: [], reviews: [], courses: [] }),
    );

    component.ngOnInit();

    expect(component.currentUserId).toBe(42);
    expect(component.loadAll).toHaveBeenCalled();
    done();
  });

  it('ngOnInit should redirect non-admin users to root', () => {
    authServiceSpy.isAdmin.and.returnValue(false);
    const router = TestBed.inject(Router);
    const navSpy = spyOn(router, 'navigate').and.returnValue(
      Promise.resolve(true),
    );

    component.ngOnInit();

    expect(navSpy).toHaveBeenCalledWith(['/']);
  });

  it('loadAll should populate arrays on success', (done) => {
    const payload = {
      users: [{ id: 1 }],
      universities: [{ id: 10 }],
      courses: [{ id: 100 }],
    } as any;
    adminServiceSpy.getAll.and.returnValue(of(payload));

    component.loadAll();

    setTimeout(() => {
      expect(component.users.length).toBe(1);
      expect(component.universities.length).toBe(1);
      expect(component.courses.length).toBe(1);
      expect(component.loading).toBeFalse();
      done();
    }, 0);
  });

  it('loadAll should handle missing or empty fields and default to empty arrays', (done) => {
    // simulate backend returning empty/missing fields
    adminServiceSpy.getAll.and.returnValue(of({} as any));

    component.loadAll();

    setTimeout(() => {
      expect(component.users).toEqual([]);
      expect(component.universities).toEqual([]);
      expect(component.courses).toEqual([]);
      expect(component.loading).toBeFalse();
      done();
    }, 0);
  });

  it('loadAll should set error on failure', (done) => {
    adminServiceSpy.getAll.and.returnValue(throwError(() => new Error('boom')));

    component.loadAll();

    setTimeout(() => {
      expect(component.error).toBe('Failed to load admin data');
      expect(component.loading).toBeFalse();
      done();
    }, 0);
  });

  it('setActiveTab should change activeTab', () => {
    component.setActiveTab('courses');
    expect(component.activeTab).toBe('courses');
  });

  it('setActiveTab to analytics should load analytics if not loaded', () => {
    spyOn(component, 'loadAnalytics');
    component.analytics = null;

    component.setActiveTab('analytics');

    expect(component.activeTab).toBe('analytics');
    expect(component.loadAnalytics).toHaveBeenCalled();
  });

  it('setActiveTab to analytics should NOT load analytics if already loaded', () => {
    spyOn(component, 'loadAnalytics');
    component.analytics = { totalUsers: 100 } as Analytics;

    component.setActiveTab('analytics');

    expect(component.activeTab).toBe('analytics');
    expect(component.loadAnalytics).not.toHaveBeenCalled();
  });

  it('loadAnalytics should populate analytics on success', (done) => {
    const mockAnalytics: Analytics = {
      totalUsers: 100,
      totalCourses: 50,
      totalUniversities: 20,
      totalCourseReviews: 200,
      totalUniversityReviews: 150,
      totalScholarships: 30,
      popularCourses: [],
      popularUniversities: [],
    };
    adminServiceSpy.getAnalytics.and.returnValue(of(mockAnalytics));

    component.loadAnalytics();

    setTimeout(() => {
      expect(component.analytics).toEqual(mockAnalytics);
      expect(component.analytics?.totalUsers).toBe(100);
      done();
    }, 0);
  });

  it('loadAnalytics should handle error gracefully', (done) => {
    adminServiceSpy.getAnalytics.and.returnValue(throwError(() => new Error('fail')));
    spyOn(console, 'error');

    component.loadAnalytics();

    setTimeout(() => {
      expect(console.error).toHaveBeenCalled();
      done();
    }, 0);
  });

  it('confirmDelete should open modal and perform delete on confirm', (done) => {
    modalStub.open.and.returnValue({ result: Promise.resolve('confirm') });
    adminServiceSpy.deleteUser.and.returnValue(of(void 0));
    component.users = [{ id: 5 } as any];

    component.confirmDelete(5);

    // wait for microtasks
    setTimeout(() => {
      expect(modalStub.open).toHaveBeenCalled();
      expect(adminServiceSpy.deleteUser).toHaveBeenCalledWith(5);
      expect(component.users.length).toBe(0);
      done();
    }, 0);
  });

  it('confirmDelete should clear pendingDeleteId on dismiss', (done) => {
    modalStub.open.and.returnValue({ result: Promise.reject('dismiss') });
    component.pendingDeleteId = null;

    component.confirmDelete(9);

    setTimeout(() => {
      expect(component.pendingDeleteId).toBeNull();
      done();
    }, 0);
  });

  it('performDelete should call adminService.deleteUser and remove user from list', (done) => {
    component.users = [
      { id: 1, name: 'A' } as any,
      { id: 2, name: 'B' } as any,
    ];
    adminServiceSpy.deleteUser.and.returnValue(of(void 0));

    component.performDelete(2);

    setTimeout(() => {
      expect(adminServiceSpy.deleteUser).toHaveBeenCalledWith(2);
      expect(component.users.find((u) => u.id === 2)).toBeUndefined();
      done();
    }, 0);
  });

  it('performDelete should alert on error', (done) => {
    spyOn(window, 'alert');
    component.users = [{ id: 1, name: 'A' } as any];
    adminServiceSpy.deleteUser.and.returnValue(
      throwError(() => new Error('fail')),
    );

    component.performDelete(1);

    setTimeout(() => {
      expect(window.alert).toHaveBeenCalledWith('Failed to delete user');
      done();
    }, 0);
  });

  describe('resetPassword', () => {
    it('confirmResetPassword should open modal and call performResetPassword on confirm', (done) => {
      modalStub.open.and.returnValue({ result: Promise.resolve('confirm') });
      adminServiceSpy.resetUserPassword.and.returnValue(of({ message: 'Email sent' }));

      component.confirmResetPassword(123);

      setTimeout(() => {
        expect(modalStub.open).toHaveBeenCalled();
        expect(adminServiceSpy.resetUserPassword).toHaveBeenCalledWith(123);
        done();
      }, 0);
    });

    it('performResetPassword should set success message', fakeAsync(() => {
      adminServiceSpy.resetUserPassword.and.returnValue(of({ message: 'Email sent' }));

      component.performResetPassword(123);
      tick();

      expect(component.resetSuccessMessage).toBe('Email sent');
      expect(component.pendingResetId).toBeNull();

      tick(5000); // Clear message timeout
      expect(component.resetSuccessMessage).toBeNull();
    }));

    it('performResetPassword should set error message on failure', fakeAsync(() => {
      adminServiceSpy.resetUserPassword.and.returnValue(
        throwError(() => ({ error: { message: 'Failed' } }))
      );
      spyOn(console, 'error');

      component.performResetPassword(123);
      tick();

      expect(component.resetErrorMessage).toBe('Failed');
      expect(component.pendingResetId).toBeNull();

      tick(5000); // Clear message timeout
      expect(component.resetErrorMessage).toBeNull();
    }));

    it('performResetPassword should use default error message', fakeAsync(() => {
      adminServiceSpy.resetUserPassword.and.returnValue(
        throwError(() => ({}))
      );
      spyOn(console, 'error');

      component.performResetPassword(123);
      tick();

      expect(component.resetErrorMessage).toBe('Failed to reset password');
      tick(5000);
    }));
  });

  describe('reviews', () => {
    it('deleteReviewConfirmed should delete review and remove from list', (done) => {
      component.reviews = [
        { id: 1 } as any,
        { id: 2 } as any,
      ];
      component.pendingDeleteReviewId = 1;
      adminServiceSpy.deleteReview.and.returnValue(of(void 0));

      component.deleteReviewConfirmed();

      setTimeout(() => {
        expect(adminServiceSpy.deleteReview).toHaveBeenCalledWith(1);
        expect(component.reviews.length).toBe(1);
        expect(component.reviews.find(r => r.id === 1)).toBeUndefined();
        expect(component.pendingDeleteReviewId).toBeNull();
        done();
      }, 0);
    });

    it('deleteReviewConfirmed should do nothing if pendingDeleteReviewId is null', () => {
      component.pendingDeleteReviewId = null;

      component.deleteReviewConfirmed();

      expect(adminServiceSpy.deleteReview).not.toHaveBeenCalled();
    });
  });

  describe('helper methods', () => {
    it('getCourseName should return course name if found', () => {
      component.courses = [
        { id: 1, name: 'Course A' } as any,
        { id: 2, name: 'Course B' } as any,
      ];

      expect(component.getCourseName(1)).toBe('Course A');
    });

    it('getCourseName should return id as string if course not found', () => {
      component.courses = [];

      expect(component.getCourseName(123)).toBe('123');
    });

    it('getUniversityName should return university name if found', () => {
      component.universities = [
        { id: 1, name: 'University A' } as any,
        { id: 2, name: 'University B' } as any,
      ];

      expect(component.getUniversityName(2)).toBe('University B');
    });

    it('getUniversityName should return id as string if university not found', () => {
      component.universities = [];

      expect(component.getUniversityName(456)).toBe('456');
    });
  });

  describe('university operations', () => {
    it('openCreateUniversity should set editing to false and open modal', () => {
      component.editing = true;
      component.selectedUniversityId = 123;

      component.openCreateUniversity();

      expect(component.editing).toBeFalse();
      expect(component.selectedUniversityId).toBeNull();
      expect(component.uniForm.name).toBe('');
      expect(modalStub.open).toHaveBeenCalled();
    });

    it('openEditUniversity should set editing to true and populate form', () => {
      const university = {
        id: 1,
        name: 'Test Uni',
        description: 'Desc',
        contactInfo: 'info@uni.edu',
        website: 'https://uni.edu',
        address: '123 Main St',
        logo: 'logo.png',
        location: { id: 5 },
      };

      component.openEditUniversity(university);

      expect(component.editing).toBeTrue();
      expect(component.selectedUniversityId).toBe(1);
      expect(component.uniForm.name).toBe('Test Uni');
      expect(component.uniForm.location).toEqual({ id: 5 });
      expect(modalStub.open).toHaveBeenCalled();
    });

    it('saveUniversity should not save if name is empty', () => {
      component.uniForm = { name: '', description: '' } as any;
      const mockModalRef = { close: jasmine.createSpy('close') } as any;

      component.saveUniversity(mockModalRef);

      expect(adminServiceSpy.createUniversity).not.toHaveBeenCalled();
      expect(adminServiceSpy.updateUniversity).not.toHaveBeenCalled();
    });

    it('saveUniversity should create new university', (done) => {
      component.editing = false;
      component.uniForm = { name: 'New Uni', description: 'Desc' } as any;
      const mockModalRef = { close: jasmine.createSpy('close') } as any;
      adminServiceSpy.createUniversity.and.returnValue(of({ id: 1, name: 'New Uni' } as any));
      adminServiceSpy.getUniversities.and.returnValue(of([]));

      component.saveUniversity(mockModalRef);

      setTimeout(() => {
        expect(adminServiceSpy.createUniversity).toHaveBeenCalled();
        expect(mockModalRef.close).toHaveBeenCalled();
        done();
      }, 0);
    });

    it('saveUniversity should update existing university', (done) => {
      component.editing = true;
      component.selectedUniversityId = 1;
      component.uniForm = { name: 'Updated Uni', description: 'Updated Desc' } as any;
      const mockModalRef = { close: jasmine.createSpy('close') } as any;
      adminServiceSpy.updateUniversity.and.returnValue(of({ id: 1, name: 'Updated Uni' } as any));
      adminServiceSpy.getUniversities.and.returnValue(of([]));

      component.saveUniversity(mockModalRef);

      setTimeout(() => {
        expect(adminServiceSpy.updateUniversity).toHaveBeenCalledWith(1, jasmine.any(Object));
        expect(mockModalRef.close).toHaveBeenCalled();
        done();
      }, 0);
    });

    it('saveUniversity should handle error', (done) => {
      spyOn(window, 'alert');
      component.editing = false;
      component.uniForm = { name: 'Fail Uni' } as any;
      const mockModalRef = { close: jasmine.createSpy('close') } as any;
      adminServiceSpy.createUniversity.and.returnValue(throwError(() => new Error('fail')));

      component.saveUniversity(mockModalRef);

      setTimeout(() => {
        expect(window.alert).toHaveBeenCalledWith('Failed to save university');
        done();
      }, 0);
    });

    it('deleteUniversity should remove university from list', (done) => {
      component.universities = [
        { id: 1 } as any,
        { id: 2 } as any,
      ];
      adminServiceSpy.deleteUniversity.and.returnValue(of(void 0));

      component.deleteUniversity(1);

      setTimeout(() => {
        expect(adminServiceSpy.deleteUniversity).toHaveBeenCalledWith(1);
        expect(component.universities.length).toBe(1);
        expect(component.universities.find(u => u.id === 1)).toBeUndefined();
        done();
      }, 0);
    });

    it('deleteUniversity should handle error with message', (done) => {
      spyOn(window, 'alert');
      component.universities = [{ id: 1 } as any];
      adminServiceSpy.deleteUniversity.and.returnValue(
        throwError(() => ({ error: { message: 'Cannot delete' } }))
      );

      component.deleteUniversity(1);

      setTimeout(() => {
        expect(window.alert).toHaveBeenCalledWith('Cannot delete');
        done();
      }, 0);
    });

    it('deleteUniversity should handle error without message', (done) => {
      spyOn(window, 'alert');
      component.universities = [{ id: 1 } as any];
      adminServiceSpy.deleteUniversity.and.returnValue(
        throwError(() => ({}))
      );

      component.deleteUniversity(1);

      setTimeout(() => {
        expect(window.alert).toHaveBeenCalledWith('Failed to delete university');
        done();
      }, 0);
    });

    it('confirmDeleteUniversity should open modal and delete on confirm', (done) => {
      modalStub.open.and.returnValue({ result: Promise.resolve('confirm') });
      adminServiceSpy.deleteUniversity.and.returnValue(of(void 0));
      component.universities = [{ id: 1 } as any];

      component.confirmDeleteUniversity(1);

      setTimeout(() => {
        expect(component.pendingDeleteId).toBeNull();
        expect(adminServiceSpy.deleteUniversity).toHaveBeenCalledWith(1);
        done();
      }, 0);
    });

    it('confirmDeleteUniversity should clear pendingDeleteId on dismiss', (done) => {
      modalStub.open.and.returnValue({ result: Promise.reject('dismiss') });

      component.confirmDeleteUniversity(5);

      setTimeout(() => {
        expect(component.pendingDeleteId).toBeNull();
        done();
      }, 0);
    });

    it('openEditUniversity should set location to null when location has no id', () => {
      const university = {
        id: 1,
        name: 'Test Uni',
        description: 'Desc',
        location: {}, // no id
      };

      component.openEditUniversity(university);

      expect(component.uniForm.location).toBeNull();
    });

    it('saveUniversity should handle location with id', (done) => {
      component.editing = false;
      component.uniForm = {
        name: 'New Uni',
        description: 'Desc',
        location: { id: 5 }
      } as any;
      const mockModalRef = { close: jasmine.createSpy('close') } as any;
      adminServiceSpy.createUniversity.and.returnValue(of({ id: 1, name: 'New Uni' } as any));
      adminServiceSpy.getUniversities.and.returnValue(of([]));

      component.saveUniversity(mockModalRef);

      setTimeout(() => {
        const callArg = adminServiceSpy.createUniversity.calls.mostRecent().args[0];
        expect(callArg.location).toEqual({ id: 5 });
        done();
      }, 0);
    });

    it('saveUniversity should set location to null when no id', (done) => {
      component.editing = false;
      component.uniForm = {
        name: 'New Uni',
        description: 'Desc',
        location: {} // no id
      } as any;
      const mockModalRef = { close: jasmine.createSpy('close') } as any;
      adminServiceSpy.createUniversity.and.returnValue(of({ id: 1, name: 'New Uni' } as any));
      adminServiceSpy.getUniversities.and.returnValue(of([]));

      component.saveUniversity(mockModalRef);

      setTimeout(() => {
        const callArg = adminServiceSpy.createUniversity.calls.mostRecent().args[0];
        expect(callArg.location).toBeNull();
        done();
      }, 0);
    });
  });

  describe('confirmResetPassword dismiss', () => {
    it('should clear pendingResetId on modal dismiss', (done) => {
      modalStub.open.and.returnValue({ result: Promise.reject('dismiss') });

      component.confirmResetPassword(123);

      setTimeout(() => {
        expect(component.pendingResetId).toBeNull();
        done();
      }, 0);
    });
  });

  describe('openDeleteReviewModal', () => {
    it('should open modal and delete review on confirm', (done) => {
      modalStub.open.and.returnValue({ result: Promise.resolve('confirm') });
      adminServiceSpy.deleteReview.and.returnValue(of(void 0));
      component.reviews = [{ id: 1 } as any, { id: 2 } as any];

      component.openDeleteReviewModal(1, {} as any);

      setTimeout(() => {
        expect(component.pendingDeleteReviewId).toBeNull();
        expect(adminServiceSpy.deleteReview).toHaveBeenCalledWith(1);
        expect(component.reviews.length).toBe(1);
        done();
      }, 0);
    });

    it('should clear pendingDeleteReviewId on modal dismiss', (done) => {
      modalStub.open.and.returnValue({ result: Promise.reject('dismiss') });

      component.openDeleteReviewModal(5, {} as any);

      setTimeout(() => {
        expect(component.pendingDeleteReviewId).toBeNull();
        done();
      }, 0);
    });
  });

  describe('performResetPassword default message', () => {
    it('should use default success message when response has no message', fakeAsync(() => {
      adminServiceSpy.resetUserPassword.and.returnValue(of({} as any));

      component.performResetPassword(123);
      tick();

      expect(component.resetSuccessMessage).toBe('Password reset email sent successfully');
      tick(5000);
    }));
  });

  describe('helper methods edge cases', () => {
    it('getCourseName should return course title if name is missing', () => {
      component.courses = [
        { id: 1, title: 'Course Title' } as any,
      ];

      expect(component.getCourseName(1)).toBe('Course Title');
    });

    it('getCourseName should return dash for undefined courseId', () => {
      component.courses = [];

      expect(component.getCourseName(undefined as any)).toBe('-');
    });

    it('getUniversityName should return dash for undefined universityId', () => {
      component.universities = [];

      expect(component.getUniversityName(undefined as any)).toBe('-');
    });
  });

  describe('loadUniversitiesOnly', () => {
    it('should handle error when reloading universities', (done) => {
      adminServiceSpy.getUniversities.and.returnValue(throwError(() => new Error('fail')));
      spyOn(console, 'error');

      // Access private method
      (component as any).loadUniversitiesOnly();

      setTimeout(() => {
        expect(console.error).toHaveBeenCalled();
        done();
      }, 0);
    });

    it('should default to empty array when response is null', (done) => {
      adminServiceSpy.getUniversities.and.returnValue(of(null as any));

      (component as any).loadUniversitiesOnly();

      setTimeout(() => {
        expect(component.universities).toEqual([]);
        done();
      }, 0);
    });
  });
});

