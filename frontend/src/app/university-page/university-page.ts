import { Component, inject, OnInit } from '@angular/core';
import { CommonModule, DecimalPipe } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router'; 
import { NgbNavModule } from '@ng-bootstrap/ng-bootstrap';
import { UniversityPageService } from './services/university-page-service';
import { UniversityViewmodel } from './viewmodels/university-viewmodel';
import { ReviewsComponent } from './reviews/reviews.component'; 

@Component({
  selector: 'app-university-page',
  standalone: true,
  imports: [CommonModule, NgbNavModule, DecimalPipe, ReviewsComponent], 
  templateUrl: './university-page.html',
  styleUrls: ['./university-page.css'],
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
