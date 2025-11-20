import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { CollegeDTO, CollegeVM, toCollegeVM } from '../viewmodels/explore-viewmodel';

@Injectable({ providedIn: 'root' })
export class ExploreService {

  private readonly data: CollegeDTO[] = [
    {
      id: '1',
      name: 'Yale University',
      blurb: 'Pellentesque aliquam blandit in dictumst at donec...',
      photoUrl: 'https://images.unsplash.com/photo-1605470207062-b72b5cbe2a87?q=80&w=1170&auto=format&fit=crop&ixlib=rb-4.1.0',
      accent: '#4BA28F',
      country: 'USA',
      field: 'Engineering',
      degree: 'Bachelor',
      languages: ['English']
    },
    {
      id: '2',
      name: 'Cambridge University',
      blurb: 'Vestibulum ante ipsum primis in faucibus orci luctus...',
      photoUrl: 'https://images.unsplash.com/photo-1605470207062-b72b5cbe2a87?q=80&w=1170&auto=format&fit=crop&ixlib=rb-4.1.0',
      accent: '#5AA593',
      country: 'UK',
      field: 'Science',
      degree: 'Master',
      languages: ['English', 'French']
    },
    {
      id: '3',
      name: 'Universidade de Lisboa',
      blurb: 'Nulla facilisi. Ut commodo elit id pretium vehicula.',
      photoUrl: 'https://images.unsplash.com/photo-1605470207062-b72b5cbe2a87?q=80&w=1170&auto=format&fit=crop&ixlib=rb-4.1.0',
      accent: '#3F907E',
      country: 'Portugal',
      field: 'Arts',
      degree: 'PhD',
      languages: ['Portuguese', 'English']
    }
  ];


  getAvailableLanguages(): string[] {
    const set = new Set<string>();
    this.data.forEach(d => (d.languages || []).forEach(l => set.add(l)));
    return Array.from(set).sort();
  }


  search(query: string, country: string, field: string, selectedLanguages: string[] = []): Observable<CollegeVM[]> {
    const q = (query || '').toLowerCase();
    const langs = (selectedLanguages || []).map(l => (l || '').toLowerCase());

    const mapped = this.data
      .filter(dto =>
        (!q || dto.name.toLowerCase().includes(q) || dto.blurb.toLowerCase().includes(q))
      )
      .filter(dto => {
        if (!langs || langs.length === 0) return true;
        if (!dto.languages || dto.languages.length === 0) return false;
        return dto.languages.some(dl => langs.includes(dl.toLowerCase()));
      })
      .map(toCollegeVM);

    return of(mapped);
  }
}
