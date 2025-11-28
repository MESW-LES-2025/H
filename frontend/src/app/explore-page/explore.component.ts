import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ExploreService } from './services/explore-service';
import { CollegeVM } from './viewmodels/explore-viewmodel';
import { DataService } from '../shared/services/data-service';

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

  //  filtro custo de vida
  cost = signal<string>('Any');
  costOptions = ['Any', 'Low', 'Medium', 'High'];

  results = signal<CollegeVM[]>([]);

  ngOnInit(): void {
    this.dataService.countries$.subscribe(countries => {
      this.countries.set(['Any', ...countries]);
    });

    this.dataService.languages$.subscribe(languages => {
      this.languageOptions.set(['Any', ...languages]);
    });

    this.search();
  }

  //  filtro de scholarships
  scholarship = signal<string>('Any');
  scholarshipOptions = ['Any', 'Yes', 'No'];

  onScholarshipChange(value: string): void {
    this.scholarship.set(value);
    this.search();
  }

  language = signal<string>('Any');
  languageOptions = signal<string[]>(['Any']);

  onLanguageChange(value: string): void {
    this.language.set(value);
    this.search();
  }

  search(): void {
    this.svc
      .search(
        this.q(),
        this.country(),
        this.cost(),
        this.scholarship(),
        this.language()
      )
      .subscribe(list => this.results.set(list));
  }

  onCountryChange(value: string): void {
    this.country.set(value);
    this.search();
  }

  onCostChange(value: string): void {
    this.cost.set(value);
    this.search();
  }

  clearFilters(): void {
    this.country.set('Any');
    this.cost.set('Any');
    this.scholarship.set('Any');
    this.language.set('Any');
    this.search();
  }

}
