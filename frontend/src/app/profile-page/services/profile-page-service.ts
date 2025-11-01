import {Injectable} from '@angular/core';
import {UserViewmodel} from '../viewmodels/user-viewmodel';
import {Observable, of} from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ProfilePageService{

  private user: UserViewmodel = {
    id: 1,
    name: "João Silva",
    age: 23,
    gender: "Male",
    location: "Lisboa, Portugal",
    profileImage: "images/profile-picture-1.jpg",
    jobTitle: 'Software Engineer',
    academicHistory: [
      {
        id: "uc-001",
        startDate: "2019-09-01",
        endDate: "2022-07-15",
        course: {
          id: 101,
          name: "Engenharia Informática",
          area: "Computer Science",
          university: {
            id: 1,
            name: "Universidade de Lisboa",
            location: "Lisboa, Portugal"
          }
        }
      },
      {
        id: "uc-002",
        startDate: "2022-09-01",
        endDate: "2024-07-20",
        course: {
          id: 102,
          name: "Mestrado em Engenharia de Software",
          area: "Software Engineering",
          university: {
            id: 1,
            name: "Universidade de Lisboa",
            location: "Lisboa, Portugal"
          }
        }
      }
    ]
  };


  public getUserProfile(id: number): Observable<UserViewmodel> {
    return of(this.user);
  }
}
