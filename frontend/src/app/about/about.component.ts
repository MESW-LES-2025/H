import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';

@Component({
  standalone: true,
  selector: 'app-about',
  imports: [CommonModule],
  templateUrl: './about.component.html',
  styleUrls: ['./about.component.css'],
})
export class AboutComponent {}
