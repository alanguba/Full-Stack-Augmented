import { Component } from '@angular/core';
import { LoginClassicComponent } from '../../components/login-classic/login-classic.component';
import { MatCardModule } from '@angular/material/card';
import { LoginForgotPasswordComponent } from '../../components/login-forgot-password/login-forgot-password.component';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [MatCardModule, LoginClassicComponent, LoginForgotPasswordComponent],
  templateUrl: './login.page.html',
  styleUrl: './login.page.css',
})
export class LoginPage {
  toogleHandler: boolean = false;

  toggle = (): void => {
    this.toogleHandler = !this.toogleHandler;
  };
}
