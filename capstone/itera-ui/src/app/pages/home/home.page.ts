import { Component } from '@angular/core';
import { HeaderComponent } from '../../shared/header/header.component';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-home.page',
  imports: [HeaderComponent, RouterOutlet],
  templateUrl: './home.page.html',
  styleUrl: './home.page.css',
  standalone: true,
})
export class HomePage {

}
