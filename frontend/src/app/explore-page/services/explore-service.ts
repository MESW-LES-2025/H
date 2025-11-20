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
      photoUrl: 'https://images.unsplash.com/photo-1605470207062-b72b5cbe2a87?q=80&w=1170&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
      accent: '#4BA28F',
      country: 'USA',
      field: 'Engineering',
      degree: 'Bachelor'
    },
    {
      id: '2',
      name: 'Cambridge University',
      blurb: 'Vestibulum ante ipsum primis in faucibus orci luctus...',
      photoUrl: 'https://images.unsplash.com/photo-1605470207062-b72b5cbe2a87?q=80&w=1170&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
      accent: '#5AA593',
      country: 'UK',
      field: 'Science',
      degree: 'Master'
    },
    {
      id: '3',
      name: 'Harvard University',
      blurb: 'Nulla facilisi. Ut commodo elit id pretium vehicula.',
      photoUrl: 'https://images.unsplash.com/photo-1605470207062-b72b5cbe2a87?q=80&w=1170&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
      accent: '#3F907E',
      country: 'USA',
      field: 'Arts',
      degree: 'PhD'
    }
  ];

  search(query: string, country: string, field: string, mode: string): Observable<CollegeVM[]> {
    const q = query.toLowerCase();
    const normalizedCountry = country.toLowerCase();

    const mapped = this.data
      .filter(dto => {
        const matchesQuery =
          !q ||
          dto.name.toLowerCase().includes(q) ||
          dto.blurb.toLowerCase().includes(q);

        const matchesCountry =
          country === 'Any' ||
          dto.country.toLowerCase() === normalizedCountry;

        return matchesQuery && matchesCountry;
      })
      .map(toCollegeVM);

    return of(mapped);
  }

}
