import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DataService } from '../shared/services/data-service';
import { CollegeVM } from './viewmodels/explore-viewmodel';
import { Subject, takeUntil } from 'rxjs';

@Component({
  selector: 'app-explore',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './explore.component.html',
  styleUrls: ['./explore.component.css'],
})
export class ExploreComponent implements OnInit {
  q = signal<string>('');
  selectedLanguages = signal<string[]>([]);
  languagesOptions: { value: string; label: string }[] = [];


  results = signal<CollegeVM[]>([]);
  private allUniversities: any[] = []; // raw items from DataService
  private destroy$ = new Subject<void>();

  constructor(private dataService: DataService) {}

  ngOnInit(): void {
    (this.dataService as any).languages$?.pipe(takeUntil(this.destroy$)).subscribe((langs: string[]) => {
      this.languagesOptions = [{ value: 'Any', label: 'Any language' }, ...langs.map(l => ({ value: l, label: l }))];
    });


    const unis$ = (this.dataService as any).universities$ || (this.dataService as any).colleges$;
    if (unis$) {
      unis$.pipe(takeUntil(this.destroy$)).subscribe((list: any[]) => {
        this.allUniversities = list || [];
        this.applyFiltersAndSetResults();
      });
    } else {
      if (typeof (this.dataService as any).getUniversities === 'function') {
        (this.dataService as any).getUniversities().pipe(takeUntil(this.destroy$)).subscribe((list: any[]) => {
          this.allUniversities = list || [];
          this.applyFiltersAndSetResults();
        });
      }
    }
  }


  toggleLanguage(lang: string, checked: boolean) {
    const current = this.selectedLanguages();
    const next = checked ? (current.includes(lang) ? current : [...current, lang]) : current.filter(l => l !== lang);
    this.selectedLanguages.set(next);
  }

  search(): void {
    this.applyFiltersAndSetResults();
  }

  private applyFiltersAndSetResults(): void {
    const q = (this.q() || '').toLowerCase().trim();
    const selected = this.selectedLanguages().filter(l => l && l !== 'Any').map(l => l.toLowerCase());

    const filtered = this.allUniversities
      .filter(u => {
        if (!q) return true;
        const name = (u.name || '').toString().toLowerCase();
        const blurb = (u.shortDescription || u.description || u.blurb || '').toString().toLowerCase();
        return name.includes(q) || blurb.includes(q);
      })
      .filter(u => {
        if (!selected || selected.length === 0) return true;
        const uniLangs: string[] = (u.languages || u.teachingLanguages || u.languagesOffered || []).map((x: any) => (x || '').toString().toLowerCase());
        return uniLangs.some(l => selected.includes(l));
      })
      .map(u => this.mapToCollegeVM(u));

    this.results.set(filtered);
  }

  private mapToCollegeVM(u: any): CollegeVM {
    return {
      id: String(u.id ?? u.uuid ?? u._id ?? ''),
      title: u.name ?? u.title ?? '',
      blurb: u.shortDescription ?? u.description ?? u.blurb ?? '',
      photo: u.photoUrl ?? u.imageUrl ?? u.photo ?? '',
      color: u.accent ?? '#7DB19F',
      country: u.country ?? '',
      field: u.field ?? u.area ?? '',
      degree: (u.degree as any) ?? 'Bachelor',
      languages: (u.languages || u.teachingLanguages || u.languagesOffered || []).map((x: any) => String(x)),
    };
  }
}
