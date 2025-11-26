import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {AuthService} from '../../auth/auth.service';
import {DataService} from '../../shared/services/data-service';

@Component({
  selector: 'app-edit-profile',
  imports: [],
  templateUrl: './edit-profile.html',
  styleUrl: './edit-profile.css',
})
export class EditProfile implements OnInit {
  editProfileForm: FormGroup;
  userId: number | null = null;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService,
    private dataService: DataService
  ) {
    this.editProfileForm = this.fb.group({
      name: ['', Validators.required],
      location: [''],
      jobTitle: [''],
      profileImage: ['']
    });
  }

  ngOnInit(): void {
    this.userId = this.dataService.getUserAtualId();
  }

}
