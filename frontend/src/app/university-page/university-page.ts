import { Component, inject, OnInit } from '@angular/core';
import { UniversityPageService } from './services/university-page-service';
import { UniversityViewmodel } from './viewmodels/university-viewmodel';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import {NgbNavModule} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-university-page',
  standalone: true,
  imports: [CommonModule, NgbNavModule],
  templateUrl: './university-page.html',
  styleUrl: './university-page.css',
})
export class UniversityPage implements OnInit {
  private universityPageService: UniversityPageService = inject(UniversityPageService);
  private route: ActivatedRoute = inject(ActivatedRoute);
  private router: Router = inject(Router);
  protected university: UniversityViewmodel | null = null;
  active: number = 1;

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    this.universityPageService.getUniversityProfile(id)
      .subscribe(data => this.university = data);
  }

  goToCourse(courseId: number): void {
    this.router.navigate(['/course', courseId]);
  }
}
