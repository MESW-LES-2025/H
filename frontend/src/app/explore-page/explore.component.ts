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

  // opções disponíveis
  countries = ['Any', 'USA', 'UK'];

  results = signal<CollegeVM[]>([]);

  ngOnInit(): void {
    this.search();
  }

  search(): void {
    this.svc
      .search(this.q(), this.country(), 'Any field', 'Any')
      .subscribe(list => {
        this.results.set(list);
      });
  }

  onCountryChange(value: string): void {
    this.country.set(value);
    this.search();
  }
}
