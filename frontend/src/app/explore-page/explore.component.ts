import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ExploreService } from './services/explore-service';
import { CollegeVM } from './viewmodels/explore-viewmodel';

@Component({
  selector: 'app-explore',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './explore.component.html',
  styleUrls: ['./explore.component.css'],
})
export class ExploreComponent implements OnInit {
  constructor(private svc: ExploreService) {}

  q = signal<string>('');

  country = signal<string>('Any');
  countries = ['Any', 'Portugal', 'Spain', 'France', 'UK', 'USA', 'Germany', 'Italy', 'Netherlands'];

  //  filtro custo de vida
  cost = signal<string>('Any');
  costOptions = ['Any', 'Low', 'Medium', 'High'];

  results = signal<CollegeVM[]>([]);

  ngOnInit(): void {
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
  languageOptions = ['Any', 'Portuguese', 'French', 'English'];

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
