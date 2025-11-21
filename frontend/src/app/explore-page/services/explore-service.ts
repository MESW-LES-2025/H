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
      photoUrl: 'https://images.unsplash.com/photo-1605470207062-b72b5cbe2a87?q=80&w=1170&auto=format&fit=crop',
      accent: '#4BA28F',
      country: 'USA',
      field: 'Engineering',
      degree: 'Bachelor',


      costOfLiving: 'High',
      hasScholarship: true,
      language: 'English'
    },
    {
      id: '2',
      name: 'Cambridge University',
      blurb: 'Vestibulum ante ipsum primis in faucibus orci luctus...',
      photoUrl: 'https://images.unsplash.com/photo-1605470207062-b72b5cbe2a87?q=80&w=1170&auto=format&fit=crop',
      accent: '#5AA593',
      country: 'UK',
      field: 'Science',
      degree: 'Master',

      costOfLiving: 'Medium',
      hasScholarship: false,
      language: 'French'
    },
    {
      id: '3',
      name: 'Harvard University',
      blurb: 'Nulla facilisi. Ut commodo elit id pretium vehicula.',
      photoUrl: 'https://images.unsplash.com/photo-1605470207062-b72b5cbe2a87?q=80&w=1170&auto=format&fit=crop',
      accent: '#3F907E',
      country: 'USA',
      field: 'Arts',
      degree: 'PhD',

      costOfLiving: 'Low',
      hasScholarship: true,
      language: 'Portuguese'
    }
  ];

  search(
    query: string,
    country: string,
    cost: string,
    scholarship: string,
    language: string
  ): Observable<CollegeVM[]> {

    const q = query.toLowerCase();
    const normalizedCountry = country.toLowerCase();
    const normalizedCost = cost.toLowerCase();
    const normalizedScholarship = scholarship.toLowerCase();

    const mapped = this.data
      .filter(dto => {
        // Search filter
        const matchesQuery =
          !q ||
          dto.name.toLowerCase().includes(q) ||
          dto.blurb.toLowerCase().includes(q);

        // Country filter
        const matchesCountry =
          country === 'Any' ||
          dto.country.toLowerCase() === normalizedCountry;

        // Cost of living filter
        const matchesCost =
          cost === 'Any' ||
          dto.costOfLiving.toLowerCase() === normalizedCost;

        // Scholarship filter
        let matchesScholarship = true;
        if (scholarship !== 'Any') {
          if (normalizedScholarship === 'yes') {
            matchesScholarship = dto.hasScholarship === true;
          } else if (normalizedScholarship === 'no') {
            matchesScholarship = dto.hasScholarship === false;
          }
        }

        // Language filter
        const matchesLanguage =
          language === 'Any' ||
          dto.language.toLowerCase() === language.toLowerCase();

        return (
          matchesQuery &&
          matchesCountry &&
          matchesCost &&
          matchesScholarship
          && matchesLanguage
        );
      })
      .map(toCollegeVM);

    return of(mapped);
  }

}
