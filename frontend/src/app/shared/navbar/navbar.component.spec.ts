import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NavbarComponent } from './navbar.component';
import { AuthService } from '../../auth/auth.service';
import { ActivatedRoute, Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs';

describe('NavbarComponent', () => {
  let component: NavbarComponent;
  let fixture: ComponentFixture<NavbarComponent>;
  let authService: jasmine.SpyObj<AuthService>;
  let router: any;
  let currentUserSubject: BehaviorSubject<any>;

  beforeEach(async () => {
    currentUserSubject = new BehaviorSubject<any>(null);
    const authServiceSpy = jasmine.createSpyObj('AuthService', ['logout'], {
      currentUser$: currentUserSubject.asObservable(),
    });

    const activatedRouteStub = {
      snapshot: {
        paramMap: {
          get: (_: string) => null,
        },
      },
      root: {},
    };

    await TestBed.configureTestingModule({
      imports: [NavbarComponent, (await import('@angular/router/testing')).RouterTestingModule.withRoutes([])],
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        { provide: ActivatedRoute, useValue: activatedRouteStub },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(NavbarComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    router = TestBed.inject(Router);
    spyOn(router, 'navigate');
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {
    it('should set userId when user is logged in', (done) => {
      fixture.detectChanges();
      currentUserSubject.next({ id: 123 });

      setTimeout(() => {
        expect((component as any).userId).toBe(123);
        done();
      }, 100);
    });

    it('should set userId to null when user is not logged in', (done) => {
      fixture.detectChanges();
      currentUserSubject.next(null);

      setTimeout(() => {
        expect((component as any).userId).toBeNull();
        done();
      }, 100);
    });

    it('should update userId when user changes', (done) => {
      fixture.detectChanges();
      currentUserSubject.next({ id: 123 });

      setTimeout(() => {
        expect((component as any).userId).toBe(123);

        currentUserSubject.next({ id: 456 });

        setTimeout(() => {
          expect((component as any).userId).toBe(456);
          done();
        }, 100);
      }, 100);
    });
  });

  describe('logout', () => {
    it('should call authService.logout', () => {
      component.logout();

      expect(authService.logout).toHaveBeenCalled();
    });
  });

  describe('ngOnDestroy', () => {
    it('should unsubscribe from userSubscription', () => {
      fixture.detectChanges();
      const unsubscribeSpy = spyOn(component['userSubscription']!, 'unsubscribe');

      component.ngOnDestroy();

      expect(unsubscribeSpy).toHaveBeenCalled();
    });

    it('should handle null userSubscription', () => {
      component['userSubscription'] = null;

      expect(() => component.ngOnDestroy()).not.toThrow();
    });
  });

  describe('goToProfile', () => {
    it('should navigate to profile page when user is logged in', () => {
      (component as any).userId = 123;

      component.goToProfile();

      expect(router.navigate).toHaveBeenCalledWith(['/profile', 123]);
    });

    it('should navigate to login page when user is not logged in', () => {
      (component as any).userId = null;

      component.goToProfile();

      expect(router.navigate).toHaveBeenCalledWith(['/login']);
    });
  });

  describe('component properties', () => {
    it('should initialize links array', () => {
      expect(component.links).toBeDefined();
      expect(component.links.length).toBe(3);
      expect(component.links[0]).toEqual({ label: 'Home', path: '/home' });
      expect(component.links[1]).toEqual({ label: 'Explore', path: '/explore' });
      expect(component.links[2]).toEqual({ label: 'About Us', path: '/about' });
    });

    it('should initialize userId as null', () => {
      expect((component as any).userId).toBeNull();
    });
  });
});
