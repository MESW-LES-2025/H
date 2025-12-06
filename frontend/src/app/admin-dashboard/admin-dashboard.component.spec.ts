import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { AdminDashboardComponent } from './admin-dashboard.component';
import { AdminService } from './admin.service';
import { of, throwError } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
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
    const spy = jasmine.createSpyObj('AdminService', ['deleteUser', 'getAll']);
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
      of({ users: [], universities: [], courses: [] }),
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
});
