import { CommonModule } from '@angular/common';
import { Component, inject, Input, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { TokenCacheService } from '../../services/token-cache.service';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { finalize } from 'rxjs';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
  selector: 'app-login-classic',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './login-classic.component.html',
  styleUrl: './login-classic.component.css',
})
export class LoginClassicComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly authService = inject(AuthService);
  private readonly tokenCache = inject(TokenCacheService);
  private readonly router = inject(Router);

  readonly form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(5)]],
    otp: [{ value: '', disabled: true }, [Validators.required, Validators.pattern(/^\d{4}$/)]],
  });

  readonly hidePassword = signal(true);
  readonly loadingFirstStep = signal(false);
  readonly loadingOtpStep = signal(false);
  readonly otpVisible = signal(false);
  readonly errorMessage = signal<string | null>(null);
  readonly successMessage = signal<string | null>(null);

  @Input({ required: true }) toogleHandler!: () => void;

  ngOnInit(): void {
    const reason = this.route.snapshot.queryParamMap.get('reason');

    if (reason === 'expired') {
      this.errorMessage.set('Tu sesión expiró. Por favor inicia sesión nuevamente.');
    } else if (reason === 'missing-session') {
      this.errorMessage.set('Debes iniciar sesión para continuar.');
    }
  }

  submitCredentials(): void {
    this.errorMessage.set(null);
    this.successMessage.set(null);

    const emailControl = this.form.controls.email;
    const passwordControl = this.form.controls.password;

    emailControl.markAsTouched();
    passwordControl.markAsTouched();

    if (emailControl.invalid || passwordControl.invalid) {
      return;
    }

    const email = emailControl.getRawValue().trim();
    const password = passwordControl.getRawValue();

    this.loadingFirstStep.set(true);

    this.authService
      .firstStepLogin(email, password)
      .subscribe({
        next: () => {
          this.otpVisible.set(true);
          this.form.controls.otp.enable();
          this.form.controls.otp.reset('');
          this.successMessage.set('Ingresa el código OTP de 4 dígitos.');
        },
        error: (err) => {
          this.otpVisible.set(false);
          this.form.controls.otp.disable();
          this.form.controls.otp.reset('');
          this.errorMessage.set(err?.error?.message ?? 'Credenciales inválidas.');
          this.loadingFirstStep.set(false);
        },
      });
  }

  submitOtp(): void {
    this.errorMessage.set(null);
    this.successMessage.set(null);

    const emailControl = this.form.controls.email;
    const passwordControl = this.form.controls.password;
    const otpControl = this.form.controls.otp;

    emailControl.markAsTouched();
    passwordControl.markAsTouched();
    otpControl.markAsTouched();

    if (emailControl.invalid || passwordControl.invalid || otpControl.invalid) {
      return;
    }

    const email = emailControl.getRawValue().trim();
    const password = passwordControl.getRawValue();
    const otp = otpControl.getRawValue();

    this.loadingOtpStep.set(true);

    this.authService
      .verifyOtp(email, password, otp)
      .pipe(finalize(() => this.loadingOtpStep.set(false)))
      .subscribe({
        next: (response) => {
          const authHeader =
            response.headers.get('Authorization') ?? response.headers.get('authorization');

          if (!authHeader) {
            this.errorMessage.set(
              'El backend respondió OK, pero no regresó el header Authorization.',
            );
            return;
          }

          // Si el backend devuelve "Bearer <jwt>", quita el prefijo.
          const token = authHeader.startsWith('Bearer ') ? authHeader.substring(7) : authHeader;

          this.tokenCache.setToken(token);
          this.successMessage.set('Autenticación exitosa. JWT guardado en caché.');
          this.router.navigateByUrl('/home/inicio');
        },
        error: (err) => {
          this.errorMessage.set(err?.error?.message ?? 'OTP inválido o expirado.');
        },
      });
  }

  onlyDigits(event: Event): void {
    const input = event.target as HTMLInputElement;
    input.value = input.value.replace(/\D/g, '').slice(0, 4);
    this.form.controls.otp.setValue(input.value, { emitEvent: false });
  }

  resetForm(): void {
    this.form.reset({
      email: '',
      password: '',
      otp: '',
    });

    this.form.controls.otp.disable();
  }

  changeToogleHandler(): void {
    this.resetForm();
    this.toogleHandler();
  }
}
