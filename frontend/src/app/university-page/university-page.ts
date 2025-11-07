import { Component, inject, OnInit } from '@angular/core';
import { UniversityPageService } from './services/university-page-service';
import { UniversityViewmodel } from './viewmodels/university-viewmodel';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-university-page',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './university-page.html',
  styleUrl: './university-page.css',
})
export class UniversityPage implements OnInit {
  private universityPageService: UniversityPageService = inject(UniversityPageService);
  private route: ActivatedRoute = inject(ActivatedRoute);
  protected university: UniversityViewmodel | null = null;

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    this.universityPageService.getUniversityProfile(id)
      .subscribe(data => this.university = data);
  }
}
