import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
})
export class HomeComponent implements OnInit {
  confirmationMessage: string | null = null;
  confirmationType: 'success' | 'error' | null = null;

  ngOnInit() {
    const msg = sessionStorage.getItem('accountDeleted');
    if (msg) {
      this.confirmationMessage = msg;
      this.confirmationType = 'success';
      sessionStorage.removeItem('accountDeleted');
      setTimeout(() => {
        this.confirmationMessage = null;
      }, 4000);
    }
  }
}
