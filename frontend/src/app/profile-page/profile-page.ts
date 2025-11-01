import {Component, inject, OnInit} from '@angular/core';
import {ProfilePageService} from './services/profile-page-service';
import {UserViewmodel} from './viewmodels/user-viewmodel';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-profile-page',
  imports: [],
  templateUrl: './profile-page.html',
  styleUrl: './profile-page.css',
})
export class ProfilePage implements OnInit {
  private profilePageService: ProfilePageService = inject(ProfilePageService);
  private route: ActivatedRoute = inject(ActivatedRoute);
  protected user: UserViewmodel | null = null;

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    this.profilePageService.getUserProfile(id)
      .subscribe(data => this.user = data);
  }


}
