import { TestBed } from '@angular/core/testing';
import { App } from './app';
import { provideHttpClient } from '@angular/common/http';
import { Router, NavigationEnd } from '@angular/router';
import { Title } from '@angular/platform-browser';
import { DataService } from './shared/services/data-service';
import { Subject } from 'rxjs';
import { provideRouter } from '@angular/router';

describe('App', () => {
  let component: App;
  let fixture: any;
  let router: Router;
  let titleService: Title;
  let dataService: jasmine.SpyObj<DataService>;
  let routerEventsSubject: Subject<any>;

  beforeEach(async () => {
    const dataServiceSpy = jasmine.createSpyObj('DataService', [
      'loadFilterLists',
    ]);
    routerEventsSubject = new Subject();

    await TestBed.configureTestingModule({
      imports: [App],
      providers: [
        provideHttpClient(),
        provideRouter([]),
        { provide: DataService, useValue: dataServiceSpy },
      ],
    }).compileComponents();

    router = TestBed.inject(Router);
    titleService = TestBed.inject(Title);
    dataService = TestBed.inject(DataService) as jasmine.SpyObj<DataService>;

    // Mock router events BEFORE creating component
    Object.defineProperty(router, 'events', {
      get: () => routerEventsSubject.asObservable(),
      configurable: true,
    });

    fixture = TestBed.createComponent(App);
    component = fixture.componentInstance;
  });

  it('should create the app', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with showNavbar as true', () => {
    expect(component.showNavbar).toBe(true);
  });

  it('should call loadFilterLists on init', () => {
    component.ngOnInit();
    expect(dataService.loadFilterLists).toHaveBeenCalled();
  });

  describe('Navigation handling', () => {
    it('should hide navbar on login route', (done) => {
      const navigationEnd = new NavigationEnd(1, '/login', '/login');

      routerEventsSubject.next(navigationEnd);

      setTimeout(() => {
        expect(component.showNavbar).toBe(false);
        done();
      });
    });

    it('should hide navbar on register route', (done) => {
      const navigationEnd = new NavigationEnd(1, '/register', '/register');

      routerEventsSubject.next(navigationEnd);

      setTimeout(() => {
        expect(component.showNavbar).toBe(false);
        done();
      });
    });

    it('should show navbar on other routes', (done) => {
      const navigationEnd = new NavigationEnd(1, '/home', '/home');

      routerEventsSubject.next(navigationEnd);

      setTimeout(() => {
        expect(component.showNavbar).toBe(true);
        done();
      });
    });

    it('should set title to "Login" on login route', (done) => {
      spyOn(titleService, 'setTitle');

      const navigationEnd = new NavigationEnd(1, '/login', '/login');

      routerEventsSubject.next(navigationEnd);

      setTimeout(() => {
        expect(titleService.setTitle).toHaveBeenCalledWith('Login');
        done();
      });
    });

    it('should set title to "Register" on register route', (done) => {
      spyOn(titleService, 'setTitle');

      const navigationEnd = new NavigationEnd(1, '/register', '/register');

      routerEventsSubject.next(navigationEnd);

      setTimeout(() => {
        expect(titleService.setTitle).toHaveBeenCalledWith('Register');
        done();
      });
    });

    it('should set default title "Lernia" for unmapped routes', (done) => {
      spyOn(titleService, 'setTitle');

      const navigationEnd = new NavigationEnd(1, '/courses', '/courses');

      routerEventsSubject.next(navigationEnd);

      setTimeout(() => {
        expect(titleService.setTitle).toHaveBeenCalledWith('Lernia');
        done();
      });
    });

    it('should use urlAfterRedirects when available', (done) => {
      spyOn(titleService, 'setTitle');

      const navigationEnd = new NavigationEnd(1, '/old-login', '/login');

      routerEventsSubject.next(navigationEnd);

      setTimeout(() => {
        expect(component.showNavbar).toBe(false);
        expect(titleService.setTitle).toHaveBeenCalledWith('Login');
        done();
      });
    });
  });

  describe('Lifecycle hooks', () => {
    it('should complete destroy$ subject on destroy', () => {
      const destroySpy = spyOn(component['destroy$'], 'complete');

      component.ngOnDestroy();

      expect(destroySpy).toHaveBeenCalled();
    });

    it('should emit on destroy$ subject on destroy', () => {
      const destroySpy = spyOn(component['destroy$'], 'next');

      component.ngOnDestroy();

      expect(destroySpy).toHaveBeenCalled();
    });

    it('should unsubscribe from router events on destroy', () => {
      fixture.detectChanges();

      const subscription = component['destroy$'];
      const completeSpy = spyOn(subscription, 'complete').and.callThrough();

      component.ngOnDestroy();

      expect(completeSpy).toHaveBeenCalled();
    });
  });
});
