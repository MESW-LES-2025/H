import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
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
  constructor(private svc: ExploreService, private dataService: DataService) {}

  q = signal<string>('');

  country = signal<string>('Any');
  countries = signal<string[]>(['Any']);

  // Cost of living filter (max value)
  cost = signal<number>(5000);
  maxCost = 5000;

  results = signal<CollegeVM[]>([]);

  // Scholarship filter
  scholarship = signal<string>('Any');
  scholarshipOptions = ['Any', 'Yes', 'No'];

  // Pagination
  pageRequest: PageRequest = { page: 0, size: 3 }; //TODO: correct pagination

  ngOnInit(): void {
    this.dataService.countries$.subscribe(countries => {
      this.countries.set(['Any', ...countries]);
    });

    this.search();
  }

  onScholarshipChange(value: string): void {
    this.scholarship.set(value);
    this.search();
  }

  search(): void {
    const costMax = this.cost() >= this.maxCost ? null : this.cost();

    this.svc
      .search(
        this.q(),
        this.country(),
        costMax,
        this.scholarship(),
        this.pageRequest
      )
      .subscribe(list => this.results.set(list));
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
}
