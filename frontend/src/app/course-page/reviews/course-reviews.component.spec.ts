import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CourseReviewsComponent } from './course-reviews.component';
import { ReviewService } from '../../university-page/services/review-service';
import { AuthService } from '../../auth/auth.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { of, throwError, BehaviorSubject } from 'rxjs';
import { Review } from '../../university-page/viewmodels/review';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

describe('CourseReviewsComponent', () => {
  let component: CourseReviewsComponent;
  let fixture: ComponentFixture<CourseReviewsComponent>;
  let reviewService: jasmine.SpyObj<ReviewService>;
  let authService: jasmine.SpyObj<AuthService>;
  let modalService: jasmine.SpyObj<NgbModal>;
  let currentUserSubject: BehaviorSubject<any>;

  const mockReviews: Review[] = [
    {
      id: 1,
      userId: 1,
      userName: 'John Doe',
      courseId: 201,
      rating: 5,
      title: 'Excellent Course',
      description: 'Really enjoyed this course',
      reviewDate: '2025-01-01',
    },
    {
      id: 2,
      userId: 2,
      userName: 'Jane Smith',
      courseId: 201,
      rating: 4,
      title: 'Good Course',
      description: 'Pretty good overall',
      reviewDate: '2025-01-02',
    },
  ];

  beforeEach(async () => {
    const reviewServiceSpy = jasmine.createSpyObj('ReviewService', [
      'getCourseReviews',
      'checkCourseEligibility',
      'addCourseReview',
      'updateCourseReview',
      'deleteCourseReview',
    ]);

    currentUserSubject = new BehaviorSubject<any>(null);
    const authServiceSpy = jasmine.createSpyObj('AuthService', [], {
      currentUser$: currentUserSubject.asObservable(),
    });

    const modalServiceSpy = jasmine.createSpyObj('NgbModal', ['open']);

    await TestBed.configureTestingModule({
      imports: [CourseReviewsComponent, FormsModule, CommonModule],
      providers: [
        { provide: ReviewService, useValue: reviewServiceSpy },
        { provide: AuthService, useValue: authServiceSpy },
        { provide: NgbModal, useValue: modalServiceSpy },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(CourseReviewsComponent);
    component = fixture.componentInstance;
    reviewService = TestBed.inject(ReviewService) as jasmine.SpyObj<ReviewService>;
    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    modalService = TestBed.inject(NgbModal) as jasmine.SpyObj<NgbModal>;

    component.courseId = 201;
    reviewService.getCourseReviews.and.returnValue(of([]));
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {
    it('should load reviews on init', (done) => {
      reviewService.getCourseReviews.and.returnValue(of(mockReviews));

      component.ngOnInit();

      setTimeout(() => {
        expect(reviewService.getCourseReviews).toHaveBeenCalledWith(201);
        expect(component.reviews).toEqual(mockReviews);
        done();
      }, 100);
    });

    it('should set isLoggedIn to true when user is logged in', (done) => {
      reviewService.checkCourseEligibility.and.returnValue(of(true));

      fixture.detectChanges();
      currentUserSubject.next({ id: 1 });

      setTimeout(() => {
        expect(component.isLoggedIn).toBe(true);
        expect(component.currentUserId).toBe(1);
        expect(component.newReview.userId).toBe(1);
        done();
      }, 100);
    });

    it('should set isLoggedIn to false when user is not logged in', (done) => {
      fixture.detectChanges();
      currentUserSubject.next(null);

      setTimeout(() => {
        expect(component.isLoggedIn).toBe(false);
        expect(component.currentUserId).toBeNull();
        done();
      }, 100);
    });

    it('should check eligibility when user is logged in', (done) => {
      reviewService.checkCourseEligibility.and.returnValue(of(true));

      fixture.detectChanges();
      currentUserSubject.next({ id: 1 });

      setTimeout(() => {
        expect(reviewService.checkCourseEligibility).toHaveBeenCalledWith(201);
        expect(component.isEligible).toBe(true);
        done();
      }, 100);
    });

    it('should set isEligible to false when not eligible', (done) => {
      reviewService.checkCourseEligibility.and.returnValue(of(false));

      fixture.detectChanges();
      currentUserSubject.next({ id: 1 });

      setTimeout(() => {
        expect(component.isEligible).toBe(false);
        done();
      }, 100);
    });
  });

  describe('ngOnDestroy', () => {
    it('should unsubscribe from user subscription', () => {
      component.ngOnInit();
      const unsubscribeSpy = spyOn(component['userSubscription']!, 'unsubscribe');

      component.ngOnDestroy();

      expect(unsubscribeSpy).toHaveBeenCalled();
    });
  });

  describe('loadReviews', () => {
    it('should load reviews for the course', (done) => {
      reviewService.getCourseReviews.and.returnValue(of(mockReviews));

      component.loadReviews();

      setTimeout(() => {
        expect(reviewService.getCourseReviews).toHaveBeenCalledWith(201);
        expect(component.reviews).toEqual(mockReviews);
        done();
      }, 100);
    });

    it('should not load reviews if courseId is not set', () => {
      component.courseId = undefined as any;

      component.loadReviews();

      expect(reviewService.getCourseReviews).not.toHaveBeenCalled();
    });
  });

  describe('checkEligibility', () => {
    it('should check eligibility for the course', (done) => {
      reviewService.checkCourseEligibility.and.returnValue(of(true));

      component.checkEligibility(1);

      setTimeout(() => {
        expect(reviewService.checkCourseEligibility).toHaveBeenCalledWith(201);
        expect(component.isEligible).toBe(true);
        done();
      }, 100);
    });

    it('should not check eligibility if courseId is not set', () => {
      component.courseId = undefined as any;

      component.checkEligibility(1);

      expect(reviewService.checkCourseEligibility).not.toHaveBeenCalled();
    });
  });

  describe('submitReview', () => {
    beforeEach(() => {
      component.newReview = {
        userId: 1,
        universityId: 0,
        courseId: 0,
        rating: 5,
        title: 'Test Review',
        description: 'Test description',
      };
    });

    it('should submit a new review successfully', (done) => {
      const savedReview: Review = {
        id: 3,
        ...component.newReview,
        courseId: 201,
      };
      reviewService.addCourseReview.and.returnValue(of(savedReview));

      component.submitReview();

      setTimeout(() => {
        expect(reviewService.addCourseReview).toHaveBeenCalled();
        expect(component.reviews[0]).toEqual(savedReview);
        expect(component.newReview.title).toBe('');
        expect(component.newReview.description).toBe('');
        expect(component.newReview.rating).toBe(5);
        done();
      }, 100);
    });

    it('should handle error when submitting review', () => {
      spyOn(window, 'alert');
      const error = { error: { message: 'Failed to submit' } };
      reviewService.addCourseReview.and.returnValue(throwError(() => error));

      component.submitReview();

      expect(window.alert).toHaveBeenCalledWith('Failed to post review: Failed to submit');
    });

    it('should handle error with default message when no error message provided', () => {
      spyOn(window, 'alert');
      reviewService.addCourseReview.and.returnValue(throwError(() => ({})));

      component.submitReview();

      expect(window.alert).toHaveBeenCalledWith(
        'Failed to post review: An unexpected error occurred.'
      );
    });
  });

  describe('deleteReview', () => {
    beforeEach(() => {
      component.reviews = [...mockReviews];
    });

    it('should delete a review successfully', (done) => {
      reviewService.deleteCourseReview.and.returnValue(of({}));

      component.deleteReview(1);

      setTimeout(() => {
        expect(reviewService.deleteCourseReview).toHaveBeenCalledWith(1);
        expect(component.reviews.length).toBe(1);
        expect(component.reviews.find((r) => r.id === 1)).toBeUndefined();
        done();
      }, 100);
    });

    it('should handle error when deleting review', () => {
      spyOn(window, 'alert');
      const error = { error: { message: 'Failed to delete' } };
      reviewService.deleteCourseReview.and.returnValue(throwError(() => error));

      component.deleteReview(1);

      expect(window.alert).toHaveBeenCalledWith('Failed to delete review: Failed to delete');
    });
  });

  describe('openDeleteModal', () => {
    it('should open modal and delete review on confirmation', (done) => {
      const mockModal = {
        result: Promise.resolve('delete'),
      };
      modalService.open.and.returnValue(mockModal as any);
      reviewService.deleteCourseReview.and.returnValue(of({}));
      component.reviews = [...mockReviews];

      component.openDeleteModal({} as any, 1);

      mockModal.result.then(() => {
        setTimeout(() => {
          expect(modalService.open).toHaveBeenCalled();
          expect(reviewService.deleteCourseReview).toHaveBeenCalledWith(1);
          done();
        }, 100);
      });
    });

    it('should not delete review when modal is dismissed', (done) => {
      const mockModal = {
        result: Promise.reject('cancel'),
      };
      modalService.open.and.returnValue(mockModal as any);

      component.openDeleteModal({} as any, 1);

      mockModal.result.catch(() => {
        expect(reviewService.deleteCourseReview).not.toHaveBeenCalled();
        done();
      });
    });

    it('should not open modal if reviewId is undefined', () => {
      component.openDeleteModal({} as any, undefined);

      expect(modalService.open).not.toHaveBeenCalled();
    });
  });

  describe('openEditModal', () => {
    it('should open modal and save edited review on confirmation', (done) => {
      const reviewToEdit = { ...mockReviews[0] };
      const mockModal = {
        result: Promise.resolve('save'),
      };
      modalService.open.and.returnValue(mockModal as any);
      reviewService.updateCourseReview.and.returnValue(of(reviewToEdit));
      component.reviews = [...mockReviews];

      component.openEditModal({} as any, reviewToEdit);

      expect(component.editingReview).toEqual(reviewToEdit);

      mockModal.result.then(() => {
        setTimeout(() => {
          expect(modalService.open).toHaveBeenCalled();
          expect(reviewService.updateCourseReview).toHaveBeenCalled();
          done();
        }, 100);
      });
    });

    it('should not save review when modal is dismissed', (done) => {
      const reviewToEdit = { ...mockReviews[0] };
      const mockModal = {
        result: Promise.reject('cancel'),
      };
      modalService.open.and.returnValue(mockModal as any);

      component.openEditModal({} as any, reviewToEdit);

      mockModal.result.catch(() => {
        expect(reviewService.updateCourseReview).not.toHaveBeenCalled();
        done();
      });
    });
  });

  describe('saveEditedReview', () => {
    beforeEach(() => {
      component.reviews = [...mockReviews];
      component.editingReview = { ...mockReviews[0], title: 'Updated Title' };
    });

    it('should save edited review successfully', (done) => {
      const updatedReview = { ...component.editingReview };
      reviewService.updateCourseReview.and.returnValue(of(updatedReview));

      component.saveEditedReview();

      setTimeout(() => {
        expect(reviewService.updateCourseReview).toHaveBeenCalledWith(1, component.editingReview);
        expect(component.reviews[0].title).toBe('Updated Title');
        done();
      }, 100);
    });

    it('should handle error when updating review', () => {
      spyOn(window, 'alert');
      const error = { error: { message: 'Failed to update' } };
      reviewService.updateCourseReview.and.returnValue(throwError(() => error));

      component.saveEditedReview();

      expect(window.alert).toHaveBeenCalledWith('Failed to update review: Failed to update');
    });

    it('should not update if editingReview has no id', () => {
      component.editingReview = { ...component.editingReview, id: undefined };

      component.saveEditedReview();

      expect(reviewService.updateCourseReview).not.toHaveBeenCalled();
    });
  });

  describe('Component initialization', () => {
    it('should initialize with default values', () => {
      expect(component.reviews).toEqual([]);
      expect(component.isLoggedIn).toBe(false);
      expect(component.isEligible).toBe(false);
      expect(component.currentUserId).toBeNull();
      expect(component.newReview.rating).toBe(5);
    });
  });
});
