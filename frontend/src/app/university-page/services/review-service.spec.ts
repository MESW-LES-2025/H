import { TestBed } from '@angular/core/testing';
import { ReviewService } from './review-service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { environment } from '../../../environments/environment';

describe('ReviewService', () => {
  let service: ReviewService;
  let httpMock: HttpTestingController;
  const base = `${environment.apiUrl}/api/reviews`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ReviewService],
    });

    service = TestBed.inject(ReviewService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('getReviews should GET university reviews', () => {
    const mock = [{ id: 1, text: 'ok' } as any];

    service.getReviews(10).subscribe((res) => expect(res).toEqual(mock));

    const req = httpMock.expectOne(`${base}/university/10`);
    expect(req.request.method).toBe('GET');
    req.flush(mock);
  });

  it('checkEligibility should GET with credentials', () => {
    service.checkEligibility(20).subscribe((res) => expect(res).toBeTrue());

    const req = httpMock.expectOne(`${base}/eligibility/20`);
    expect(req.request.method).toBe('GET');
    expect(req.request.withCredentials).toBeTrue();
    req.flush(true);
  });

  it('addReview should POST review with credentials', () => {
    const review = { id: 0, text: 'a' } as any;
    service.addReview(review).subscribe((res) => expect(res).toEqual(review));

    const req = httpMock.expectOne(base);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(review);
    expect(req.request.withCredentials).toBeTrue();
    req.flush(review);
  });

  it('getCourseReviews should GET course reviews', () => {
    const mock = [{ id: 2, text: 'course' } as any];

    service.getCourseReviews(33).subscribe((res) => expect(res).toEqual(mock));

    const req = httpMock.expectOne(`${base}/course/33`);
    expect(req.request.method).toBe('GET');
    req.flush(mock);
  });

  it('checkCourseEligibility should GET with credentials', () => {
    service.checkCourseEligibility(44).subscribe((res) => expect(res).toBeFalse());

    const req = httpMock.expectOne(`${base}/course/eligibility/44`);
    expect(req.request.method).toBe('GET');
    expect(req.request.withCredentials).toBeTrue();
    req.flush(false);
  });

  it('addCourseReview should POST to /course with credentials', () => {
    const review = { id: 3, text: 'c' } as any;
    service.addCourseReview(review).subscribe((res) => expect(res).toEqual(review));

    const req = httpMock.expectOne(`${base}/course`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(review);
    expect(req.request.withCredentials).toBeTrue();
    req.flush(review);
  });

  it('updateReview should PUT to /:id with credentials', () => {
    const review = { id: 4, text: 'u' } as any;
    service.updateReview(4, review).subscribe((res) => expect(res).toEqual(review));

    const req = httpMock.expectOne(`${base}/4`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(review);
    expect(req.request.withCredentials).toBeTrue();
    req.flush(review);
  });

  it('updateCourseReview should PUT to /course/:id with credentials', () => {
    const review = { id: 5, text: 'uc' } as any;
    service.updateCourseReview(5, review).subscribe((res) => expect(res).toEqual(review));

    const req = httpMock.expectOne(`${base}/course/5`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(review);
    expect(req.request.withCredentials).toBeTrue();
    req.flush(review);
  });

  it('deleteReview should DELETE /:id with credentials', () => {
    service.deleteReview(7).subscribe((res) => expect(res).toBeTruthy());

    const req = httpMock.expectOne(`${base}/7`);
    expect(req.request.method).toBe('DELETE');
    expect(req.request.withCredentials).toBeTrue();
    req.flush({});
  });

  it('deleteCourseReview should DELETE /course/:id with credentials', () => {
    service.deleteCourseReview(8).subscribe((res) => expect(res).toBeTruthy());

    const req = httpMock.expectOne(`${base}/course/8`);
    expect(req.request.method).toBe('DELETE');
    expect(req.request.withCredentials).toBeTrue();
    req.flush({});
  });
});
