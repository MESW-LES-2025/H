import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ProfilePage } from './profile-page';
import { ActivatedRoute } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { ProfilePageService } from './services/profile-page-service';
import { of, throwError } from 'rxjs';
import { UserViewmodel, FavoritesResponse } from './viewmodels/user-viewmodel';
import { ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

describe('ProfilePage', () => {
  let component: ProfilePage;
  let fixture: ComponentFixture<ProfilePage>;
  let mockProfileService: jasmine.SpyObj<ProfilePageService>;
  let mockActivatedRoute: any;

  const mockUser: UserViewmodel = {
    id: 1,
    name: 'Test User',
    age: 25,
    gender: 'MALE',
    location: 'Lisbon',
    jobTitle: 'Developer',
    profileImage: '/test-image.jpg',
    academicHistory: [],
    role: 'USER',
  };

  const mockFavorites: FavoritesResponse = {
    universities: [
      {
        id: 1,
        name: 'Oxford University',
        description: 'Test description',
        location: { id: 1, city: 'Oxford', country: 'UK', costOfLiving: 1500 },
      },
    ],
    courses: [
      {
        id: 1,
        name: 'Computer Science',
        courseType: 'Bachelor',
      },
    ],
  };

  beforeEach(async () => {
    mockProfileService = jasmine.createSpyObj('ProfilePageService', [
      'getUserProfile',
      'getOwnProfile',
      'getOwnFavorites',
      'updateProfile',
      'deleteAccount',
      'addFavoriteUniversity',
      'removeFavoriteUniversity',
      'addFavoriteCourse',
      'removeFavoriteCourse',
    ]);

    mockActivatedRoute = {
      snapshot: {
        paramMap: {
          get: jasmine.createSpy('get').and.returnValue('1'),
        },
      },
    };

    await TestBed.configureTestingModule({
      imports: [ProfilePage, ReactiveFormsModule, CommonModule],
      providers: [
        provideHttpClient(),
        provideRouter([]),
        { provide: ProfilePageService, useValue: mockProfileService },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
      ],
    }).compileComponents();

    mockProfileService.getUserProfile.and.returnValue(of(mockUser));
    mockProfileService.getOwnProfile.and.returnValue(of(mockUser));
    mockProfileService.getOwnFavorites.and.returnValue(of(mockFavorites));

    // Clear localStorage before each test
    localStorage.clear();

    fixture = TestBed.createComponent(ProfilePage);
    component = fixture.componentInstance;
  });

  afterEach(() => {
    localStorage.clear();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {
    it('should load user profile by id when id param exists', () => {
      fixture.detectChanges();

      expect(mockProfileService.getUserProfile).toHaveBeenCalledWith(1);
      expect(component['user']).toEqual(mockUser);
    });

    it('should load own profile when no id param exists', () => {
      mockActivatedRoute.snapshot.paramMap.get.and.returnValue(null);

      fixture.detectChanges();

      expect(mockProfileService.getOwnProfile).toHaveBeenCalled();
      expect(component['user']).toEqual(mockUser);
    });

    it('should load own profile when id param is NaN', () => {
      mockActivatedRoute.snapshot.paramMap.get.and.returnValue('abc');

      fixture.detectChanges();

      expect(mockProfileService.getOwnProfile).toHaveBeenCalled();
      expect(component['user']).toEqual(mockUser);
    });

    it('should load favorites on init', () => {
      fixture.detectChanges();

      expect(mockProfileService.getOwnFavorites).toHaveBeenCalled();
      expect(component['universities'].length).toBe(1);
      expect(component['courses'].length).toBe(1);
    });

    it('should map universities correctly from favorites', () => {
      fixture.detectChanges();

      const university = component['universities'][0];
      expect(university.id).toBe(1);
      expect(university.name).toBe('Oxford University');
      expect(university.city).toBe('Oxford');
      expect(university.country).toBe('UK');
      expect(university.isFavorite).toBeTrue();
    });

    it('should map courses correctly from favorites', () => {
      fixture.detectChanges();

      const course = component['courses'][0];
      expect(course.id).toBe(1);
      expect(course.name).toBe('Computer Science');
      expect(course.type).toBe('Bachelor');
      expect(course.isFavorite).toBeTrue();
    });

    it('should handle favorites with null values', () => {
      const emptyFavorites: FavoritesResponse = {
        universities: null as any,
        courses: null as any,
      };
      mockProfileService.getOwnFavorites.and.returnValue(of(emptyFavorites));

      fixture.detectChanges();

      expect(component['universities'].length).toBe(0);
      expect(component['courses'].length).toBe(0);
    });

    it('should handle favorites with undefined location', () => {
      const favoritesWithNoLocation: FavoritesResponse = {
        universities: [
          {
            id: 2,
            name: 'Cambridge',
            description: 'Test',
            location: null as any,
          },
        ],
        courses: [],
      };
      mockProfileService.getOwnFavorites.and.returnValue(
        of(favoritesWithNoLocation),
      );

      fixture.detectChanges();

      const university = component['universities'][0];
      expect(university.city).toBe('Unknown');
      expect(university.country).toBe('Unknown');
    });

    it('should handle favorites error', () => {
      spyOn(console, 'error');
      mockProfileService.getOwnFavorites.and.returnValue(
        throwError(() => new Error('Failed to load')),
      );

      fixture.detectChanges();

      expect(console.error).toHaveBeenCalledWith(
        'Error loading favorites',
        jasmine.any(Error),
      );
    });

    it('should initialize form with null values', () => {
      fixture.detectChanges();

      expect(component['editProfileForm']).toBeDefined();
      expect(component['editProfileForm'].get('id')?.value).toBeNull();
      expect(component['editProfileForm'].get('name')?.value).toBeNull();
      expect(component['editProfileForm'].get('age')?.value).toBeNull();
      expect(component['editProfileForm'].get('gender')?.value).toBeNull();
      expect(component['editProfileForm'].get('location')?.value).toBeNull();
      expect(component['editProfileForm'].get('jobTitle')?.value).toBeNull();
    });

    it('should initialize with universities tab active', () => {
      fixture.detectChanges();

      expect(component['activeTab']).toBe('universities');
    });
  });

  describe('Edit Profile Modal', () => {
    beforeEach(() => {
      // Set localStorage to mark user as owner
      localStorage.setItem('userId', String(mockUser.id));
      fixture.detectChanges();
      component['user'] = mockUser;
    });

    afterEach(() => {
      localStorage.clear();
    });

    it('should open edit modal and pre-fill form with user data', () => {
      component['openEditModal']();

      expect(component['showEditModal']).toBeTrue();
      expect(component['editProfileForm'].get('id')?.value).toBe(1);
      expect(component['editProfileForm'].get('name')?.value).toBe('Test User');
      expect(component['editProfileForm'].get('age')?.value).toBe(25);
      expect(component['editProfileForm'].get('gender')?.value).toBe('MALE');
      expect(component['editProfileForm'].get('location')?.value).toBe(
        'Lisbon',
      );
      expect(component['editProfileForm'].get('jobTitle')?.value).toBe(
        'Developer',
      );
    });

    it('should not open modal if user is null', () => {
      component['user'] = null;
      component['openEditModal']();

      expect(component['showEditModal']).toBeFalse();
    });

    it('should not open modal if not owner', () => {
      localStorage.setItem('userId', '999'); // Different user
      component['openEditModal']();

      expect(component['showEditModal']).toBeFalse();
    });

    it('should close edit modal and reset form', () => {
      component['showEditModal'] = true;
      component['editProfileForm'].patchValue({ name: 'Test' });

      component['closeEditModal']();

      expect(component['showEditModal']).toBeFalse();
      expect(component['editProfileForm'].get('name')?.value).toBeNull();
    });

    it('should submit valid form and update user', () => {
      const updatedUser = { ...mockUser, name: 'Updated Name' };
      mockProfileService.updateProfile.and.returnValue(of(updatedUser));

      component['openEditModal']();
      component['editProfileForm'].patchValue({ name: 'Updated Name' });
      component['onSubmitEdit']();

      expect(mockProfileService.updateProfile).toHaveBeenCalled();
      expect(component['user']?.name).toBe('Updated Name');
      expect(component['showEditModal']).toBeFalse();
    });

    it('should pass correct data to update service', () => {
      const updatedUser = { ...mockUser };
      mockProfileService.updateProfile.and.returnValue(of(updatedUser));

      component['openEditModal']();
      component['editProfileForm'].patchValue({
        id: 1,
        name: 'New Name',
        age: 30,
        gender: 'FEMALE',
        location: 'Porto',
        jobTitle: 'Manager',
      });
      component['onSubmitEdit']();

      expect(mockProfileService.updateProfile).toHaveBeenCalledWith({
        id: 1,
        name: 'New Name',
        age: 30,
        gender: 'FEMALE',
        location: 'Porto',
        jobTitle: 'Manager',
      });
    });

    it('should mark form as touched if invalid on submit', () => {
      component['openEditModal']();
      component['editProfileForm'].patchValue({ name: '', gender: '' });

      component['onSubmitEdit']();

      expect(component['editProfileForm'].get('name')?.touched).toBeTrue();
      expect(component['editProfileForm'].get('gender')?.touched).toBeTrue();
      expect(mockProfileService.updateProfile).not.toHaveBeenCalled();
    });

    it('should handle update profile error', () => {
      spyOn(console, 'error');
      spyOn(window, 'alert');
      mockProfileService.updateProfile.and.returnValue(
        throwError(() => new Error('Update failed')),
      );

      component['openEditModal']();
      component['onSubmitEdit']();

      expect(console.error).toHaveBeenCalledWith(
        'Failed to update profile:',
        jasmine.any(Error),
      );
      expect(window.alert).toHaveBeenCalledWith(
        'Failed to update profile. Please try again.',
      );
    });

    it('should keep modal open on update error', () => {
      spyOn(console, 'error');
      spyOn(window, 'alert');
      mockProfileService.updateProfile.and.returnValue(
        throwError(() => new Error('Update failed')),
      );

      component['openEditModal']();
      component['onSubmitEdit']();

      expect(component['showEditModal']).toBeTrue();
    });
  });

  describe('Tab Navigation', () => {
    beforeEach(() => {
      fixture.detectChanges();
    });

    it('should set active tab to universities', () => {
      component['setTab']('universities');
      expect(component['activeTab']).toBe('universities');
    });

    it('should set active tab to courses', () => {
      component['setTab']('courses');
      expect(component['activeTab']).toBe('courses');
    });

    it('should set active tab to countries', () => {
      component['setTab']('countries');
      expect(component['activeTab']).toBe('countries');
    });

    it('should set active tab to other', () => {
      component['setTab']('other');
      expect(component['activeTab']).toBe('other');
    });

    it('should switch between tabs', () => {
      component['setTab']('courses');
      expect(component['activeTab']).toBe('courses');

      component['setTab']('universities');
      expect(component['activeTab']).toBe('universities');

      component['setTab']('countries');
      expect(component['activeTab']).toBe('countries');
    });
  });

  describe('Delete Account', () => {
    beforeEach(() => {
      localStorage.setItem('userId', String(mockUser.id));
      fixture.detectChanges();
      component['user'] = mockUser;
    });

    afterEach(() => {
      localStorage.clear();
    });

    it('should not delete if user is null', () => {
      component['user'] = null;
      component['confirmDelete']();

      expect(mockProfileService.deleteAccount).not.toHaveBeenCalled();
    });

    it('should not delete if not owner', () => {
      localStorage.setItem('userId', '999'); // Different user
      spyOn(window, 'confirm'); // Add this to prevent confirm dialog

      component['confirmDelete']();

      expect(window.confirm).not.toHaveBeenCalled(); // Confirm should not even be called
      expect(mockProfileService.deleteAccount).not.toHaveBeenCalled();
    });

    it('should not delete if user cancels confirmation', () => {
      spyOn(window, 'confirm').and.returnValue(false);

      component['confirmDelete']();

      expect(mockProfileService.deleteAccount).not.toHaveBeenCalled();
    });

    it('should show confirmation dialog with correct message', () => {
      spyOn(window, 'confirm').and.returnValue(false);

      component['confirmDelete']();

      expect(window.confirm).toHaveBeenCalledWith(
        'Are you sure you want to delete your account? This action cannot be undone.',
      );
    });

    it('should not clean up localStorage on delete error', () => {
      spyOn(console, 'error');
      spyOn(window, 'confirm').and.returnValue(true);
      spyOn(window, 'alert');
      spyOn(localStorage, 'removeItem');
      mockProfileService.deleteAccount.and.returnValue(
        throwError(() => new Error('Delete failed')),
      );

      component['confirmDelete']();

      expect(localStorage.removeItem).not.toHaveBeenCalled();
    });
  });

  describe('University Favorites', () => {
    beforeEach(() => {
      fixture.detectChanges();
    });

    it('should remove favorite university', () => {
      mockProfileService.removeFavoriteUniversity.and.returnValue(of(void 0));
      component['universities'] = [
        {
          id: 1,
          name: 'Oxford',
          image: '',
          city: 'Oxford',
          country: 'UK',
          isFavorite: true,
        },
      ];

      component['removeFavoriteUniversity'](1);

      expect(mockProfileService.removeFavoriteUniversity).toHaveBeenCalledWith(
        1,
      );
      expect(component['universities'].length).toBe(0);
    });

    it('should handle remove favorite university error', () => {
      spyOn(console, 'error');
      mockProfileService.removeFavoriteUniversity.and.returnValue(
        throwError(() => new Error('Remove failed')),
      );

      component['removeFavoriteUniversity'](1);

      expect(console.error).toHaveBeenCalledWith(
        'Error removing favorite university',
        jasmine.any(Error),
      );
    });

    it('should not remove other universities when removing one', () => {
      mockProfileService.removeFavoriteUniversity.and.returnValue(of(void 0));
      component['universities'] = [
        {
          id: 1,
          name: 'Oxford',
          image: '',
          city: 'Oxford',
          country: 'UK',
          isFavorite: true,
        },
        {
          id: 2,
          name: 'Cambridge',
          image: '',
          city: 'Cambridge',
          country: 'UK',
          isFavorite: true,
        },
      ];

      component['removeFavoriteUniversity'](1);

      expect(component['universities'].length).toBe(1);
      expect(component['universities'][0].id).toBe(2);
    });

    it('should add favorite university when not favorited', () => {
      mockProfileService.addFavoriteUniversity.and.returnValue(of(void 0));
      const uni = {
        id: 2,
        name: 'Cambridge',
        image: '',
        city: 'Cambridge',
        country: 'UK',
        isFavorite: false,
      };

      component['toggleUniversityFavorite'](uni);

      expect(mockProfileService.addFavoriteUniversity).toHaveBeenCalledWith(2);
      expect(uni.isFavorite).toBeTrue();
    });

    it('should remove favorite university when already favorited via toggle', () => {
      mockProfileService.removeFavoriteUniversity.and.returnValue(of(void 0));
      component['universities'] = [
        {
          id: 1,
          name: 'Oxford',
          image: '',
          city: 'Oxford',
          country: 'UK',
          isFavorite: true,
        },
      ];
      const uni = component['universities'][0];

      component['toggleUniversityFavorite'](uni);

      expect(mockProfileService.removeFavoriteUniversity).toHaveBeenCalledWith(
        1,
      );
      expect(component['universities'].length).toBe(0);
    });

    it('should handle toggle university favorite add error', () => {
      spyOn(console, 'error');
      mockProfileService.addFavoriteUniversity.and.returnValue(
        throwError(() => new Error('Toggle failed')),
      );
      const uni = {
        id: 2,
        name: 'Cambridge',
        image: '',
        city: 'Cambridge',
        country: 'UK',
        isFavorite: false,
      };

      component['toggleUniversityFavorite'](uni);

      expect(console.error).toHaveBeenCalledWith(jasmine.any(Error));
    });

    it('should handle toggle university favorite remove error', () => {
      spyOn(console, 'error');
      mockProfileService.removeFavoriteUniversity.and.returnValue(
        throwError(() => new Error('Remove failed')),
      );
      const uni = {
        id: 1,
        name: 'Oxford',
        image: '',
        city: 'Oxford',
        country: 'UK',
        isFavorite: true,
      };

      component['toggleUniversityFavorite'](uni);

      expect(console.error).toHaveBeenCalledWith(jasmine.any(Error));
    });
  });

  describe('Course Favorites', () => {
    beforeEach(() => {
      fixture.detectChanges();
    });

    it('should add favorite course when not favorited', () => {
      mockProfileService.addFavoriteCourse.and.returnValue(of(void 0));
      const course = {
        id: 2,
        name: 'Math',
        type: 'Bachelor',
        isFavorite: false,
      };

      component['toggleCourseFavorite'](course);

      expect(mockProfileService.addFavoriteCourse).toHaveBeenCalledWith(2);
      expect(course.isFavorite).toBeTrue();
    });

    it('should remove favorite course when already favorited', () => {
      mockProfileService.removeFavoriteCourse.and.returnValue(of(void 0));
      component['courses'] = [
        { id: 1, name: 'CS', type: 'Bachelor', isFavorite: true },
      ];
      const course = component['courses'][0];

      component['toggleCourseFavorite'](course);

      expect(mockProfileService.removeFavoriteCourse).toHaveBeenCalledWith(1);
      expect(component['courses'].length).toBe(0);
    });

    it('should not remove other courses when removing one', () => {
      mockProfileService.removeFavoriteCourse.and.returnValue(of(void 0));
      component['courses'] = [
        { id: 1, name: 'CS', type: 'Bachelor', isFavorite: true },
        { id: 2, name: 'Math', type: 'Master', isFavorite: true },
      ];
      const course = component['courses'][0];

      component['toggleCourseFavorite'](course);

      expect(component['courses'].length).toBe(1);
      expect(component['courses'][0].id).toBe(2);
    });

    it('should handle toggle course favorite error on add', () => {
      spyOn(console, 'error');
      mockProfileService.addFavoriteCourse.and.returnValue(
        throwError(() => new Error('Add failed')),
      );
      const course = {
        id: 2,
        name: 'Math',
        type: 'Bachelor',
        isFavorite: false,
      };

      component['toggleCourseFavorite'](course);

      expect(console.error).toHaveBeenCalledWith(jasmine.any(Error));
    });

    it('should handle toggle course favorite error on remove', () => {
      spyOn(console, 'error');
      mockProfileService.removeFavoriteCourse.and.returnValue(
        throwError(() => new Error('Remove failed')),
      );
      const course = { id: 1, name: 'CS', type: 'Bachelor', isFavorite: true };

      component['toggleCourseFavorite'](course);

      expect(console.error).toHaveBeenCalledWith(jasmine.any(Error));
    });
  });

  describe('Helper Methods', () => {
    it('should track items by id', () => {
      const item = { id: 123, name: 'Test' };
      const result = component['trackById'](0, item);

      expect(result).toBe(123);
    });

    it('should track different items with different ids', () => {
      const item1 = { id: 1, name: 'First' };
      const item2 = { id: 2, name: 'Second' };

      expect(component['trackById'](0, item1)).toBe(1);
      expect(component['trackById'](1, item2)).toBe(2);
    });
  });

  describe('Form Validation', () => {
    beforeEach(() => {
      fixture.detectChanges();
    });

    it('should require name field', () => {
      const nameControl = component['editProfileForm'].get('name');
      nameControl?.setValue('');

      expect(nameControl?.hasError('required')).toBeTrue();
      expect(nameControl?.valid).toBeFalse();
    });

    it('should require gender field', () => {
      const genderControl = component['editProfileForm'].get('gender');
      genderControl?.setValue('');

      expect(genderControl?.hasError('required')).toBeTrue();
      expect(genderControl?.valid).toBeFalse();
    });

    it('should accept valid name', () => {
      const nameControl = component['editProfileForm'].get('name');
      nameControl?.setValue('John Doe');

      expect(nameControl?.valid).toBeTrue();
    });

    it('should accept valid gender', () => {
      const genderControl = component['editProfileForm'].get('gender');
      genderControl?.setValue('MALE');

      expect(genderControl?.valid).toBeTrue();
    });

    it('should allow optional fields to be empty', () => {
      const ageControl = component['editProfileForm'].get('age');
      const locationControl = component['editProfileForm'].get('location');
      const jobTitleControl = component['editProfileForm'].get('jobTitle');

      ageControl?.setValue(null);
      locationControl?.setValue(null);
      jobTitleControl?.setValue(null);

      expect(ageControl?.valid).toBeTrue();
      expect(locationControl?.valid).toBeTrue();
      expect(jobTitleControl?.valid).toBeTrue();
    });

    it('should accept numeric age', () => {
      const ageControl = component['editProfileForm'].get('age');
      ageControl?.setValue(25);

      expect(ageControl?.valid).toBeTrue();
    });

    it('should have valid form when all required fields are filled', () => {
      component['editProfileForm'].patchValue({
        id: 1,
        name: 'Test User',
        gender: 'MALE',
      });

      expect(component['editProfileForm'].valid).toBeTrue();
    });

    it('should have invalid form when name is missing', () => {
      component['editProfileForm'].patchValue({
        id: 1,
        name: '',
        gender: 'MALE',
      });

      expect(component['editProfileForm'].invalid).toBeTrue();
    });

    it('should have invalid form when gender is missing', () => {
      component['editProfileForm'].patchValue({
        id: 1,
        name: 'Test User',
        gender: '',
      });

      expect(component['editProfileForm'].invalid).toBeTrue();
    });

    it('should maintain form validity with optional fields filled', () => {
      component['editProfileForm'].patchValue({
        id: 1,
        name: 'Test User',
        age: 30,
        gender: 'FEMALE',
        location: 'Porto',
        jobTitle: 'Engineer',
      });

      expect(component['editProfileForm'].valid).toBeTrue();
    });
  });

  describe('Component State', () => {
    it('should initialize with showEditModal as false', () => {
      fixture.detectChanges();

      expect(component['showEditModal']).toBeFalse();
    });

    it('should initialize with user as null before data loads', () => {
      // Don't call detectChanges yet
      expect(component['user']).toBeNull();
    });

    it('should initialize with empty universities array when no favorites', () => {
      mockProfileService.getOwnFavorites.and.returnValue(
        of({ universities: [], courses: [] }),
      );

      // Create a fresh component instance
      const newFixture = TestBed.createComponent(ProfilePage);
      const newComponent = newFixture.componentInstance;
      newFixture.detectChanges();

      expect(newComponent['universities']).toEqual([]);
    });

    it('should initialize with empty courses array when no favorites', () => {
      mockProfileService.getOwnFavorites.and.returnValue(
        of({ universities: [], courses: [] }),
      );

      // Create a fresh component instance
      const newFixture = TestBed.createComponent(ProfilePage);
      const newComponent = newFixture.componentInstance;
      newFixture.detectChanges();

      expect(newComponent['courses']).toEqual([]);
    });

    it('should maintain state after tab changes', () => {
      fixture.detectChanges();

      component['setTab']('courses');
      expect(component['activeTab']).toBe('courses');

      component['setTab']('universities');
      expect(component['activeTab']).toBe('universities');
      expect(component['universities']).toBeDefined();
      expect(component['courses']).toBeDefined();
    });

    it('should load universities when favorites exist', () => {
      fixture.detectChanges();

      expect(component['universities'].length).toBeGreaterThan(0);
      expect(component['universities'][0].name).toBe('Oxford University');
    });

    it('should load courses when favorites exist', () => {
      fixture.detectChanges();

      expect(component['courses'].length).toBeGreaterThan(0);
      expect(component['courses'][0].name).toBe('Computer Science');
    });
  });

  describe('isOwner getter', () => {
    beforeEach(() => {
      fixture.detectChanges();
    });

    it('should return true when localStorage userId matches user id', () => {
      localStorage.setItem('userId', '1');
      component['user'] = mockUser;

      expect(component.isOwner).toBeTrue();
    });

    it('should return false when localStorage userId does not match', () => {
      localStorage.setItem('userId', '999');
      component['user'] = mockUser;

      expect(component.isOwner).toBeFalse();
    });

    it('should return false when user is null', () => {
      localStorage.setItem('userId', '1');
      component['user'] = null;

      expect(component.isOwner).toBeFalse();
    });

    it('should return false when userId is not in localStorage', () => {
      localStorage.removeItem('userId');
      component['user'] = mockUser;

      expect(component.isOwner).toBeFalse();
    });
  });
});
