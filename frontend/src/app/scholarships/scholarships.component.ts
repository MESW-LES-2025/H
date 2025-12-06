import { Component, OnInit, signal, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ScholarshipService } from './services/scholarship-service';
import { ScholarshipVM } from './viewmodels/scholarship-viewmodel';
import { PageRequest } from '../shared/viewmodels/pagination';

@Component({
  selector: 'app-scholarships',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './scholarships.component.html',
  styleUrls: ['./scholarships.component.css'],
})
export class ScholarshipsComponent implements OnInit, OnDestroy {
  constructor(
    private svc: ScholarshipService,
    private router: Router,
  ) {}

  q = signal<string>('');
  private searchTimeout: any = null;

  // Course type filter
  courseType = signal<string>('Any');
  courseTypeOptions = ['Any', 'BACHELOR', 'MASTER', 'DOCTORATE'];

  // Amount filter (max value)
  maxAmount = signal<number>(50000);
  maxAmountLimit = 50000;

  minAmount = signal<number>(0);
  minAmountLimit = 0;

  results = signal<ScholarshipVM[]>([]);

  // Pagination
  pageRequest: PageRequest = { page: 0, size: 10 };
  hasMorePages = signal<boolean>(false);
  isLoading = signal<boolean>(false);

  ngOnInit(): void {
    this.search();
  }

  ngOnDestroy(): void {
    if (this.searchTimeout) {
      clearTimeout(this.searchTimeout);
    }
  }

  onSearchInput(value: string): void {
    this.q.set(value);

    if (this.searchTimeout) {
      clearTimeout(this.searchTimeout);
    }

    this.searchTimeout = setTimeout(() => {
      this.search();
    }, 2000);
  }

  clearSearch(): void {
    this.q.set('');
    this.search();
  }

  onCourseTypeChange(value: string): void {
    this.courseType.set(value);
    this.search();
  }

  search(): void {
    this.pageRequest.page = 0;
    this.isLoading.set(true);
    const maxAmountValue =
      this.maxAmount() >= this.maxAmountLimit ? null : this.maxAmount();
    const minAmountValue =
      this.minAmount() <= this.minAmountLimit ? null : this.minAmount();

    this.svc
      .search(
        this.q(),
        this.courseType(),
        minAmountValue,
        maxAmountValue,
        this.pageRequest,
      )
      .subscribe((page) => {
        this.results.set(page.content);
        this.hasMorePages.set(page.number + 1 < page.totalPages);
        this.isLoading.set(false);
      });
  }

  onMinAmountChange(value: number): void {
    this.minAmount.set(value);
    this.search();
  }

  onMaxAmountChange(value: number): void {
    this.maxAmount.set(value);
    this.search();
  }

  clearFilters(): void {
    this.q.set('');
    this.courseType.set('Any');
    this.minAmount.set(this.minAmountLimit);
    this.maxAmount.set(this.maxAmountLimit);
    this.search();
  }

  loadMore(): void {
    if (this.isLoading() || !this.hasMorePages()) {
      return;
    }

    this.isLoading.set(true);
    this.pageRequest.page++;
    const maxAmountValue =
      this.maxAmount() >= this.maxAmountLimit ? null : this.maxAmount();
    const minAmountValue =
      this.minAmount() <= this.minAmountLimit ? null : this.minAmount();

    this.svc
      .search(
        this.q(),
        this.courseType(),
        minAmountValue,
        maxAmountValue,
        this.pageRequest,
      )
      .subscribe((page) => {
        this.results.set([...this.results(), ...page.content]);
        this.hasMorePages.set(page.number + 1 < page.totalPages);
        this.isLoading.set(false);
      });
  }

  goToUniversity(id: string): void {
    this.router.navigate(['/university', id]);
  }

  formatCourseType(courseType: string): string {
    if (courseType === 'BACHELOR') return 'Bachelor';
    if (courseType === 'MASTER') return 'Master';
    if (courseType === 'DOCTORATE') return 'Doctorate';
    return courseType;
  }
}
