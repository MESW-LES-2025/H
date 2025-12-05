import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { EditProfile } from './edit-profile';
import { ProfilePageService } from '../services/profile-page-service';

describe('EditProfile', () => {
  let component: EditProfile;
  let fixture: ComponentFixture<EditProfile>;
  let mockProfileService: jasmine.SpyObj<ProfilePageService>;
  let mockRouter: jasmine.SpyObj<Router>;
  let mockActivatedRoute: any;

  beforeEach(async () => {
    mockProfileService = jasmine.createSpyObj('ProfilePageService', [
      'updateProfile',
    ]);
    mockRouter = jasmine.createSpyObj('Router', ['navigate']);
    mockActivatedRoute = {
      snapshot: {
        paramMap: {
          get: jasmine.createSpy('get').and.returnValue('123'),
        },
      },
    };

    await TestBed.configureTestingModule({
      imports: [EditProfile, ReactiveFormsModule],
      providers: [
        { provide: ProfilePageService, useValue: mockProfileService },
        { provide: Router, useValue: mockRouter },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(EditProfile);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize component without calling ngOnInit', () => {
    expect(component.userIdAtual).toBeNull();
    expect(component.editProfileForm).toBeUndefined();
  });

  describe('ngOnInit', () => {
    it('should initialize form with user ID from route params', () => {
      component.ngOnInit();

      expect(component.userIdAtual).toBe(123);
      expect(component.editProfileForm).toBeDefined();
      expect(component.editProfileForm.get('id')?.value).toBe(123);
    });

    it('should initialize form with null user ID when route param is missing', () => {
      mockActivatedRoute.snapshot.paramMap.get.and.returnValue(null);
      component.ngOnInit();

      expect(component.userIdAtual).toBeNull();
      expect(component.editProfileForm.get('id')?.value).toBeNull();
    });

    it('should initialize form with all required controls', () => {
      component.ngOnInit();

      expect(component.editProfileForm.get('id')).toBeDefined();
      expect(component.editProfileForm.get('name')).toBeDefined();
      expect(component.editProfileForm.get('age')).toBeDefined();
      expect(component.editProfileForm.get('gender')).toBeDefined();
      expect(component.editProfileForm.get('location')).toBeDefined();
      expect(component.editProfileForm.get('jobTitle')).toBeDefined();
    });

    it('should set required validators on id, name, and gender fields', () => {
      component.ngOnInit();

      const idControl = component.editProfileForm.get('id');
      const nameControl = component.editProfileForm.get('name');
      const genderControl = component.editProfileForm.get('gender');

      idControl?.setValue(null);
      nameControl?.setValue(null);
      genderControl?.setValue(null);

      expect(idControl?.hasError('required')).toBe(true);
      expect(nameControl?.hasError('required')).toBe(true);
      expect(genderControl?.hasError('required')).toBe(true);
    });

    it('should not set required validators on optional fields', () => {
      component.ngOnInit();

      const ageControl = component.editProfileForm.get('age');
      const locationControl = component.editProfileForm.get('location');
      const jobTitleControl = component.editProfileForm.get('jobTitle');

      ageControl?.setValue(null);
      locationControl?.setValue(null);
      jobTitleControl?.setValue(null);

      expect(ageControl?.hasError('required')).toBe(false);
      expect(locationControl?.hasError('required')).toBe(false);
      expect(jobTitleControl?.hasError('required')).toBe(false);
    });

    it('should call ngOnInit multiple times without errors', () => {
      component.ngOnInit();
      const firstFormInstance = component.editProfileForm;

      component.ngOnInit();
      const secondFormInstance = component.editProfileForm;

      expect(firstFormInstance).not.toBe(secondFormInstance);
      expect(component.userIdAtual).toBe(123);
    });
  });

  describe('onSubmit', () => {
    beforeEach(() => {
      component.ngOnInit();
    });

    it('should call updateProfile and navigate on successful form submission', () => {
      mockProfileService.updateProfile.and.returnValue(of({} as any));

      component.editProfileForm.patchValue({
        id: 123,
        name: 'John Doe',
        age: 25,
        gender: 'MALE',
        location: 'New York',
        jobTitle: 'Developer',
      });

      component.onSubmit();

      expect(mockProfileService.updateProfile).toHaveBeenCalledWith({
        id: 123,
        name: 'John Doe',
        age: 25,
        gender: 'MALE',
        location: 'New York',
        jobTitle: 'Developer',
      });
      expect(mockRouter.navigate).toHaveBeenCalledWith(['/profile', 123]);
    });

    it('should not submit if form is invalid', () => {
      component.editProfileForm.patchValue({
        id: 123,
        name: '', // Required field is empty
        gender: 'MALE',
      });

      component.onSubmit();

      expect(mockProfileService.updateProfile).not.toHaveBeenCalled();
      expect(mockRouter.navigate).not.toHaveBeenCalled();
    });

    it('should mark all fields as touched when form is invalid', () => {
      component.editProfileForm.patchValue({
        id: 123,
        name: '', // Required field is empty
        gender: 'MALE',
      });

      spyOn(component.editProfileForm, 'markAllAsTouched');

      component.onSubmit();

      expect(component.editProfileForm.markAllAsTouched).toHaveBeenCalled();
    });

    it('should not submit if userIdAtual is null', () => {
      component.userIdAtual = null;
      component.editProfileForm.patchValue({
        id: null,
        name: 'John Doe',
        gender: 'MALE',
      });

      component.onSubmit();

      expect(mockProfileService.updateProfile).not.toHaveBeenCalled();
      expect(mockRouter.navigate).not.toHaveBeenCalled();
    });

    it('should not submit if form is valid but userIdAtual is null', () => {
      component.userIdAtual = null;
      component.editProfileForm.patchValue({
        id: 123,
        name: 'John Doe',
        gender: 'MALE',
      });

      expect(component.editProfileForm.valid).toBe(true);
      component.onSubmit();

      expect(mockProfileService.updateProfile).not.toHaveBeenCalled();
    });

    it('should handle all form fields correctly including optional ones', () => {
      mockProfileService.updateProfile.and.returnValue(of({} as any));

      component.editProfileForm.patchValue({
        id: 123,
        name: 'John Doe',
        age: null, // Optional field
        gender: 'MALE',
        location: null, // Optional field
        jobTitle: null, // Optional field
      });

      component.onSubmit();

      expect(mockProfileService.updateProfile).toHaveBeenCalled();
      const callArgs =
        mockProfileService.updateProfile.calls.mostRecent().args[0];
      expect(callArgs.age).toBeNull();
      expect(callArgs.location).toBeNull();
      expect(callArgs.jobTitle).toBeNull();
    });

    it('should handle service error gracefully', () => {
      const errorResponse = new Error('Update failed');
      mockProfileService.updateProfile.and.returnValue(
        throwError(() => errorResponse),
      );

      spyOn(console, 'error');

      component.editProfileForm.patchValue({
        id: 123,
        name: 'John Doe',
        gender: 'MALE',
      });

      component.onSubmit();

      expect(mockProfileService.updateProfile).toHaveBeenCalled();
      expect(console.error).toHaveBeenCalledWith(
        'Failed to update profile:',
        errorResponse,
      );
      expect(mockRouter.navigate).not.toHaveBeenCalled();
    });

    it('should validate name field is required', () => {
      component.editProfileForm.patchValue({
        id: 123,
        name: '',
        gender: 'MALE',
      });

      expect(component.editProfileForm.valid).toBe(false);
      expect(component.editProfileForm.get('name')?.hasError('required')).toBe(
        true,
      );
    });

    it('should validate gender field is required', () => {
      component.editProfileForm.patchValue({
        id: 123,
        name: 'John Doe',
        gender: '',
      });

      expect(component.editProfileForm.valid).toBe(false);
      expect(
        component.editProfileForm.get('gender')?.hasError('required'),
      ).toBe(true);
    });

    it('should accept valid age values', () => {
      component.editProfileForm.patchValue({
        id: 123,
        name: 'John Doe',
        age: 30,
        gender: 'MALE',
      });

      expect(component.editProfileForm.get('age')?.valid).toBe(true);
    });

    it('should handle different gender values', () => {
      mockProfileService.updateProfile.and.returnValue(of({} as any));

      const genderValues = ['MALE', 'FEMALE', 'OTHER'];

      genderValues.forEach((gender) => {
        component.editProfileForm.patchValue({
          id: 123,
          name: 'Test User',
          gender: gender,
        });

        component.onSubmit();

        const callArgs =
          mockProfileService.updateProfile.calls.mostRecent().args[0];
        expect(callArgs.gender).toBe(gender);
      });
    });

    it('should handle successful submission with all fields populated', () => {
      mockProfileService.updateProfile.and.returnValue(of({} as any));

      component.editProfileForm.patchValue({
        name: 'Jane Smith',
        age: 35,
        gender: 'FEMALE',
        location: 'Paris',
        jobTitle: 'Manager',
      });

      component.onSubmit();

      expect(mockProfileService.updateProfile).toHaveBeenCalledWith({
        id: 123,
        name: 'Jane Smith',
        age: 35,
        gender: 'FEMALE',
        location: 'Paris',
        jobTitle: 'Manager',
      });
      expect(mockRouter.navigate).toHaveBeenCalledWith(['/profile', 123]);
    });

    it('should not navigate if updateProfile throws synchronous error', () => {
      mockProfileService.updateProfile.and.throwError('Sync error');
      spyOn(console, 'error');

      component.editProfileForm.patchValue({
        name: 'John Doe',
        gender: 'MALE',
      });

      expect(() => component.onSubmit()).toThrow();
      expect(mockRouter.navigate).not.toHaveBeenCalled();
    });

    it('should handle form submission with only required fields', () => {
      mockProfileService.updateProfile.and.returnValue(of({} as any));

      component.editProfileForm.patchValue({
        name: 'Min User',
        gender: 'OTHER',
      });

      component.onSubmit();

      const callArgs =
        mockProfileService.updateProfile.calls.mostRecent().args[0];
      expect(callArgs.id).toBe(123);
      expect(callArgs.name).toBe('Min User');
      expect(callArgs.gender).toBe('OTHER');
      expect(mockRouter.navigate).toHaveBeenCalledWith(['/profile', 123]);
    });
  });

  describe('Form Integration', () => {
    beforeEach(() => {
      component.ngOnInit();
    });

    it('should update form value when controls are changed', () => {
      component.editProfileForm.get('name')?.setValue('Alice');
      component.editProfileForm.get('age')?.setValue(28);
      component.editProfileForm.get('location')?.setValue('London');

      expect(component.editProfileForm.get('name')?.value).toBe('Alice');
      expect(component.editProfileForm.get('age')?.value).toBe(28);
      expect(component.editProfileForm.get('location')?.value).toBe('London');
    });

    it('should maintain form validity state correctly', () => {
      expect(component.editProfileForm.valid).toBe(false);

      component.editProfileForm.patchValue({
        id: 123,
        name: 'Bob',
        gender: 'MALE',
      });

      expect(component.editProfileForm.valid).toBe(true);
    });

    it('should handle form reset', () => {
      component.editProfileForm.patchValue({
        name: 'Test',
        age: 25,
      });

      component.editProfileForm.reset();

      expect(component.editProfileForm.get('name')?.value).toBeNull();
      expect(component.editProfileForm.get('age')?.value).toBeNull();
    });

    it('should update multiple fields simultaneously', () => {
      component.editProfileForm.patchValue({
        id: 999,
        name: 'Multi Update',
        age: 40,
        gender: 'MALE',
        location: 'Tokyo',
        jobTitle: 'Architect',
      });

      expect(component.editProfileForm.value).toEqual({
        id: 999,
        name: 'Multi Update',
        age: 40,
        gender: 'MALE',
        location: 'Tokyo',
        jobTitle: 'Architect',
      });
    });

    it('should handle individual field updates', () => {
      component.editProfileForm.get('id')?.setValue(111);
      expect(component.editProfileForm.get('id')?.value).toBe(111);

      component.editProfileForm.get('jobTitle')?.setValue('Engineer');
      expect(component.editProfileForm.get('jobTitle')?.value).toBe('Engineer');
    });
  });

  describe('Constructor', () => {
    it('should inject dependencies correctly', () => {
      expect(component['fb']).toBeDefined();
      expect(component['router']).toBe(mockRouter);
      expect(component['profilePageService']).toBe(mockProfileService);
      expect(component['route']).toBe(mockActivatedRoute);
    });
  });
});
