import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ExploreService } from './services/explore-service';
import { CollegeVM } from './viewmodels/explore-viewmodel';
import { DataService } from '../shared/services/data-service';
import { PageRequest } from '../shared/viewmodels/pagination';

@Component({
  selector: 'app-explore',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './explore.component.html',
  styleUrls: ['./explore.component.css'],
})
export class ExploreComponent implements OnInit {
  constructor(
    private svc: ExploreService,
    private dataService: DataService,
    private router: Router
  ) {}

  q = signal<string>('');

  country = signal<string>('Any');
  countries = signal<string[]>(['Any']);

  cost = signal<number>(5000);
  maxCost = 5000;

  results = signal<CollegeVM[]>([]);

  scholarship = signal<string>('Any');
  scholarshipOptions = ['Any', 'Yes', 'No'];

  pageRequest: PageRequest = { page: 0, size: 3 };
  hasMorePages = signal<boolean>(false);
  isLoading = signal<boolean>(false);

  favoriteUniversityIds = signal<number[]>([]);

  ngOnInit(): void {
    this.dataService.countries$.subscribe(countries => {
      this.countries.set(['Any', ...countries]);
    });

    this.loadFavoritesAndSearch();
  }

  // ================== FAVORITOS ==================

  private loadFavoritesAndSearch(): void {
    const storedId = localStorage.getItem('userId');
    if (!storedId) {
      this.search();
      return;
    }

    this.svc.getFavorites().subscribe({
      next: resp => {
        const uniIds = resp.universities?.map(u => u.id) ?? [];
        this.favoriteUniversityIds.set(uniIds);
        this.search();
      },
      error: err => {
        console.error('Error loading favorites:', err);
        this.search();
      },
    });
  }

  private withFavoriteFlag(content: CollegeVM[]): CollegeVM[] {
    const favIds = this.favoriteUniversityIds();
    return content.map(c => ({
      ...c,
      isFavorite: favIds.includes(Number(c.id)),
    }));
  }

  // click no coracao
  onFavoriteUniversityClick(college: CollegeVM, event: MouseEvent): void {
    event.stopPropagation();

    const uniId = Number(college.id);
    if (isNaN(uniId)) return;

    if (!college.isFavorite) {
      this.svc.addFavoriteUniversity(uniId).subscribe({
        next: () => {
          college.isFavorite = true;

          const current = this.favoriteUniversityIds();
          if (!current.includes(uniId)) {
            this.favoriteUniversityIds.set([...current, uniId]);
          }
        },
        error: err => {
          console.error('Error adding favorite:', err);
        },
      });
    } else {
      this.svc.removeFavoriteUniversity(uniId).subscribe({
        next: () => {
          college.isFavorite = false;

          const updated = this.favoriteUniversityIds().filter(id => id !== uniId);
          this.favoriteUniversityIds.set(updated);
        },
        error: err => {
          console.error('Error removing favorite:', err);
        },
      });
    }
  }

  // ================== FILTROS / PESQUISA ==================

  onScholarshipChange(value: string): void {
    this.scholarship.set(value);
    this.search();
  }

  onCountryChange(value: string): void {
    this.country.set(value);
    this.search();
  }

  onCostChange(value: number): void {
    this.cost.set(value);
    this.search();
  }

  clearFilters(): void {
    this.q.set('');
    this.country.set('Any');
    this.cost.set(this.maxCost);
    this.scholarship.set('Any');
    this.search();
  }

  search(): void {
    this.pageRequest.page = 0;
    this.isLoading.set(true);
    const costMax = this.cost() >= this.maxCost ? null : this.cost();

    this.svc
      .search(
        this.q(),
        this.country(),
        costMax,
        this.scholarship(),
        this.pageRequest
      )
      .subscribe(page => {
        const contentWithFav = this.withFavoriteFlag(page.content);
        this.results.set(contentWithFav);
        this.hasMorePages.set(page.number + 1 < page.totalPages);
        this.isLoading.set(false);
      });
  }

  loadMore(): void {
    if (this.isLoading() || !this.hasMorePages()) {
      return;
    }

    this.isLoading.set(true);
    this.pageRequest.page++;
    const costMax = this.cost() >= this.maxCost ? null : this.cost();

    this.svc
      .search(
        this.q(),
        this.country(),
        costMax,
        this.scholarship(),
        this.pageRequest
      )
      .subscribe(page => {
        const extraWithFav = this.withFavoriteFlag(page.content);
        this.results.set([...this.results(), ...extraWithFav]);
        this.hasMorePages.set(page.number + 1 < page.totalPages);
        this.isLoading.set(false);
      });
  }

  goToUniversity(id: string): void {
    this.router.navigate(['/university', id]);
  }
}
